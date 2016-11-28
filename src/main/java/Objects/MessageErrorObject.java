package Objects;

import sx.blah.discord.handle.obj.IMessage;


/**
 * Created by Vaerys on 28/11/2016.
 */
public class MessageErrorObject {

    String guildID;
    String guildName;
    String authorID;
    String authorUsername;
    String channelID;
    String channelName;
    String messageID;
    String message;

    public MessageErrorObject(IMessage message) {
        this.guildID = message.getGuild().getID();
        this.guildName = message.getGuild().getName();
        this.authorID = message.getAuthor().getID();
        this.authorUsername = message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator();
        this.channelID = message.getChannel().getID();
        this.channelName = message.getChannel().getName();
        this.messageID = message.getID();
        this.message = message.getContent();
    }
}
