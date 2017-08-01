package Handlers;

import Commands.CommandObject;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.DailyUserMessageObject;
import Objects.QueueObject;
import Objects.XEmbedBuilder;
import POGOs.GlobalData;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
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
        Emoji thumbsUp = EmojiManager.getForAlias("thumbsup");
        Emoji thumbsDown = EmojiManager.getForAlias("thumbsdown");
        IChannel channel = object.client.getChannelByID(Globals.queueChannelID);

        if (channel != null) {
            switch (type) {
                case Constants.QUEUE_DAILY:
                    long uID = Utility.newDailyMsgUID(Globals.getGlobalData());
                    XEmbedBuilder embedBuilder = new XEmbedBuilder();
                    embedBuilder.withAuthorName("New Daily Message - " + object.guild.getName());
                    embedBuilder.withFooterText(object.authorSID);
                    embedBuilder.withTitle(object.authorUserName);
                    embedBuilder.withDesc(content);
                    embedBuilder.appendField(dowString, dayOfWeek + "", true);
                    embedBuilder.appendField(uIDString, uID + "", true);
                    embedBuilder.withColor(Utility.getUsersColour(object.botUser,object.guild));
                    IMessage message = Utility.sendEmbedMessage("", embedBuilder, channel).get();
                    RequestBuffer.request(() -> message.addReaction(thumbsUp)).get();
                    RequestBuffer.request(() -> message.addReaction(thumbsDown)).get();
                    Globals.getGlobalData().getQueuedRequests().add(new QueueObject(message.getLongID(), uID, type));
                    checkQueue();
                    return;
                default:
                    return;
            }
        }
    }

    public static void checkQueue() {
        ArrayList<QueueObject> queuedMessages = Globals.getGlobalData().getQueuedRequests();
        for (int i = 0; i < queuedMessages.size(); i++) {
            if (queuedMessages == null || queuedMessages.size() == 0) return;
            IMessage message = Globals.getClient().getMessageByID(queuedMessages.get(i).getMessageId());
            if (message == null) {
                if (message.getLongID() == queuedMessages.get(i).getMessageId()) {
                    queuedMessages.remove(i);
                }
            }
        }
    }


    public static void addedReaction(IMessage message, IReaction reaction) {
        Emoji thumbsUp = EmojiManager.getForAlias("thumbsup");
        Emoji thumbsDown = EmojiManager.getForAlias("thumbsdown");
        Emoji ok = EmojiManager.getForAlias("white_check_mark");
        Emoji no = EmojiManager.getForAlias("no_entry_sign");
        ArrayList<QueueObject> queuedMessages = Globals.getGlobalData().getQueuedRequests();

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
                                RequestBuffer.request(() -> message.removeAllReactions()).get();
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
                                    Globals.getGlobalData().getDailyMessages().add(new DailyUserMessageObject(embed.getDescription(), day, userID, uID));
                                    RequestBuffer.request(() -> message.addReaction(ok)).get();
                                    iterator.remove();
                                } catch (NumberFormatException e) {
                                    iterator.remove();
                                } catch (IllegalArgumentException e) {
                                    iterator.remove();
                                }
                                //if denied
                            } else if (reaction.getUnicodeEmoji().equals(thumbsDown)) {
                                RequestBuffer.request(() -> message.removeAllReactions()).get();
                                RequestBuffer.request(() -> message.addReaction(no)).get();
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
//        for (int i = 0; i < queuedMessages.size(); i++) {
//            if (messageId == queuedMessages.get(i).getMessageId()) {
//                if (message.getEmbeds().size() > 0) {
//                    IEmbed embed = message.getEmbeds().get(0);
//                    DayOfWeek day = DayOfWeek.valueOf(embed.getFooter().getText());
//                    embed.getDescription();
//                    RequestBuffer.request(() -> message.removeAllReactions()).get();
////                    if (reaction.getUnicodeEmoji().equals(thumbsUp)) {
////                        Utility.newDailyMsgUID(Globals.getGlobalData());
////                        Globals.getGlobalData().getDailyMessages().add(new DailyUserMessageObject(embed.getDescription(), day, embed.getTitle()));
////                        RequestBuffer.request(() -> message.addReaction(ok)).get();
////                    } else {
////                        RequestBuffer.request(() -> message.addReaction(no)).get();
////                    }
//                    queuedMessages.remove(i);
//                }
//            }
//        }
//        checkQueue();
        }
    }
}
