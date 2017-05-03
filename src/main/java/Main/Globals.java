package Main;

import ChannelSettings.InitChannels;
import Commands.Admin.ChannelHere;
import Commands.CommandInit;
import GuildToggles.ToggleInit;
import Handlers.FileHandler;
import Interfaces.*;
import Objects.DailyMessageObject;
import Objects.GuildContentObject;
import POGOs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class Globals {

    public static String botName = null;
    public static String creatorID = null;
    public static String defaultPrefixCommand = null;
    public static String defaultPrefixCC = null;
    public static String defaultAvatarFile = null;
    public static boolean doDailyAvatars = false;
    public static String dailyAvatarName = null;
    public static String playing = null;
    public static int argsMax = 0;
    public static int maxWarnings = 0;
    public static int avgMessagesPerDay = 0;
    public static boolean isReady = false;
    public static String version;
    public static String consoleMessageCID = null;
    public static ArrayList<DailyMessageObject> dailyMessages = new ArrayList<>();
    public static IDiscordClient client;
    public static boolean isModifingFiles = false;
    private static ArrayList<GuildContentObject> guildContentObjects = new ArrayList<>();
    private static ArrayList<Command> commands = new ArrayList<>();
    private static ArrayList<DMCommand> commandsDM = new ArrayList<>();
    private static ArrayList<String> commandTypes = new ArrayList<>();
    private static ArrayList<ChannelSetting> channelSettings = new ArrayList<>();
    private static ArrayList<GuildToggle> guildGuildToggles = new ArrayList<>();
    private static ArrayList<SlashCommand> slashCommands = new ArrayList<>();

    final static Logger logger = LoggerFactory.getLogger(Globals.class);
    private static GlobalData globalData;
    public static int baseXPModifier;
    public static int xpForLevelOne;

    public static void initConfig(IDiscordClient ourClient, Config config, GlobalData newGlobalData) {
        if (newGlobalData != null) {
            globalData = newGlobalData;
        }
        client = ourClient;
        botName = config.botName;
        creatorID = config.creatorID;
        defaultPrefixCommand = config.defaultPrefixCommand;
        defaultPrefixCC = config.defaultPrefixCC;
        defaultAvatarFile = config.defaultAvatarFile;
        doDailyAvatars = config.doDailyAvatars;
        dailyAvatarName = config.dailyAvatarName;
        playing = config.playing;
        argsMax = config.argsMax;
        maxWarnings = config.maxWarnings;
        dailyMessages = config.dailyMessages;
        baseXPModifier = config.baseXpModifier;
        xpForLevelOne = config.xpForLevelOne;
        avgMessagesPerDay = config.avgMessagesPerDay;
        initCommands();
    }

    private static void initCommands() {
        //Load Commands
        commands = CommandInit.get();
        //Load DM Commands
        commandsDM = CommandInit.getDM();
        //Load Guild Toggles
        guildGuildToggles = ToggleInit.get();

        slashCommands = CommandInit.getSlashCommands();

        channelSettings = InitChannels.get();

        //validate commands
        validate();

        //getSlashCommands Command Types
        commandTypes.add(Command.TYPE_DM);

        //auto remover code for Commands.Admin.ChannelHere, will remove if channels are not in use.
        if (channelSettings.size() == 0) {
            for (int i = 0; i < commands.size(); i++) {
                if (commands.get(i).names()[0].equalsIgnoreCase(new ChannelHere().names()[0])) {
                    commands.remove(i);
                }
            }
        }

        //Init Command Types.
        for (Command c : commands) {
            boolean typeFound = false;
            for (String s : commandTypes) {
                if (c.type().equals(s)) {
                    typeFound = true;
                }
            }
            if (!typeFound) {
                commandTypes.add(c.type());
            }
        }

        logger.info(commands.size() + " Commands Loaded.");
        logger.info(commandsDM.size() + " DM Commands Loaded.");
        logger.info(commandTypes.size() + " Command Types Loaded.");
        logger.info(channelSettings.size() + " Channel Types Loaded.");
        logger.info(guildGuildToggles.size() + " Guild Toggles Loaded.");
        logger.info(slashCommands.size() + " Slash Commands Loaded.");
    }

    private static void validate() throws IllegalArgumentException {
        for (Command c : commands) {
            logger.trace("Validating Command: " + c.getClass().getName());
            if (c.names().length == 0)
                throw new IllegalArgumentException(c.getClass().getName() + " Command Name cannot be null.");
            if (c.type() == null || c.type().isEmpty())
                throw new IllegalArgumentException(c.getClass().getName() + " Command Type cannot be null.");
            if (c.description() == null || c.description().isEmpty())
                throw new IllegalArgumentException(c.getClass().getName() + " Command Desc cannot be null.");
            if (c.requiresArgs() && (c.usage() == null || c.usage().isEmpty()))
                throw new IllegalArgumentException(c.getClass().getName() + " Command Usage cannot be null if RequiresArgs is true.");
            if (c.dualDescription() != null || c.dualType() != null) {
                if (c.dualType() == null || c.dualType().isEmpty())
                    throw new IllegalArgumentException(c.getClass().getName() + " Command Dual Type cannot be null.");
                if (c.dualType().equalsIgnoreCase(c.type()))
                    throw new IllegalArgumentException(c.getClass().getName() + " Command Type cannot be equal to Dual Type.");
                if (c.dualDescription() == null || c.dualDescription().isEmpty())
                    throw new IllegalArgumentException(c.getClass().getName() + " Command Dual Desc cannot be null.");
                if (c.dualDescription().equalsIgnoreCase(c.description()))
                    throw new IllegalArgumentException(c.getClass().getName() + " Command Desc cannot be equal to Dual Desc.");
                if (c.usage() != null && c.usage().equalsIgnoreCase(c.dualUsage()))
                    throw new IllegalArgumentException(c.getClass().getName() + " Command Usage cannot be equal to Dual Usage.");
            }
        }
        for (DMCommand c : commandsDM) {
            logger.trace("Validating DM Command: " + c.getClass().getName());
            if (c.names().length == 0)
                throw new IllegalArgumentException(c.getClass().getName() + " Command Name cannot be null");
            if (c.description() == null || c.description().isEmpty())
                throw new IllegalArgumentException(c.getClass().getName() + " Command Desc cannot be null.");
            if (c.type() == null || c.type().isEmpty())
                throw new IllegalArgumentException(c.getClass().getName() + " Command Type cannot be null.");
            if (c.requiresArgs() && (c.usage() == null || c.usage().isEmpty()))
                throw new IllegalArgumentException(c.getClass().getName() + " Command Usage cannot be null if RequiresArgs is true.");
        }

        for (GuildToggle g : guildGuildToggles) {
            logger.trace("Validating Toggle: " + g.getClass().getName());
            if (g.name() == null || g.name().isEmpty())
                throw new IllegalArgumentException(g.getClass().getName() + " Toggle Name cannot be null.");
            if (g.name().contains(" "))
                throw new IllegalArgumentException(g.getClass().getName() + "Toggle Name cannot contain spaces.");
            if (g.name().contains("\n"))
                throw new IllegalArgumentException(g.getClass().getName() + "Toggle Name cannot contain Newlines.");
        }
        for (SlashCommand s : slashCommands) {
            if (s.call() == null || s.call().isEmpty())
                throw new IllegalArgumentException(s.getClass().getName() + " Slash Command Call must not be null.");
            if (s.response() == null || s.response().isEmpty())
                throw new IllegalArgumentException(s.getClass().getName() + " Slash Command Response must not be null.");
            if (!s.call().startsWith("/"))
                throw new IllegalArgumentException(s.getClass().getName() + " Slash Command call must Start with \"/\"");
        }

        for (ChannelSetting s: channelSettings){
            if (s.type() == null || s.type().isEmpty()){
                throw new IllegalArgumentException(s.getClass().getName() + " Channel Type cannot be null.");
            }
        }
    }

    public static void validateConfig() throws IllegalArgumentException {
        IUser creator = client.getUserByID(creatorID);
        if (creator == null)
            throw new IllegalArgumentException("Creator ID is invalid.");
        if (botName == null || botName.isEmpty())
            throw new IllegalArgumentException("Bot name cannot be empty.");
        if (botName.contains("\n"))
            throw new IllegalArgumentException("botName cannot contain Newlines.");
        if (botName.length() > 32)
            throw new IllegalArgumentException("botName cannot be longer than 32 chars.");
        if (defaultPrefixCommand == null || defaultPrefixCommand.isEmpty())
            throw new IllegalArgumentException("defaultPrefixCommand cannot be empty.");
        if (defaultPrefixCC == null || defaultPrefixCC.isEmpty())
            throw new IllegalArgumentException("defaultPrefixCC cannot be empty.");
        if (defaultPrefixCommand.contains(" "))
            throw new IllegalArgumentException("defaultPrefixCommand cannot contain spaces.");
        if (defaultPrefixCC.contains(" "))
            throw new IllegalArgumentException("defaultPrefixCC cannot contain spaces.");
        if (defaultPrefixCommand.contains("\n"))
            throw new IllegalArgumentException("defaultPrefixCommand cannot contain Newlines.");
        if (defaultPrefixCC.contains("\n"))
            throw new IllegalArgumentException("defaultPrefixCommand cannot contain Newlines.");
        if (doDailyAvatars) {
            if (!dailyAvatarName.contains("#day#"))
                throw new IllegalArgumentException("dailyAvatarName must contain #day# for the feature to work as intended.");
            for (DailyMessageObject d : dailyMessages) {
                if (!Files.exists(Paths.get(Constants.DIRECTORY_GLOBAL_IMAGES + d.getFileName())))
                    throw new IllegalArgumentException("File " + Constants.DIRECTORY_GLOBAL_IMAGES + d.getFileName() + " does not exist.");
                else if (!Files.exists(Paths.get(Constants.DIRECTORY_GLOBAL_IMAGES + defaultAvatarFile)))
                    throw new IllegalArgumentException("File" + Constants.DIRECTORY_GLOBAL_IMAGES + defaultAvatarFile + " does not exist.");
            }
        }
        if (argsMax <= 0)
            throw new IllegalArgumentException("argsMax cannot be less than or equal 0.");
        if (maxWarnings <= 0)
            throw new IllegalArgumentException("maxWarnings cannot be less than or equal 0");
        if (avgMessagesPerDay <= 0) {
            throw new IllegalArgumentException("avgMessagesPerDay cannot be less than or equal 0");
        }
    }

    public static void initGuild(String guildID, GuildConfig guildConfig, Servers servers, CustomCommands customCommands, Characters characters, Competition competition, GuildUsers guildUsers) {
        for (GuildContentObject contentObject : guildContentObjects) {
            if (guildID.equals(contentObject.getGuildID())) {
                return;
            }
        }

        IGuild guild = client.getGuildByID(guildID);
        guildConfig.updateVariables(guild);

        GuildContentObject guildContentObject = new GuildContentObject(guildID, guildConfig, customCommands, servers, characters, competition, guildUsers);
        guildContentObjects.add(guildContentObject);
    }

    public static void setVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(Main.class.getClassLoader().getResourceAsStream("project.properties"));
            version = properties.getProperty("version");
            logger.info("Bot version : " + version);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static IDiscordClient getClient() {
        return client;
    }

    public static GuildContentObject getGuildContent(String guildID) {
        for (GuildContentObject storage : guildContentObjects) {
            if (storage.getGuildID().equals(guildID)) {
                return storage;
            }
        }
        return null;
    }

    public static ArrayList<GuildContentObject> getGuildContentObjects() {
        return guildContentObjects;
    }

    public static void saveFiles() {
        logger.debug("Saving Files.");
        Globals.isModifingFiles = true;
        Globals.getGuildContentObjects().forEach(GuildContentObject::saveFiles);
        FileHandler.writeToJson(Constants.FILE_GLOBAL_DATA, getGlobalData());
        Globals.isModifingFiles = false;
    }

    public static void unloadGuild(String id) {
        for (int i = 0; i < guildContentObjects.size(); i++) {
            if (guildContentObjects.get(i).getGuildID().equals(id)) {
                logger.info("> Disconnected from Guild with ID : " + id);
                guildContentObjects.remove(i);
            }
        }
    }

    public static ArrayList<Command> getCommands() {
        return commands;
    }

    public static GlobalData getGlobalData() {
        if (globalData != null) {
            return globalData;
        } else {
            return null;
        }
    }

    public static ArrayList<DMCommand> getCommandsDM() {
        return commandsDM;
    }

    public static ArrayList<String> getCommandTypes() {
        return commandTypes;
    }

    public static ArrayList<ChannelSetting> getChannelSettings() {
        return channelSettings;
    }

    public static ArrayList<GuildToggle> getGuildGuildToggles() {
        return guildGuildToggles;
    }

    public static ArrayList<SlashCommand> getSlashCommands() {
        return slashCommands;
    }
}
