package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.TrackLikes;
import com.github.vaerys.objects.XRequestBuffer;
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

    public static void pinMessage(CommandObject command) {
        List<IChannel> channelIDS = command.guild.config.getChannelsByType(Command.CHANNEL_ART, command.guild);
        List<TrackLikes> likes = command.guild.channelData.getLikes();
        List<Long> pins = command.guild.channelData.getPinnedMessages();

        //exit if not pinning art
        if (!command.guild.config.artPinning) return;
        //exit if message owner is a bot
        if (command.message.get().getAuthor().isBot()) return;
        //exit if user has art pinning denied.
        ProfileObject profile = command.user.getProfile(command.guild);
        if (profile != null && profile.getSettings().contains(UserSetting.DENY_ART_PINNING)) return;
        //exit if there is no art channel
        if (channelIDS.size() == 0) return;
        //exit if this is not the art channel
        if (channelIDS.get(0).getLongID() != command.channel.longID) return;

        if (command.message.get().getAttachments().size() != 0) {
            if (Utility.isImageLink(command.message.get().getAttachments().get(0).getUrl())) {
                try {
                    //pin message
                    command.channel.get().pin(command.message.get());
                    //add to pins
                    pins.add(command.message.longID);
                    //add pin response
                    RequestBuffer.request(() -> command.message.get().addReaction(Utility.getReaction("pushpin")));
                    //if likeart
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
        } else {
            for (String nl : command.message.get().getContent().split("/n")) {
                for (String s : nl.split(" ")) {
                    if (Utility.isImageLink(s) || isHostingWebsite(s)) {
                        try {
                            //pin message
                            command.message.get().getChannel().pin(command.message.get());
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
                            //do nothing
                        }
                    }
                }
            }
            return;
        }
    }

    private static void checkList(CommandObject command) {
        ArrayList<Long> pinnedMessages = command.guild.channelData.getPinnedMessages();
        List<TrackLikes> likes = command.guild.channelData.getLikes();

        ListIterator iterator = pinnedMessages.listIterator();

        //remove null or non pinned items
        try {
            while (iterator.hasNext()) {
                Long id = (Long) iterator.next();
                IMessage pinned = command.channel.get().getMessageByID(id);
                if (pinned == null) iterator.remove();
                else if (!pinned.isPinned()) iterator.remove();
            }
        }catch (ConcurrentModificationException e){
            //skip, this happens if hearts are added too quickly
        }


        //keep list at 25 or less
        int tries = 0;
        List<IMessage> pins = command.channel.get().getPinnedMessages();
        List<IMessage> markedForUnpin = new ArrayList<>();
        while (pinnedMessages.size() > 26 && tries < 25) {
            for (IMessage p : pins) {
                if (p.getLongID() == pinnedMessages.get(0)) {
                    markedForUnpin.add(p);
                    pinnedMessages.remove(0);
                }
            }
            tries++;
        }
        for (IMessage m : markedForUnpin) {
            RequestBuffer.request(() -> {
                if (m.isPinned()) {
                    unPin(m, command);
                }
            });
        }
        iterator = likes.listIterator();
        while (iterator.hasNext()) {
            TrackLikes like = (TrackLikes) iterator.next();
            if (!pinnedMessages.contains(like.getMessageID())) {
                iterator.remove();
            }
        }
    }

    private static void unPin(IMessage message, CommandObject command) {
        if (message.isPinned()) {
            XRequestBuffer.request(() -> command.channel.get().unpin(message));
        }
    }

    public static boolean isHostingWebsite(String word) {
        boolean hostingWebsite = false;
        if (word.toLowerCase().toLowerCase().contains("https://gfycat.com/")) hostingWebsite = true;
        return hostingWebsite;
    }

    public static void pinLiked(CommandObject command) {
        List<IChannel> channelIDS = command.guild.config.getChannelsByType(Command.CHANNEL_ART, command.guild);
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

        checkList(command);

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
