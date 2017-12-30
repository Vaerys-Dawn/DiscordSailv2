package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.TrackLikes;
import com.github.vaerys.templates.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Vaerys on 12/05/2017.
 */
public class ArtHandler {

    final static Logger logger = LoggerFactory.getLogger(ArtHandler.class);

    private static final List<String> hosts = new ArrayList<String>() {{
        add("https://gfycat.com/");
        add("https://imgur.com/");
        add("https://gyazo.com/");
        add("https://steamuserimages-a.akamaihd.net/");
        add("http://fav.me/");
    }};

    public static void pinMessage(CommandObject command) {
        List<IChannel> channelIDS = command.guild.getChannelsByType(Command.CHANNEL_ART);
        List<TrackLikes> likes = command.guild.channelData.getLikes();
        List<Long> pins = command.guild.channelData.getPinnedMessages();

        //exit if not pinning art
        if (!command.guild.config.artPinning) return;
        //checks the list if something seems off
        if (pins.size() > command.guild.config.pinLimit) checkList(command);
        //exit if the art is already pinned
        if (command.message.get().isPinned()) return;
        //exit if message owner is a bot
        if (command.message.get().getAuthor().isBot()) return;
        //exit if user has art pinning denied.
        ProfileObject profile = command.user.getProfile(command.guild);
        if (profile != null && profile.getSettings().contains(UserSetting.DENY_ART_PINNING)) return;
        //exit if there is no art channel
        if (channelIDS.size() == 0) return;
        //exit if this is not the art channel
        if (channelIDS.get(0).getLongID() != command.channel.longID) return;
        //exit if there is no art to be found
        if (!checkAttachments(command) && !checkMessage(command)) return;

        try {
            //pin message
            RequestBuffer.request(() -> command.channel.get().pin(command.message.get())).get();

            String name;
            if (checkAttachments(command)) {
                name = "ATTACHMENT_PIN";
            } else {
                name = "MESSAGE_PIN";
            }
            String args;
            args = command.message.getContent();
            for (IMessage.Attachment a : command.message.getAttachments()) {
                args += " <" + a.getUrl() + ">";
            }
            if (command.message.getContent() == null || command.message.getContent().isEmpty()) {
                args = args.replace("  ", "");
            }

            logger.debug(Utility.loggingFormatter(command, "ART_PINNED", name, args));
            //add to ping
            pins.add(command.message.longID);
            //add pin response
            RequestBuffer.request(() -> command.message.get().addReaction(Utility.getReaction("pushpin")));
            //if like art
            if (command.guild.config.likeArt) {
                //add heart
                RequestBuffer.request(() -> command.message.get().addReaction(Utility.getReaction("heart")));
                //add to list
                likes.add(new TrackLikes(command.message.longID));
            }
            checkList(command);
            return;
        } catch (DiscordException e) {
            if (e.getErrorMessage().contains("already pinned")) {
                return;
            } else {
                Utility.sendStack(e);
            }
        }
    }

    private static boolean checkAttachments(CommandObject command) {
        for (IMessage.Attachment a : command.message.getAttachments()) {
            if (Utility.isImageLink(a.getUrl()))
                return true;
        }
        return false;
    }

    private static boolean checkMessage(CommandObject command) {
        for (String s : command.message.get().getContent().split("( |\n)")) {
            if (Utility.isImageLink(s) || isHostingWebsite(s)) return true;
        }
        return false;
    }

