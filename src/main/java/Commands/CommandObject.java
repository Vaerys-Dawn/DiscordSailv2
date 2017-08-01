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
import java.util.ListIterator;


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
    public IUser botUser;
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
        botUser = client.getOurUser();

        commands = (ArrayList<Command>) Globals.getCommands().clone();
        commandTypes = (ArrayList<String>) Globals.getCommandTypes().clone();
        channelSettings = (ArrayList<ChannelSetting>) Globals.getChannelSettings().clone();
        guildToggles = (ArrayList<GuildToggle>) Globals.getGuildGuildToggles().clone();

        checkToggles();

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
        validate();
        init();
        return this;
    }

    public CommandObject setChannel(IChannel channel) {
        this.channel = channel;
        validate();
        init();
        return this;
    }

    public CommandObject setGuild(IGuild guild) {
        this.guild = guild;
        validate();
        init();
        return this;
    }

    public CommandObject setMessage(IMessage message) {
        this.message = message;
        validate();
        init();
        return this;
    }

    private void checkToggles() {
        for (GuildToggle g : guildToggles) {
            if (!g.get(guildConfig)) {
                g.execute(this);
            }
        }

        for (GuildToggle g : toRemove) {
            ListIterator iterator = guildToggles.listIterator();
            while (iterator.hasNext()) {
                GuildToggle toggle = (GuildToggle) iterator.next();
                if (toggle.name().equalsIgnoreCase(g.name())) {
                    if (g.isModule()) {
                        logger.trace("Module: " + g.name() + " removed.");
                    } else {
                        logger.trace("Toggle: " + g.name() + " removed.");
                    }
                    iterator.remove();
                }
            }
        }
    }

    public void removeCommandsByType(String type) {
        ListIterator iterator = commands.listIterator();
        while (iterator.hasNext()) {
            Command c = (Command) iterator.next();
            if (c.type().equals(type)) {
                logger.trace("Command: " + c.names()[0] + " removed.");
                iterator.remove();
            }
        }
        for (String s : commandTypes) {
            if (s.equals(type)) {
                logger.trace("Type: " + s + " removed.");
                commandTypes.remove(s);
                return;
            }
        }
    }

    public void removeChannel(String channel) {
        ListIterator iterator = channelSettings.listIterator();
        while (iterator.hasNext()) {
            ChannelSetting c = (ChannelSetting) iterator.next();
            if (c.type().equals(channel)) {
                iterator.remove();
                logger.trace("Channel Setting: " + c.type() + " removed.");
            }
        }
    }

    public void removeCommand(String[] names) {
        ListIterator iterator = commands.listIterator();
        while (iterator.hasNext()) {
            Command c = (Command) iterator.next();
            if (c.names()[0].equals(names[0])) {
                iterator.remove();
                logger.trace("Command: " + c.names()[0] + " removed.");
            }
        }
    }

    public void removeToggle(String name) {
        for (GuildToggle g : guildToggles) {
            if (g.name().equalsIgnoreCase(name)) {
                toRemove.add(g);
            }
        }
    }
}
