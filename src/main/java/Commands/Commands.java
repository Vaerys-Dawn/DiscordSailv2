package Commands;

import Annotations.AliasAnnotation;
import Annotations.CommandAnnotation;
import Main.Constants;
import Main.FileHandler;
import Main.Utility;
import POGOs.Characters;
import POGOs.CustomCommands;
import POGOs.GuildConfig;
import POGOs.Servers;
import org.apache.commons.lang3.text.WordUtils;
import sx.blah.discord.handle.obj.*;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class Commands {

    private IMessage message;
    private IGuild guild;
    private IChannel channel;
    private IUser author;
    private String guildID;
    private String args;

    boolean loadedConfig = false;
    boolean loadedCC = false;
    boolean loadedServers = false;
    boolean loadedCharacters = false;

    private GuildConfig guildConfig = new GuildConfig();
    private CustomCommands customCommands = new CustomCommands();
    private Servers servers = new Servers();
    private Characters characters = new Characters();

    FileHandler handler = new FileHandler();

    public Commands(String args, IMessage message) {
        this.args = args;
        this.message = message;
        guild = message.getGuild();
        channel = message.getChannel();
        author = message.getAuthor();
        guildID = guild.getID();
        loadConfig();
    }

    //File Loaders
    public void loadConfig() {
        guildConfig = (GuildConfig) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), GuildConfig.class);
        loadedConfig = true;
    }

    public void loadCC() {
        customCommands = (CustomCommands) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_CUSTOM), CustomCommands.class);
        loadedCC = true;
    }

    public void loadServers() {
        servers = (Servers) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_SERVERS), Servers.class);
        loadedServers = true;
    }

    public void loadCharacters() {
        characters = (Characters) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_CHARACTERS), Characters.class);
        loadedCharacters = true;
    }

    public void flushFiles() {
        if (loadedConfig) handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), guildConfig);
        if (loadedCC) handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_CUSTOM), customCommands);
        if (loadedServers) handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_SERVERS), servers);
        if (loadedCharacters)
            handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_CHARACTERS), characters);
    }

    //BlackListed Phrase Remover



    //Channel Initiators

    @AliasAnnotation(alias = {"SetupChannel"})
    @CommandAnnotation(
            name = "ChannelHere", description = "Sets the current channel as the **General** channel.", usage = "[General, Servers, Admin, RaceSelect, Logging]",
            perms = {Permissions.MANAGE_CHANNELS}, type = Constants.TYPE_ADMIN, doLogging = true, requiresArgs = true)
    public String channelHere() {
        boolean channelUpdated = false;
        if (args.equalsIgnoreCase(Constants.CHANNEL_GENERAL)) {
            guildConfig.setUpChannel(Constants.CHANNEL_GENERAL, channel.getID());
            channelUpdated = true;
        } else if (args.equalsIgnoreCase(Constants.CHANNEL_SERVERS)) {
            guildConfig.setUpChannel(Constants.CHANNEL_SERVERS, channel.getID());
            channelUpdated = true;
        } else if (args.equalsIgnoreCase(Constants.CHANNEL_ADMIN)) {
            guildConfig.setUpChannel(Constants.CHANNEL_ADMIN, channel.getID());
            channelUpdated = true;
        } else if (args.equalsIgnoreCase(Constants.CHANNEL_RACE_SELECT)) {
            guildConfig.setUpChannel(Constants.CHANNEL_RACE_SELECT, channel.getID());
            channelUpdated = true;
        } else if (args.equalsIgnoreCase(Constants.CHANNEL_LOGGING)) {
            guildConfig.setUpChannel(Constants.CHANNEL_LOGGING, channel.getID());
            channelUpdated = true;
        }
        if (channelUpdated) {
            return "> This channel is now this server's **" + WordUtils.capitalizeFully(args) + "** channel.";
        } else {
            return "> Channel type with that name not found.";
        }
    }

    //help commands

    //General Commands

    @CommandAnnotation(name = "Hello", description = "Says Hello.")
    public String hello() {
        return "> Hello " + author.getDisplayName(guild) + ".";
    }

    @CommandAnnotation(name = "Test", description = "Tests things", doLogging = true)
    public String test() {
        return "Tested";
    }

    //admin commands

    //race select commands

    //servers commands

    //character commands

    //Custom command commands
}
