package Commands;

import Main.Globals;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class DMCommandObject {
    public IMessage message;
    public String messageID;
    public IChannel channel;
    public String channelID;
    public IUser author;
    public String authorID;
    public String authorUserName;
    public String notAllowed;
    public IDiscordClient client;

    public DMCommandObject(IMessage message) {
        this.message = message;
        messageID = message.getStringID();
        channel = message.getChannel();
        channelID = channel.getStringID();
        author = message.getAuthor();
        authorID = author.getStringID();
        authorUserName = author.getName() + "#" + author.getDiscriminator();
        client = Globals.getClient();
        notAllowed = "> I'm sorry " + author.getName() + ", I'm afraid I can't let you do that.";
    }

    public void setAuthor(IUser author) {
        this.author = author;
        authorID = author.getStringID();
        authorUserName = author.getName() + "#" + author.getDiscriminator();
        notAllowed = "> I'm sorry " + author.getName() + ", I'm afraid I can't let you do that.";
    }

    public void setChannel(IChannel channel) {
        this.channel = channel;
        channelID = channel.getStringID();
    }

    public void setMessage(IMessage message) {
        this.message = message;
        messageID = message.getStringID();
    }
}
