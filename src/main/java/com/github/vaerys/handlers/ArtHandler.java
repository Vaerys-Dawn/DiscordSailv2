package com.github.vaerys.handlers;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.botlevel.TrackLikes;
import com.github.vaerys.objects.userlevel.ProfileObject;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        TextChannel channel = command.guild.getChannelByType(ChannelSetting.ART);
        List<Long> pins = command.guild.channelData.getPinnedMessages();
        //exit if messageChannel is wrong
        if (channel == null || !command.guildChannel.get().equals(channel)) return;
        //exit if message isn't pinned
        if (!command.message.get().isPinned()) return;
        for (long l : pins) {
            if (command.message.longID == l && command.message.author.longID == command.user.longID) {
                channel.unpinMessageById(command.message.longID).queue();
                command.message.get().addReaction(Constants.EMOJI_REMOVE_PIN).queue();
                MessageReaction reaction = command.message.getReactionByName(Constants.EMOJI_ADD_PIN);
                for (User user : reaction.retrieveUsers().complete()) {
                    command.message.get().removeReaction(reaction.getReactionEmote().getEmote(), user).queue();
                }
                checkList(command);
                return;
            }
        }
    }

    public static void pinMessage(CommandObject command, UserObject reacted, UserObject owner) {
        TextChannel channelIDS = command.guild.getChannelByType(ChannelSetting.ART);
        List<TrackLikes> likes = command.guild.channelData.getLikes();
        List<Long> pins = command.guild.channelData.getPinnedMessages();

        //exit if not pinning art
        if (!command.guild.config.artPinning) return;
        //exit if the art is already pinned
        if (command.message.get().isPinned()) return;
        //exit if message owner is a bot
        if (owner.get().isBot()) return;
        //exit if message has already been unpinned.
        MessageReaction reaction = command.message.getReactionByName(Constants.EMOJI_REMOVE_PIN);
        if (reaction != null && reaction.retrieveUsers().stream().anyMatch(c -> c == command.client.bot.get())) {
            command.message.get().removeReaction(Constants.EMOJI_ADD_PIN, reacted.get()).queue();
            return;
        }
        //exit if globalUser has art pinning denied.
        ProfileObject profile = reacted.getProfile();
        if (profile != null && profile.getSettings().contains(UserSetting.DENY_ART_PINNING)) return;
        //exit if there is no art messageChannel
        if (channelIDS == null) return;
        //exit if this is not the art messageChannel
        if (channelIDS.getIdLong() != command.guildChannel.longID) return;
        //exit if there is no art to be found
        if (!checkAttachments(command) && !checkMessage(command)) return;

        //pin message
        command.guildChannel.get().pinMessageById(command.message.longID).queue();

        //debug builder
        String name;
        if (checkAttachments(command)) {
            name = "ATTACHMENT_PIN";
        } else {
            name = "MESSAGE_PIN";
        }
        String args;
        args = command.message.getContent();
        for (Message.Attachment a : command.message.getAttachments()) {
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
        command.message.get().addReaction(Constants.EMOJI_ADD_PIN).queue();
        //if like art

        if (command.guild.config.likeArt && command.guild.config.modulePixels) {
            //add heart
            command.message.get().addReaction(Constants.EMOJI_LIKE_PIN).queue();
            //add to list
            likes.add(new TrackLikes(command.message.longID));
        }

        String response;
        if (command.guild.config.autoArtPinning && reacted.longID == command.client.bot.longID) {
            response = "\\> I have pinned **" + owner.displayName + "'s** art.";
        } else {
            if (owner.longID == reacted.longID) {
                response = "\\> **" + reacted.displayName + "** Has pinned their";
            } else {
                response = "\\> **" + reacted.displayName + "** Has pinned **" + owner.displayName + "'s**";
            }
            response += " art by reacting with the \uD83D\uDCCC emoji.";
        }
        if (command.guild.config.likeArt && command.guild.config.modulePixels) {
            response += "\nYou can now react with a \u2764 emoji to give the user some pixels.";
        }
        Message pinResponse = command.guildChannel.sendMessage(response);
        Thread thread = new Thread(() -> {
            try {
                logger.trace("Deleting in 2 minutes.");
                Thread.sleep(2 * 60 * 1000);
                pinResponse.delete().queue();
            } catch (InterruptedException e) {
                // do nothing
            }
        });
        checkList(command);
        thread.start();
        return;
    }

    private static boolean checkAttachments(CommandObject command) {
        for (Message.Attachment a : command.message.getAttachments()) {
            if (Utility.isImageLink(a.getUrl()))
                return true;
        }
        return false;
    }

    private static boolean checkMessage(CommandObject command) {
        for (String s : command.message.get().getContentRaw().split("( |\n)")) {
            if (Utility.isImageLink(s) || isSoundFile(s) || isHostingWebsite(s)) return true;
        }
        return false;
    }

    private static boolean isSoundFile(String s) {
        return s.matches("(?i).*(.mp3|.wav|.ogg)");
    }

    private static void checkList(CommandObject command) {
        List<Long> pinnedMessages = command.guild.channelData.getPinnedMessages();
        List<TrackLikes> likes = command.guild.channelData.getLikes();
        List<Message> channelPins = command.guildChannel.get().retrievePinnedMessages().complete();
        List<Message> markedForUnpin = new LinkedList<>();
        int pinLimit = command.guild.config.pinLimit;
        int tries = 0;

        ListIterator iterator = pinnedMessages.listIterator();
        List<Long> channelPinsLong = channelPins.stream().map(iMessage -> iMessage.getIdLong()).collect(Collectors.toList());
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
            for (Message p : channelPins) {
                if (pinnedMessages.contains(p.getIdLong()) && pinnedMessages.get(0) == p.getIdLong()) {
                    //adds the pin to the messages to be unpinned
                    markedForUnpin.add(p);
                    removePin(p, pinnedMessages);
                }
            }
            tries++;
        }

        for (Message message : markedForUnpin) {
            if (message.isPinned()) {
                command.guildChannel.get().unpinMessageById(message.getId()).queue();
                command.guild.sendDebugLog(command.setMessage(message), "ART_PINNED", "UNPIN", "PIN TOTAL = " +
                        command.guildChannel.getPinCount() + "/" + command.guild.config.pinLimit);
            }
        }


        iterator = likes.listIterator();
        while (iterator.hasNext()) {
            TrackLikes like = (TrackLikes) iterator.next();
            if (!pinnedMessages.contains(like.getMessageID())) {
                iterator.remove();
            }
        }
    }

    private static void removePin(Message p, List<Long> pins) {
        ListIterator iterator = pins.listIterator();
        while (iterator.hasNext()) {
            long id = (long) iterator.next();
            if (id == p.getIdLong()) {
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
     * Handles the granting of xp to globalUser's when their art has a :heart: reaction applied to their
     * message that was pinned with the artPinning module.
     *
     * @param command Used to parse in the variables needed to access the guild, messageChannel, message,
     *                and globalUser objects. these objects allows access to the api.
     * @param reacted the globalUser that added a reaction.
     * @param owner   the globalUser that owns the message.
     */
    public static void pinLiked(CommandObject command, UserObject reacted, UserObject owner) {
        List<TextChannel> channelIDS = command.guild.getChannelsByType(ChannelSetting.ART);
        //exit if not pinning art
        if (!command.guild.config.artPinning) return;
        //exit if pixels is off
        if (!command.guild.config.modulePixels) return;
        //exit if xp gain is off
        if (!command.guild.config.xpGain) return;
        //exit if liking art is off
        if (!command.guild.config.likeArt) return;
        //exit if message owner is a bot
        if (owner.get().isBot()) return;
        //exit if there is no art messageChannel
        if (channelIDS.size() == 0) return;
        //exit if this is not the art messageChannel
        if (channelIDS.get(0).getIdLong() != command.guildChannel.longID) return;
        //you cant give yourself pixels via your own art
        if (owner.longID == reacted.longID) return;

        //exit if not pinned art
        if (!command.guild.channelData.getPinnedMessages().contains(command.message.longID)) return;

        TrackLikes messageLikes = command.guild.channelData.getLiked(command.message.longID);
        //some thing weird happened if this is null unless its a legacy pin
        if (messageLikes == null) return;
        //cant like the same thing more than once
        if (messageLikes.getUsers().contains(reacted.longID)) return;

        ProfileObject profile = owner.getProfile();

        //exit if profile doesn't exist
        if (profile == null) return;
        //exit if the globalUser should not gain pixels
        if (profile.getSettings().contains(UserSetting.NO_XP_GAIN) ||
                profile.getSettings().contains(UserSetting.DENIED_XP)) return;

        logger.trace(reacted.displayName + " just gave " + owner.displayName + " some pixels for liking their art.");
        //grant globalUser xp for their nice art.
        profile.addXP(5, command.guild.config);
        messageLikes.getUsers().add(reacted.longID);
    }
}
