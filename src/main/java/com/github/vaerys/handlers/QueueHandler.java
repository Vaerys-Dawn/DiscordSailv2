package com.github.vaerys.handlers;

import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.botlevel.QueueObject;
import com.github.vaerys.objects.userlevel.DailyMessage;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Vaerys on 19/07/2017.
 */
public class QueueHandler {

    private static String dowString = "Day of week";
    private static String uIDString = "UID";
    private static boolean isChecking = false;

    public static void addToQueue(CommandObject object, String content, DayOfWeek dayOfWeek, String type) {
        runCheck();
        MessageReaction.ReactionEmote thumbsUp = Utility.getReaction(Constants.EMOJI_THUMBS_UP);
        MessageReaction.ReactionEmote thumbsDown = Utility.getReaction(Constants.EMOJI_THUMBS_DOWN);
        TextChannel channel = object.client.get().getTextChannelById(Globals.queueChannelID);

        if (channel != null) {
            switch (type) {
                case Constants.QUEUE_DAILY:
                    long uID = Globals.getDailyMessages().newDailyMsgUID();
                    if (uID == -1) {
                        object.client.creator.queueDm("\\> Max limit of Daily messages hit.");
                        break;
                    }
                    XEmbedBuilder embedBuilder = new XEmbedBuilder(object);
                    embedBuilder.setAuthor("New Daily Message - " + object.guild.get().getName());
                    embedBuilder.setFooter(object.user.longID + "");
                    embedBuilder.setTitle(object.user.username);
                    embedBuilder.setDescription(content);
                    embedBuilder.addField(dowString, dayOfWeek + "", true);
                    embedBuilder.addField(uIDString, uID + "", true);
                    Message message = embedBuilder.send(channel);
                    message.addReaction(thumbsUp.getEmoji()).complete();
                    message.addReaction(thumbsDown.getEmoji()).complete();
                    Globals.getDailyMessages().getQueue().add(new QueueObject(message.getIdLong(), uID, type));
                    return;
                default:
                    return;
            }
        }
    }

    private static void runCheck() {
        if (isChecking) return;
        new Thread(() -> {
            isChecking = true;
            TextChannel queueChannel = Globals.client.getTextChannelById(Globals.queueChannelID);
            if (queueChannel == null) return;
            ArrayList<QueueObject> queuedMessages = Globals.getDailyMessages().getQueue();
            ListIterator<QueueObject> iterator = queuedMessages.listIterator();
            while (iterator.hasNext()) {
                QueueObject object = iterator.next();
                Message item = queueChannel.retrieveMessageById(object.getMessageId()).complete();
                if (item == null || object.isMarkedForRemoval()) {
                    iterator.remove();
                }
            }
            isChecking = false;
        }).start();
    }

    public static void reactionAdded(CommandObject object, MessageReaction reaction) {
        runCheck();
        MessageReaction.ReactionEmote thumbsUp = Utility.getReaction(Constants.EMOJI_THUMBS_UP);
        MessageReaction.ReactionEmote thumbsDown = Utility.getReaction(Constants.EMOJI_THUMBS_DOWN);
        MessageReaction.ReactionEmote ok = Utility.getReaction(Constants.EMOJI_ALLOW);
        MessageReaction.ReactionEmote no = Utility.getReaction(Constants.EMOJI_DENY);
        Message message = object.message.get();
        User owner = object.client.creator.get();
        //exit if not the queue messageChannel
        if (object.guildChannel.longID != Globals.queueChannelID) return;
        //exit if not the owner.
        if (reaction.getCount() == 0) return;
        List<User> users;
        if (reaction.getReactionEmote().isEmoji()) {
            users = message.retrieveReactionUsers(reaction.getReactionEmote().getEmoji()).complete();
        } else {
            users = message.retrieveReactionUsers(reaction.getReactionEmote().getEmote()).complete();
        }
        if (users.isEmpty()) return;
        //exit if no embeds
        if (message.getEmbeds().isEmpty()) return;
        //exit if not enough reactions
        if (message.getReactions().size() <= 1) return;

        QueueObject request = Globals.getDailyMessages().getRequestItem(object.message.longID);
        if (request == null) return;

        if (request.getMessageId() == message.getIdLong()) {
            //getAllToggles the embed
            MessageEmbed embed = message.getEmbeds().get(0);
            message.clearReactions().complete();
            if (request.getType().equals(Constants.QUEUE_DAILY)) {
                try {
                    //getAllToggles the data
                    long userID = Long.parseLong(embed.getFooter().getText());
                    long uID = -1;
                    DayOfWeek day = null;
                    for (MessageEmbed.Field f : embed.getFields()) {
                        if (f.getName().equalsIgnoreCase(dowString)) {
                            day = DayOfWeek.valueOf(f.getValue());
                        } else if (f.getName().equalsIgnoreCase(uIDString)) {
                            uID = Long.parseLong(f.getValue());
                        }
                    }
                    UserObject user = new UserObject(object.client.getUserByID(userID), null);
                    //do if accepted
                    if (reaction.getReactionEmote().equals(thumbsUp)) {

                        user.queueDm("\\> A daily message you sent was approved. **[" + uID + "]**");
                        Globals.getDailyMessages().getMessages().add(new DailyMessage(embed.getDescription(), day, userID, uID));
                        message.addReaction(ok.getEmoji()).complete();
                        request.toggleMarkedForRemoval();
                        //do if denied
                    } else if (reaction.getReactionEmote().equals(thumbsDown)) {
                        user.queueDm("\\> A daily message you sent was denied. **[" + uID + "]**");
                        request.toggleMarkedForRemoval();
                        message.addReaction(no.getEmoji()).complete();
                    }
                } catch (IllegalArgumentException e) {
                    request.toggleMarkedForRemoval();
                }
            }
        }
    }
}
