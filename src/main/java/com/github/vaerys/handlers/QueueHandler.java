package com.github.vaerys.handlers;

import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.botlevel.QueueObject;
import com.github.vaerys.objects.userlevel.DailyMessage;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.RequestBuffer;

import java.time.DayOfWeek;
import java.util.ArrayList;
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
        ReactionEmoji thumbsUp = Utility.getReaction(Constants.EMOJI_THUMBS_UP);
        ReactionEmoji thumbsDown = Utility.getReaction(Constants.EMOJI_THUMBS_DOWN);
        TextChannel channel = object.client.get().getChannelByID(Globals.queueChannelID);

        if (channel != null) {
            switch (type) {
                case Constants.QUEUE_DAILY:
                    long uID = Globals.getDailyMessages().newDailyMsgUID();
                    if (uID == -1) {
                        object.client.creator.sendDm("\\> Max limit of Daily messages hit.");
                        break;
                    }
                    XEmbedBuilder embedBuilder = new XEmbedBuilder(object);
                    embedBuilder.setAuthor("New Daily Message - " + object.guild.get().getName());
                    embedBuilder.setFooter(object.user.longID + "");
                    embedBuilder.setTitle(object.user.username);
                    embedBuilder.setDescription(content);
                    embedBuilder.addField(dowString, dayOfWeek + "", true);
                    embedBuilder.addField(uIDString, uID + "", true);
                    Message message = RequestHandler.sendEmbedMessage("", embedBuilder, channel).get();
                    RequestBuffer.request(() -> message.addReaction(thumbsUp)).get();
                    RequestBuffer.request(() -> message.addReaction(thumbsDown)).get();
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
            TextChannel queueChannel = Globals.client.getChannelByID(Globals.queueChannelID);
            if (queueChannel == null) return;
            ArrayList<QueueObject> queuedMessages = Globals.getDailyMessages().getQueue();
            ListIterator iterator = queuedMessages.listIterator();
            while (iterator.hasNext()) {
                QueueObject object = (QueueObject) iterator.next();
                Message item = queueChannel.getMessageByID(object.getMessageId());
                if (item == null) {
                    item = RequestBuffer.request(() -> queueChannel.fetchMessage(object.getMessageId())).get();
                }
                if (item == null) {
                    iterator.remove();
                } else if (object.isMarkedForRemoval()) {
                    iterator.remove();
                }
            }
            isChecking = false;
        }).start();
    }

    public static void reactionAdded(CommandObject object, IReaction reaction) {
        runCheck();
        ReactionEmoji thumbsUp = Utility.getReaction(Constants.EMOJI_THUMBS_UP);
        ReactionEmoji thumbsDown = Utility.getReaction(Constants.EMOJI_THUMBS_DOWN);
        ReactionEmoji ok = Utility.getReaction(Constants.EMOJI_ALLOW);
        ReactionEmoji no = Utility.getReaction(Constants.EMOJI_DENY);
        Message message = object.message.get();
        IUser owner = object.client.creator.get();
        //exit if not the queue channel
        if (object.channel.longID != Globals.queueChannelID) return;
        //exit if not the owner.
        if (reaction.getCount() == 0) return;
        if (!reaction.getUserReacted(owner)) return;
        //exit if no embeds
        if (message.getEmbeds().size() == 0) return;
        //exit if not enough reactions
        if (message.getReactions().size() <= 1) return;

        QueueObject request = Globals.getDailyMessages().getRequestItem(object.message.longID);
        if (request == null) return;

        if (request.getMessageId() == message.getIdLong()) {
            //getAllToggles the embed
            IEmbed embed = message.getEmbeds().get(0);
            RequestBuffer.request(() -> message.removeAllReactions()).get();
            switch (request.getType()) {
                //do if daily request
                case Constants.QUEUE_DAILY:
                    try {
                        //getAllToggles the data
                        long userID = Long.parseLong(embed.getFooter().getText());
                        long uID = -1;
                        DayOfWeek day = null;
                        for (IEmbed.IEmbedField f : embed.getEmbedFields()) {
                            if (f.getName().equalsIgnoreCase(dowString)) {
                                day = DayOfWeek.valueOf(f.getValue());
                            } else if (f.getName().equalsIgnoreCase(uIDString)) {
                                uID = Long.parseLong(f.getValue());
                            }
                        }
                        UserObject user = new UserObject(object.client.getUserByID(userID), null);
                        //do if accepted
                        if (reaction.getEmoji().equals(thumbsUp)) {

                            user.sendDm("\\> A daily message you sent was approved. **[" + uID + "]**");
                            Globals.getDailyMessages().getMessages().add(new DailyMessage(embed.getDescription(), day, userID, uID));
                            RequestBuffer.request(() -> message.addReaction(ok)).get();
                            request.toggleMarkedForRemoval();
                            return;
                            //do if denied
                        } else if (reaction.getEmoji().equals(thumbsDown)) {
                            user.sendDm("\\> A daily message you sent was denied. **[" + uID + "]**");
                            request.toggleMarkedForRemoval();
                            RequestBuffer.request(() -> message.addReaction(no)).get();
                            return;
                        } else {
                            return;
                        }
                    } catch (NumberFormatException e) {
                        request.toggleMarkedForRemoval();
                    } catch (IllegalArgumentException e) {
                        request.toggleMarkedForRemoval();
                    }
                default:
                    return;
            }
        }
    }
}
