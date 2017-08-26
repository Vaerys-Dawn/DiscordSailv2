package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.DailyUserMessageObject;
import com.github.vaerys.objects.QueueObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.objects.XRequestBuffer;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;

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
        Emoji thumbsUp = EmojiManager.getForAlias("thumbsup");
        Emoji thumbsDown = EmojiManager.getForAlias("thumbsdown");
        IChannel channel = object.client.get().getChannelByID(Globals.queueChannelID);

        if (channel != null) {
            switch (type) {
                case Constants.QUEUE_DAILY:
                    long uID = Utility.newDailyMsgUID(Globals.getDailyMessages());
                    XEmbedBuilder embedBuilder = new XEmbedBuilder();
                    embedBuilder.withAuthorName("New Daily Message - " + object.guild.get().getName());
                    embedBuilder.withFooterText(object.user.stringID);
                    embedBuilder.withTitle(object.user.username);
                    embedBuilder.withDesc(content);
                    embedBuilder.appendField(dowString, dayOfWeek + "", true);
                    embedBuilder.appendField(uIDString, uID + "", true);
                    embedBuilder.withColor(Utility.getUsersColour(object.client.bot, object.guild.get()));
                    IMessage message = Utility.sendEmbedMessage("", embedBuilder, channel).get();
                    XRequestBuffer.request(() -> message.addReaction(thumbsUp)).get();
                    XRequestBuffer.request(() -> message.addReaction(thumbsDown)).get();
                    Globals.getDailyMessages().getQueue().add(new QueueObject(message.getLongID(), uID, type));
                    checkQueue();
                    return;
                default:
                    return;
            }
        }
    }

    public static void checkQueue() {
        ArrayList<QueueObject> queuedMessages = Globals.getDailyMessages().getQueue();
        ListIterator iterator = queuedMessages.listIterator();
        while (iterator.hasNext()) {
            QueueObject object = (QueueObject) iterator.next();
            if (Globals.client.getMessageByID(object.getMessageId()) == null) {
                iterator.remove();
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void addedReaction(IMessage message, IReaction reaction) {
        Emoji thumbsUp = EmojiManager.getForAlias("thumbsup");
        Emoji thumbsDown = EmojiManager.getForAlias("thumbsdown");
        Emoji ok = EmojiManager.getForAlias("white_check_mark");
        Emoji no = EmojiManager.getForAlias("no_entry_sign");
        ArrayList<QueueObject> queuedMessages = Globals.getDailyMessages().getQueue();

        long messageId = message.getLongID();
        ListIterator iterator = queuedMessages.listIterator();
        while (iterator.hasNext()) {
            QueueObject object = (QueueObject) iterator.next();
            //check if has same messageID
            if (messageId == object.getMessageId()) {
                //check if has embed
                if (message.getEmbeds().size() > 0) {
                    IEmbed embed = message.getEmbeds().get(0);
                    //check what type of object it is.
                    switch (object.getType()) {
                        case Constants.QUEUE_DAILY:
                            //if approved
                            if (reaction.getUnicodeEmoji().equals(thumbsUp)) {
                                XRequestBuffer.request(() -> message.removeAllReactions()).get();
                                try {
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
                                    Utility.sendDM("> A daily message you sent was approved. **[" + uID + "]**", userID);
                                    Globals.getDailyMessages().getMessages().add(new DailyUserMessageObject(embed.getDescription(), day, userID, uID));
                                    XRequestBuffer.request(() -> message.addReaction(ok)).get();
                                    iterator.remove();
                                } catch (NumberFormatException e) {
                                    iterator.remove();
                                } catch (IllegalArgumentException e) {
                                    iterator.remove();
                                }
                                //if denied
                            } else if (reaction.getUnicodeEmoji().equals(thumbsDown)) {
                                try {
                                    long userID = Long.parseLong(embed.getFooter().getText());
                                    long uID = -1;
                                    for (IEmbed.IEmbedField f : embed.getEmbedFields()) {
                                        if (f.getName().equalsIgnoreCase(uIDString)) {
                                            uID = Long.parseLong(f.getValue());
                                        }
                                    }
                                    Utility.sendDM("> A daily message you sent was denied. **[" + uID + "]**", userID);
                                } catch (NumberFormatException e) {
                                    //do nothing
                                }
                                XRequestBuffer.request(() -> message.removeAllReactions()).get();
                                XRequestBuffer.request(() -> message.addReaction(no)).get();
                                iterator.remove();
                            }
                            break;
                        default:
                            //ignore everything for now
                            break;
                    }
                }
            }
            checkQueue();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
