package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.TrackLikes;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

import java.util.*;
import java.util.stream.Collectors;

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

    public static void unPin(CommandObject command) {
        IChannel channel = command.guild.getChannelByType(ChannelSetting.ART);
        List<Long> pins = command.guild.channelData.getPinnedMessages();
        //exit if channel is wrong
        if (channel == null || !command.channel.get().equals(channel)) return;
        //exit if message isn't pinned
        if (!command.message.get().isPinned()) return;
        for (long l : pins) {
            if (command.message.longID == l && command.message.author.longID == command.user.longID) {
                RequestBuffer.request(() -> channel.unpin(command.message.get()));
                RequestBuffer.request(() -> command.message.get().addReaction(Utility.getReaction(Constants.EMOJI_REMOVE_PIN)));
                IReaction reaction = command.message.getReationByName(Constants.EMOJI_ADD_PIN);
                for (IUser user : reaction.getUsers()) {
                    RequestBuffer.request(() -> command.message.get().removeReaction(user, reaction));
                }
                checkList(command);
                return;
            }
        }
    }

    public static void pinMessage(CommandObject command) {
        IChannel channelIDS = command.guild.getChannelByType(ChannelSetting.ART);
        List<TrackLikes> likes = command.guild.channelData.getLikes();
        List<Long> pins = command.guild.channelData.getPinnedMessages();

        //exit if not pinning art
        if (!command.guild.config.artPinning) return;
        //exit if the art is already pinned
        if (command.message.get().isPinned()) return;
        //exit if message owner is a bot
        if (command.message.get().getAuthor().isBot()) return;
        //exit if message has already been unpinned.
        IReaction reaction = command.message.getReationByName(Constants.EMOJI_REMOVE_PIN);
        if (reaction != null && reaction.getUserReacted(command.client.bot.get())) {
            RequestBuffer.request(() ->
                    command.message.get().removeReaction(command.user.get(), Utility.getReaction(Constants.EMOJI_ADD_PIN))).get();
            return;
        }
        //exit if user has art pinning denied.
        ProfileObject profile = command.user.getProfile(command.guild);
        if (profile != null && profile.getSettings().contains(UserSetting.DENY_ART_PINNING)) return;
        //exit if there is no art channel
        if (channelIDS == null) return;
        //exit if this is not the art channel
        if (channelIDS.getLongID() != command.channel.longID) return;
        //exit if there is no art to be found
        if (!checkAttachments(command) && !checkMessage(command)) return;

        try {
            //pin message
            RequestBuffer.request(() -> command.channel.get().pin(command.message.get())).get();

            //debug builder
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

            command.guild.sendDebugLog(command, "ART_PINNED", name, args);
            //end debug

            //add to ping
            pins.add(command.message.longID);
            //add pin response
            RequestBuffer.request(() -> command.message.get().addReaction(Utility.getReaction(Constants.EMOJI_ADD_PIN)));
            //if like art
            if (command.guild.config.likeArt && command.guild.config.modulePixels) {
                //add heart
                RequestBuffer.request(() -> command.message.get().addReaction(Utility.getReaction(Constants.EMOJI_LIKE_PIN)));
                //add to list
                likes.add(new TrackLikes(command.message.longID));
            }
            checkList(command);
            String response;
            if (!command.guild.config.autoArtPinning) {
                if (command.user.longID == command.message.author.longID) {
                    response = "> **" + command.user.displayName + "** Has pinned their art by reacting with the \uD83D\uDCCC emoji.";
                } else {
                    response = "> **" + command.user.displayName + "** Has pinned **" + command.message.author.displayName + "'s** art by reacting with the \uD83D\uDCCC emoji.";
                }
            } else {
                response = "> I have pinned **" + command.message.author.displayName + "'s** art.";
            }
            if (command.guild.config.likeArt && command.guild.config.modulePixels) {
                response += "\n You can now react with a ❤ emoji to give the user some pixels.";
            }
            IMessage pinResponse = RequestHandler.sendMessage(response, command.channel).get();
            Thread thread = new Thread(() -> {
                try {
                    logger.trace("Deleting in 2 minutes.");
                    Thread.sleep(2 * 60 * 1000);
                    RequestHandler.deleteMessage(pinResponse);
                } catch (InterruptedException e) {
                    // do nothing
                }
            });
            thread.start();
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
        List<IMessage> channelpins = RequestBuffer.request(() -> {
            return command.channel.get().getPinnedMessages();
        }).get();
        List<IMessage> markedForUnpin = new LinkedList<>();
        int pinLimit = command.guild.config.pinLimit;
        int tries = 0;

        ListIterator iterator = pinnedMessages.listIterator();
        List<Long> channelPinsLong = channelpins.stream().map(iMessage -> iMessage.getLongID()).collect(Collectors.toList());
        try {
            while (iterator.hasNext()) {
                long item = (long) iterator.next();
                if (!channelPinsLong.contains(item)) {
                    iterator.remove();
                }
            }
        } catch (ConcurrentModificationException e) {
            return;
        }

        while (pinnedMessages.size() > pinLimit && tries < 50) {
            for (IMessage p : channelpins) {
                if (pinnedMessages.contains(p.getLongID()) && pinnedMessages.get(0) == p.getLongID()) {
                    //adds the pin to the messages to be unpinned
                    markedForUnpin.add(p);
                    removePin(p, pinnedMessages);
                }
            }
            tries++;
        }

        tries = 0;
        for (IMessage message : markedForUnpin) {
            try {
                if (message.isPinned()) {
                    RequestBuffer.request(() -> command.channel.get().unpin(message)).get();
                    command.guild.sendDebugLog(command.setMessage(message), "ART_PINNED", "UNPIN", "PIN TOTAL = " +
                            command.channel.getPinCount() + "/" + command.guild.config.pinLimit);
                }
            } catch (DiscordException e) {
                if (!e.getMessage().contains("Message is not pinned!")) {
                    throw (e);
                }
            }
        }
        tries++;


        iterator = likes.listIterator();
        while (iterator.hasNext()) {
            TrackLikes like = (TrackLikes) iterator.next();
            if (!pinnedMessages.contains(like.getMessageID())) {
                iterator.remove();
            }
        }

    }

//    private static void checkList(CommandObject command) {
//        List<Long> pinnedMessages = command.guild.channelData.getPinnedMessages();
//        List<TrackLikes> likes = command.guild.channelData.getLikes();
//        List<IMessage> pins = RequestBuffer.request(() -> {
//            return command.channel.get().getPinnedMessages();
//        }).get();
//        List<IMessage> markedForUnpin = new ArrayList<>();
//        int tries = 0;
//
//
//        ListIterator iterator = pinnedMessages.listIterator();
//        try {
//            while (iterator.hasNext()) {
//                Long id = (Long) iterator.next();
//                IMessage pin = command.channel.get().getMessageByID(id);
//                if (pin == null) {
//                    pin = RequestBuffer.request(() -> {
//                        return command.channel.get().fetchMessage(id);
//                    }).get();
//                }
//                if (pin == null) iterator.remove();
//                else if (!pin.isPinned()) iterator.remove();
//            }
//        } catch (ConcurrentModificationException e) {
//            //skip, this happens if hearts are added too quickly
//        }
//
//        int pinLimit = command.guild.config.pinLimit;
//
//        while (pinnedMessages.size() > pinLimit && tries < 50) {
//            for (IMessage p : pins) {
//                if (pinnedMessages.contains(p.getLongID()) && pinnedMessages.get(0) == p.getLongID()) {
//                    //adds the pin to the messages to be unpinned
//                    markedForUnpin.add(p);
//                    removePin(p, pinnedMessages);
//                }
//            }
//            tries++;
//        }
//        tries = 0;
//        boolean flag = markedForUnpin.size() != 0;
////        while (flag && tries < 20) {
//            int expectedSize = command.channel.getPinCount();
//            for (IMessage message : markedForUnpin) {
//                try {
//                    if (message.isPinned()) {
//                        RequestBuffer.request(() -> command.channel.get().unpin(message)).get();
//                        logger.debug(Utility.loggingFormatter(command.setMessage(message), "ART_PINNED", "UNPIN", "PIN TOTAL = " + command.channel.getPinCount()));
//                        expectedSize--;
//                    }
//                } catch (DiscordException e) {
//                    if (!e.getMessage().contains("Message is not pinned!")) {
//                        throw (e);
//                    }
//                }
////                List<IMessage> pinned = RequestBuffer.request(() -> {
////                    return command.channel.get().getPinnedMessages();
////                }).get();
////                int size = pinned.size();
////                if (size > expectedSize) {
////                    flag = true;
////                }
//            }
//            tries++;
////        }
//
//        iterator = likes.listIterator();
//        while (iterator.hasNext()) {
//            TrackLikes like = (TrackLikes) iterator.next();
//            if (!pinnedMessages.contains(like.getMessageID())) {
//                iterator.remove();
//            }
//        }
//    }

    private static void removePin(IMessage p, List<Long> pins) {
        ListIterator iterator = pins.listIterator();
        while (iterator.hasNext()) {
            long id = (long) iterator.next();
            if (id == p.getLongID()) {
                iterator.remove();
            }
        }
    }


    /**
     * handles detection of hosting websites within link values.
     *
     * @param word String that may contain a URL of a hosting website.
     * @return if param word is considered a hosting website defined by values in @hosts then return true
     * else return false.
     */
    public static boolean isHostingWebsite(String word) {
        for (String s : hosts) {
            if (word.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the granting of xp to user's when their art has a :heart: reaction applied to their
     * message that was pinned with the artPinning module.
     *
     * @param command Used to parse in the variables needed to access the guild, channel, message,
     *                and user objects. these objects allows access to the api.
     */
    public static void pinLiked(CommandObject command) {
        List<IChannel> channelIDS = command.guild.getChannelsByType(ChannelSetting.ART);
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

        //exit if not pinned art
        if (!command.guild.channelData.getPinnedMessages().contains(command.message.longID)) return;

        TrackLikes messageLikes = command.guild.channelData.getLiked(command.message.longID);
        //some thing weird happened if this is null unless its a legacy pin
        if (messageLikes == null) return;
        //cant like the same thing more than once
        if (messageLikes.getUsers().contains(command.user.longID)) return;

        UserObject user = new UserObject(command.message.get().getAuthor(), command.guild);
        ProfileObject profile = user.getProfile(command.guild);

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
