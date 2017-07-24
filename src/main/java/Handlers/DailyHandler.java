package Handlers;

import Commands.CommandObject;
import Main.Globals;
import Main.Utility;
import Objects.DailyUserMessageObject;
import Objects.XEmbedBuilder;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.util.RequestBuffer;

import java.time.DayOfWeek;
import java.util.ArrayList;

/**
 * Created by Vaerys on 19/07/2017.
 */
public class DailyHandler {

    public static void addToQueue(CommandObject object, String content, DayOfWeek dayOfWeek) {
        Emoji thumbsUp = EmojiManager.getForAlias("thumbsup");
        Emoji thumbsDown = EmojiManager.getForAlias("thumbsdown");
        IChannel channel = object.client.getChannelByID(Globals.queueChannelID);

        if (channel != null) {
            XEmbedBuilder embedBuilder = new XEmbedBuilder();
            embedBuilder.withAuthorName("New Daily Message - " + object.guild.getName());
            embedBuilder.withDesc(content);
            embedBuilder.withFooterText(dayOfWeek.toString());
            embedBuilder.withColor(Utility.getUsersColour(object.botUser, channel.getGuild()));
            embedBuilder.withTitle("[" + object.authorSID + "] " + object.authorUserName);
            IMessage message = Utility.sendEmbedMessage("", embedBuilder, channel).get();
            RequestBuffer.request(() -> message.addReaction(thumbsUp)).get();
            RequestBuffer.request(() -> message.addReaction(thumbsDown)).get();
            Globals.getGlobalData().getQueuedRequests().add(message.getLongID());
            checkQueue();
        }
    }

    public static void checkQueue() {
        ArrayList<Long> queuedMessages = Globals.getGlobalData().getQueuedRequests();
        for (int i = 0; i < queuedMessages.size(); i++) {
            if (queuedMessages == null || queuedMessages.size() == 0) return;
            IMessage message = Globals.getClient().getMessageByID(queuedMessages.get(i));
            if (message == null) {
                if (message.getLongID() == queuedMessages.get(i)) {
                    queuedMessages.remove(i);
                }
            }
        }
    }


    public static void addedReaction(IMessage message, IReaction reaction) {
        Emoji thumbsUp = EmojiManager.getForAlias("thumbsup");
        Emoji ok = EmojiManager.getForAlias("white_check_mark");
        Emoji no = EmojiManager.getForAlias("no_entry_sign");
        ArrayList<Long> queuedMessages = Globals.getGlobalData().getQueuedRequests();

        long messageId = message.getLongID();
        for (int i = 0; i < queuedMessages.size(); i++) {
            if (messageId == queuedMessages.get(i)) {
                if (message.getEmbeds().size() > 0) {
                    IEmbed embed = message.getEmbeds().get(0);
                    DayOfWeek day = DayOfWeek.valueOf(embed.getFooter().getText());
                    embed.getDescription();
                    RequestBuffer.request(() -> message.removeAllReactions()).get();
                    if (reaction.getUnicodeEmoji().equals(thumbsUp)) {
                        Globals.getGlobalData().getDailyUserMessages().add(new DailyUserMessageObject(embed.getDescription(), day, embed.getTitle()));
                        RequestBuffer.request(() -> message.addReaction(ok)).get();
                    } else {
                        RequestBuffer.request(() -> message.addReaction(no)).get();
                    }
                    queuedMessages.remove(i);
                }
            }
        }
        checkQueue();
    }
}
