package Commands;

import Main.Globals;
import Main.Utility;
import Objects.GuildContentObject;
import POGOs.*;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;

import java.awt.*;
import java.util.List;


/**
 * Created by Vaerys on 29/01/2017.
 */
public class CommandObject {

    public IMessage message;
    public String messageID;
    public IGuild guild;
    public String guildID;
    public IChannel channel;
    public String channelID;
    public IUser author;
    public String authorID;
    public String authorDisplayName;
    public String authorUserName;
    public Color authorColour;
    public List<IRole> authorRoles;
    public String notAllowed;

    public GuildConfig guildConfig;
    public CustomCommands customCommands;
    public Characters characters;
    public Servers servers;
    public Competition competition;
    public IDiscordClient client;

    public CommandObject(IMessage message) {
        this.message = message;
        messageID = message.getID();
        guild = message.getGuild();
        guildID = guild.getID();
        channel = message.getChannel();
        channelID = channel.getID();
        author = message.getAuthor();
        authorID = author.getID();
        authorUserName = author.getName() + "#" + author.getDiscriminator();
        authorDisplayName = author.getDisplayName(guild);
        authorColour = Utility.getUsersColour(author, guild);
        authorRoles = author.getRolesForGuild(guild);

        GuildContentObject guildFiles = Globals.getGuildContent(guildID);
        guildConfig = guildFiles.getGuildConfig();
        customCommands = guildFiles.getCustomCommands();
        characters = guildFiles.getCharacters();
        servers = guildFiles.getServers();
        competition = guildFiles.getCompetition();
        client = Globals.getClient();

        notAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't do that.";
    }

    public void setAuthor(IUser author) {
        this.author = author;
        authorID = author.getID();
        authorUserName = author.getName() + "#" + author.getDiscriminator();
        authorDisplayName = author.getDisplayName(guild);
        authorColour = Utility.getUsersColour(author, guild);
        authorRoles = author.getRolesForGuild(guild);
        notAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't do that.";
    }

    public void setChannel(IChannel channel) {
        this.channel = channel;
        channelID = channel.getID();
    }

    public void setGuild(IGuild guild) {
        this.guild = guild;
        guildID = guild.getID();
        GuildContentObject contentObject = Globals.getGuildContent(guildID);
        guildConfig = contentObject.getGuildConfig();
        customCommands = contentObject.getCustomCommands();
        servers = contentObject.getServers();
        characters = contentObject.getCharacters();
        competition = contentObject.getCompetition();
        if (guild.getUserByID(authorID) != null) {
            authorColour = Utility.getUsersColour(author, guild);
            authorRoles = author.getRolesForGuild(guild);
            authorDisplayName = author.getDisplayName(guild);
            notAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't do that.";
        }
    }

    public void setMessage(IMessage message) {
        this.message = message;
        messageID = message.getID();
    }
}
