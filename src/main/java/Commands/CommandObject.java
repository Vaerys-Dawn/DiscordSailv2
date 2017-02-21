package Commands;

import GuildToggles.GuildToggle;
import Main.Globals;
import Main.Utility;
import Objects.GuildContentObject;
import POGOs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;

import java.awt.*;
import java.util.ArrayList;
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

    public GuildContentObject guildContent;
    public GuildConfig guildConfig;
    public CustomCommands customCommands;
    public Characters characters;
    public Servers servers;
    public Competition competition;

    public ArrayList<Command> commands = new ArrayList<>();
    public ArrayList<DMCommand> dmCommands = new ArrayList<>();
    public ArrayList<String> channelTypes = new ArrayList<>();
    public ArrayList<String> commandTypes = new ArrayList<>();
    public ArrayList<GuildToggle> guildToggles = new ArrayList<>();

    public IDiscordClient client;


    final static Logger logger = LoggerFactory.getLogger(CommandObject.class);

    public CommandObject(IMessage message) {
        this.message = message;
        guild = message.getGuild();
        channel = message.getChannel();
        author = message.getAuthor();
        init();
    }

    public CommandObject(IMessage message, IGuild guild, IChannel channel, IUser author) {
        this.message = message;
        this.guild = guild;
        this.channel = channel;
        this.author = author;
        validate();
        init();
    }

    private void init() {
        messageID = message.getID();
        guildID = guild.getID();
        channelID = channel.getID();
        authorID = author.getID();
        authorUserName = author.getName() + "#" + author.getDiscriminator();
        authorDisplayName = author.getDisplayName(guild);
        authorColour = Utility.getUsersColour(author, guild);
        authorRoles = author.getRolesForGuild(guild);

        guildContent = Globals.getGuildContent(guildID);
        guildConfig = guildContent.getGuildConfig();
        customCommands = guildContent.getCustomCommands();
        characters = guildContent.getCharacters();
        servers = guildContent.getServers();
        competition = guildContent.getCompetition();
        client = Globals.getClient();

        commands = (ArrayList<Command>) Globals.getCommands().clone();
        commandTypes = (ArrayList<String>) Globals.getCommandTypes().clone();
        channelTypes = (ArrayList<String>) Globals.getChannelTypes().clone();
        guildToggles = Globals.getGuildGuildToggles();

        for (GuildToggle t : guildToggles) {
            if (t.isModule()) {
                if (!t.get(guildConfig)) {
                    logger.info(t.name() + " - " + t.get(guildConfig) + "");
                    t.execute(this);
                }
            }
        }
        dmCommands = Globals.getCommandsDM();

        notAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't let you do that.";
    }

    private void validate() throws IllegalStateException {
        if (message == null) throw new IllegalStateException("message can't be null");
        if (guild == null) throw new IllegalStateException("guild can't be null");
        if (channel == null) throw new IllegalStateException("channel can't be null");
        if (author == null) throw new IllegalStateException("author can't be null");
    }

    public void setAuthor(IUser author) {
        this.author = author;
        authorID = author.getID();
        authorUserName = author.getName() + "#" + author.getDiscriminator();
        authorDisplayName = author.getDisplayName(guild);
        authorColour = Utility.getUsersColour(author, guild);
        authorRoles = author.getRolesForGuild(guild);
        notAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't let you do that.";
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
            notAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't let you do that.";
        }
    }

    public void setMessage(IMessage message) {
        this.message = message;
        messageID = message.getID();
    }

    public void removeCommandsByType(String type) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).type().equalsIgnoreCase(type)) {
                logger.debug(type + " - " + commands.get(i).names()[0] + " - removed");
                commands.remove(i);
            }
        }
        for (int i = 0; i < commandTypes.size(); i++) {
            if (commandTypes.get(i).equalsIgnoreCase(type)) {
                commandTypes.remove(i);
                logger.debug(type + " - removed");
            }
        }
    }

    public void removeChannel(String channel) {
        for (int i = 0; i < channelTypes.size(); i++) {
            if (channelTypes.get(i).equalsIgnoreCase(channel)) {
                channelTypes.remove(i);
            }
        }
    }
}
