package Commands;

import Interfaces.ChannelSetting;
import Interfaces.Command;
import Interfaces.DMCommand;
import Interfaces.GuildToggle;
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
    public String messageSID;
    public long messageID;
    public IGuild guild;
    public String guildSID;
    public long guildID;
    public IChannel channel;
    public String channelSID;
    public long channelID;
    public IUser author;
    public String authorSID;
    public long authorID;
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
    public GuildUsers guildUsers;
    public ChannelData channelData;

    public ArrayList<Command> commands = new ArrayList<>();
    public ArrayList<DMCommand> dmCommands = new ArrayList<>();
    public ArrayList<String> commandTypes = new ArrayList<>();
    public ArrayList<GuildToggle> guildToggles = new ArrayList<>();
    private ArrayList<GuildToggle> toRemove = new ArrayList<>();
    public ArrayList<ChannelSetting> channelSettings = new ArrayList<>();

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

    public CommandObject(DMCommandObject command) {
        this.message = command.message;
        this.channel = command.channel;
        this.author = command.author;
        for (IGuild g : command.client.getGuilds()) {
            if (g.getUsers().contains(command.author)) {
                this.guild = g;
            }
        }
        validate();
        init();
    }

    private void init() {
        messageSID = message.getStringID();
        guildSID = guild.getStringID();
        channelSID = channel.getStringID();
        authorSID = author.getStringID();
        messageID = message.getLongID();
        guildID = guild.getLongID();
        channelID = channel.getLongID();
        authorID = author.getLongID();
        authorUserName = author.getName() + "#" + author.getDiscriminator();
        authorDisplayName = author.getDisplayName(guild);
        authorColour = Utility.getUsersColour(author, guild);
        authorRoles = author.getRolesForGuild(guild);

        guildContent = Globals.getGuildContent(guildSID);
        guildConfig = guildContent.getGuildConfig();
        customCommands = guildContent.getCustomCommands();
        characters = guildContent.getCharacters();
        servers = guildContent.getServers();
        competition = guildContent.getCompetition();
        guildUsers = guildContent.getGuildUsers();
        channelData = guildContent.getChannelData();
        client = Globals.getClient();

        commands = (ArrayList<Command>) Globals.getCommands().clone();
        commandTypes = (ArrayList<String>) Globals.getCommandTypes().clone();
        channelSettings = (ArrayList<ChannelSetting>) Globals.getChannelSettings().clone();
        guildToggles = (ArrayList<GuildToggle>) Globals.getGuildGuildToggles().clone();

        logger.trace("Nodules:" + (" CC = " + guildConfig.moduleCC +
                ", ROLES = " + guildConfig.moduleRoles +
                ", COMP = " + guildConfig.moduleComp +
                ", SERVERS = " + guildConfig.moduleServers +
                ", CHARS = " + guildConfig.moduleChars +
                ", ME = " + guildConfig.moduleMe +
                ", MODMUTE = " + guildConfig.moduleModMute + ".").toUpperCase());
        String testToggles = "";
        for (GuildToggle g : guildToggles) {
            testToggles += g.name() + ", ";
        }
        for (int i = 0; i < guildToggles.size(); i++) {
            if (!guildToggles.get(i).get(guildConfig)) {
                if (guildToggles.get(i).isModule()) {
                    logger.trace(guildToggles.get(i).name() + " - " + guildToggles.get(i).get(guildConfig));
                }
                guildToggles.get(i).execute(this);
            }
        }
        //well this works I guess.
        for (GuildToggle t : toRemove) {
            for (int i = 0; i < guildToggles.size(); i++) {
                if (t.name().equalsIgnoreCase(guildToggles.get(i).name())) {
                    guildToggles.remove(i);
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

    public CommandObject setAuthor(IUser author) {
        this.author = author;
        authorSID = author.getStringID();
        authorUserName = author.getName() + "#" + author.getDiscriminator();
        authorDisplayName = author.getDisplayName(guild);
        authorColour = Utility.getUsersColour(author, guild);
        authorRoles = author.getRolesForGuild(guild);
        notAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't let you do that.";
        return this;
    }

    public CommandObject setChannel(IChannel channel) {
        this.channel = channel;
        channelSID = channel.getStringID();
        return this;
    }

    public CommandObject setGuild(IGuild guild) {
        this.guild = guild;
        guildSID = guild.getStringID();
        GuildContentObject contentObject = Globals.getGuildContent(guildSID);
        guildConfig = contentObject.getGuildConfig();
        customCommands = contentObject.getCustomCommands();
        servers = contentObject.getServers();
        characters = contentObject.getCharacters();
        competition = contentObject.getCompetition();
        if (guild.getUserByID(authorSID) != null) {
            authorColour = Utility.getUsersColour(author, guild);
            authorRoles = author.getRolesForGuild(guild);
            authorDisplayName = author.getDisplayName(guild);
            notAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't let you do that.";
        }
        return this;
    }

    public CommandObject setMessage(IMessage message) {
        this.message = message;
        messageSID = message.getStringID();
        return this;
    }

    public void removeCommandsByType(String type) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).type().equalsIgnoreCase(type)) {
                logger.trace(type + " - " + commands.get(i).names()[0] + " - removed");
                commands.remove(i);
                i--;
            }
        }
        for (int i = 0; i < commandTypes.size(); i++) {
            if (commandTypes.get(i).equalsIgnoreCase(type)) {
                logger.trace("Type - " + commandTypes.get(i) + " - removed");
                commandTypes.remove(i);
            }
        }
    }

    public void removeChannel(String channel) {
        for (int i = 0; i < channelSettings.size(); i++) {
            if (channelSettings.get(i).type().equalsIgnoreCase(channel)) {
                channelSettings.remove(i);
            }
        }
    }

    public void removeCommand(String[] names) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).names()[0].equals(names[0])) {
                commands.remove(i);
            }
        }
    }

    public void removeToggle(String name) {
        for (int i = 0; i < guildToggles.size(); i++) {
            if (guildToggles.get(i).name().equals(name)) {
                toRemove.add(guildToggles.get(i));
            }
        }
    }
}
