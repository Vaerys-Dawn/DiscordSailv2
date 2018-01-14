package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.DailyMessage;
import com.github.vaerys.objects.QueueObject;
import com.github.vaerys.objects.XEmbedBuilder;
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

    public static void addToQueue(CommandObject object, String content, DayOfWeek dayOfWeek, String type) {
        ReactionEmoji thumbsUp = Utility.getReaction("thumbsup");
        ReactionEmoji thumbsDown = Utility.getReaction("thumbsdown");
        IChannel channel = object.client.get().getChannelByID(Globals.queueChannelID);

        if (channel != null) {
            switch (type) {
                case Constants.QUEUE_DAILY:
                    long uID = Globals.getDailyMessages().newDailyMsgUID();
                    if (uID == -1) {
                        object.client.creator.sendDm("> Max limit of Daily messages hit.");
                        break;
                    }
                    XEmbedBuilder embedBuilder = new XEmbedBuilder(object);
                    embedBuilder.withAuthorName("New Daily Message - " + object.guild.get().getName());
                    embedBuilder.withFooterText(object.user.longID + "");
                    embedBuilder.withTitle(object.user.username);
                    embedBuilder.withDesc(content);
                    embedBuilder.appendField(dowString, dayOfWeek + "", true);
                    embedBuilder.appendField(uIDString, uID + "", true);
                    IMessage message = RequestHandler.sendEmbedMessage("", embedBuilder, channel).get();
                    RequestBuffer.request(() -> message.addReaction(thumbsUp)).get();
                    RequestBuffer.request(() -> message.addReaction(thumbsDown)).get();
                    Globals.getDailyMessages().getQueue().add(new QueueObject(message.getLongID(), uID, type));
                    checkQueue();
                    return;
                default:
                    return;
            }
        }
    }

    public static void checkQueue() {
        IChannel queueChannel = Globals.client.getChannelByID(Globals.queueChannelID);
        if (queueChannel == null) return;
        ArrayList<QueueObject> queuedMessages = Globals.getDailyMessages().getQueue();
        ListIterator iterator = queuedMessages.listIterator();
        while (iterator.hasNext()) {
            QueueObject object = (QueueObject) iterator.next();
            if (queueChannel.getMessageByID(object.getMessageId()) == null) {
                iterator.remove();
            } else if (object.isMarkedForRemoval()) {
                iterator.remove();
            }
        }
    }


    public static void reactionAdded(CommandObject object, IReaction reaction) {
        ReactionEmoji thumbsUp = Utility.getReaction("thumbsup");
        ReactionEmoji thumbsDown = Utility.getReaction("thumbsdown");
        ReactionEmoji ok = Utility.getReaction("white_check_mark");
        ReactionEmoji no = Utility.getReaction("no_entry_sign");
        ArrayList<QueueObject> queuedMessages = Globals.getDailyMessages().getQueue();
        IMessage message = object.message.get();
        IUser owner = object.client.creator.get();
        //exit if not the queue channel
        if (object.channel.longID != Globals.queueChannelID) {
            return;
        }
        //exit if not the owner.
        if (reaction.getCount() == 0){
            return;
        }
        if (!reaction.getUserReacted(owner)) {
            return;
        }
        //exit if no embeds
        if (message.getEmbeds().size() == 0) {
            return;
        }
        //exit if not enough reactions
        if (message.getReactions().size() <= 1) {
            return;
        }

        checkQueue();

        for (QueueObject q : queuedMessages) {
            if (q.getMessageId() == message.getLongID()) {
                //get the embed
                IEmbed embed = message.getEmbeds().get(0);
                RequestBuffer.request(() -> message.removeAllReactions()).get();
                switch (q.getType()) {
                    //do if daily request
                    case Constants.QUEUE_DAILY:
                        try {
                            //get the data
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
                            UserObject user = new UserObject(object.client.getUserByID(userID),null);
                            //do if accepted
                            if (reaction.getEmoji().equals(thumbsUp)) {

                                user.sendDm("> A daily message you sent was approved. **[" + uID + "]**");
                                Globals.getDailyMessages().getMessages().add(new DailyMessage(embed.getDescription(), day, userID, uID));
                                RequestBuffer.request(() -> message.addReaction(ok)).get();
                                q.toggleMarkedForRemoval();
                                return;
                                //do if denied
                            } else if (reaction.getEmoji().equals(thumbsDown)) {
                                user.sendDm("> A daily message you sent was denied. **[" + uID + "]**");
                                q.toggleMarkedForRemoval();
                                RequestBuffer.request(() -> message.addReaction(no)).get();
                                return;
                            } else {
                                return;
                            }
                        } catch (NumberFormatException e) {
                            q.toggleMarkedForRemoval();
                        } catch (IllegalArgumentException e) {
                            q.toggleMarkedForRemoval();
                        }
                    default:
                        return;
                }
            }
        }
    }
}