    private static void checkList(CommandObject command) {
        List<Long> pinnedMessages = command.guild.channelData.getPinnedMessages();
        List<TrackLikes> likes = command.guild.channelData.getLikes();
        List<IMessage> pins = RequestBuffer.request(() -> {
            return command.channel.get().getPinnedMessages();
        }).get();
        List<IMessage> markedForUnpin = new ArrayList<>();
        int tries = 0;


        ListIterator iterator = pinnedMessages.listIterator();
        try {
            while (iterator.hasNext()) {
                Long id = (Long) iterator.next();
                IMessage pin = command.channel.get().getMessageByID(id);
                if (pin == null) iterator.remove();
                else if (!pin.isPinned()) iterator.remove();
            }
        } catch (ConcurrentModificationException e) {
            //skip, this happens if hearts are added too quickly
        }

        int pinLimit = command.guild.config.pinLimit;

        while (pinnedMessages.size() > pinLimit && tries < 50) {
            for (IMessage p : pins) {
                if (pinnedMessages.contains(p.getLongID()) && pinnedMessages.get(0) == p.getLongID()) {
                    //adds the pin to the messages to be unpinned
                    markedForUnpin.add(p);
                    removePin(p, pinnedMessages);
                }
            }
            tries++;
        }
        tries = 0;
        boolean flag = markedForUnpin.size() != 0;
        while (flag && tries < 20) {
            int expectedSize = RequestBuffer.request(() -> {
                return command.channel.get().getPinnedMessages().size();
            }).get();
            for (IMessage message : markedForUnpin) {
                try {
                    if (message.isPinned()) {
                        RequestBuffer.request(() -> command.channel.get().unpin(message));
                        expectedSize--;
                    }

                } catch (DiscordException e) {
                    if (!e.getMessage().contains("Message is not pinned!")) {
                        throw (e);
                    }
                }
                List<IMessage> pinned = RequestBuffer.request(() -> {
                    return command.channel.get().getPinnedMessages();
                }).get();
                int size = pinned.size();
                if (size > expectedSize) {
                    flag = true;
                }
            }
            tries++;
        }

        iterator = likes.listIterator();
        while (iterator.hasNext()) {
            TrackLikes like = (TrackLikes) iterator.next();
            if (!pinnedMessages.contains(like.getMessageID())) {
                iterator.remove();
            }
        }
    }

    private static void removePin(IMessage p, List<Long> pins) {
        ListIterator iterator = pins.listIterator();
        while (iterator.hasNext()) {
            long id = (long) iterator.next();
            if (id == p.getLongID()) {
                iterator.remove();
            }
        }
    }

    public static boolean isHostingWebsite(String word) {
        for (String s : hosts) {
            if (word.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static void pinLiked(CommandObject command) {
        List<IChannel> channelIDS = command.guild.getChannelsByType(Command.CHANNEL_ART);
        //exit if not pinning art
        if (!command.guild.config.artPinning) return;
        //exit if pixels is off
        if (!command.guild.config.modulePixels) return;
        //exit if xp gain is off
        if (!command.guild.config.xpGain) return;
        //exit if liking art is off
        if (!command.guild.config.likeArt) return;
        //exit if message owner is a bot
        if (command.message.get().getAuthor().isBot()) return;
        //exit if there is no art channel
        if (channelIDS.size() == 0) return;
        //exit if this is not the art channel
        if (channelIDS.get(0).getLongID() != command.channel.longID) return;
        //you cant give yourself pixels via your own art
        if (command.message.get().getAuthor().getLongID() == command.user.longID) return;

//        checkList(command);

        //exit if not pinned art
        if (!command.guild.channelData.getPinnedMessages().contains(command.message.longID)) return;

        TrackLikes messageLikes = command.guild.channelData.getLiked(command.message.longID);
        //some thing weird happened if this is null unless its a legacy pin
        if (messageLikes == null) return;
        //cant like the same thing more than once
        if (messageLikes.getUsers().contains(command.user.longID)) return;

        UserObject user = new UserObject(command.message.get().getAuthor(), command.guild);
        ProfileObject profile = user.getProfile(command.guild);
        //exit if user is null
        if (user == null) return;
        //exit if profile doesn't exist
        if (profile == null) return;
        //exit if the user should not gain pixels
        if (profile.getSettings().contains(UserSetting.NO_XP_GAIN) ||
                profile.getSettings().contains(UserSetting.DENIED_XP)) return;

        logger.trace(command.user.displayName + " just gave " + user.displayName + " some pixels for liking their art.");
        //grant user xp for their nice art.
        profile.addXP(5, command.guild.config);
        messageLikes.getUsers().add(command.user.longID);
    }
}
