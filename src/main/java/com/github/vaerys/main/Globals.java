package com.github.vaerys.main;

import com.github.vaerys.channelsettings.InitChannels;
import com.github.vaerys.commands.CommandInit;
import com.github.vaerys.commands.admin.ChannelHere;
import com.github.vaerys.guildtoggles.ToggleInit;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.interfaces.*;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.DailyMessageObject;
import com.github.vaerys.objects.RandomStatusObject;
import com.github.vaerys.pogos.Config;
import com.github.vaerys.pogos.DailyMessages;
import com.github.vaerys.pogos.GlobalData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

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
    public static boolean doRandomGames = false;
    public static String dailyAvatarName = null;
    public static String playing = null;
    public static long queueChannelID = -1;
    public static int argsMax = 0;
    public static int maxWarnings = 0;
    public static int avgMessagesPerDay = 20;
    public static boolean isReady = false;
    public static String version;
    public static String consoleMessageCID = null;
    public static ArrayList<DailyMessageObject> configDailyMessages = new ArrayList<>();
    public static IDiscordClient client;
    public static boolean showSaveWarning = false;
    public static boolean shuttingDown = false;
    public static boolean savingFiles = false;
    //    private static ArrayList<GuildContentObject> guildContentObjects = new ArrayList<>();
    private static List<GuildObject> guilds = new ArrayList<>();
    public static List<Command> commands = new ArrayList<>();
    //    private static List<Command> commandsDM = new ArrayList<>();
    private static ArrayList<String> commandTypes = new ArrayList<>();
    private static ArrayList<ChannelSetting> channelSettings = new ArrayList<>();
    private static ArrayList<GuildToggle> guildGuildToggles = new ArrayList<>();
    private static ArrayList<SlashCommand> slashCommands = new ArrayList<>();
    private static ArrayList<RandomStatusObject> randomStatuses = new ArrayList<>();
    private static List<String> blacklistedURls;


    final static Logger logger = LoggerFactory.getLogger(Globals.class);
    private static GlobalData globalData;
    private static DailyMessages dailyMessages;
    public static int baseXPModifier;
    public static int xpForLevelOne;
    public static long lastDmUserID = -1;
    public static Color pixelColour = new Color(226, 218, 117);
    private static ArrayList<Command> creatorCommands = new ArrayList<>();

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
        configDailyMessages = config.dailyMessages;
        baseXPModifier = config.baseXpModifier;
        xpForLevelOne = config.xpForLevelOne;
        avgMessagesPerDay = config.avgMessagesPerDay;
        doRandomGames = config.doRandomGames;
        showSaveWarning = config.showSaveWarning;
        randomStatuses = config.randomStatuses;
        queueChannelID = config.queueChannelID;
        blacklistedURls = FileHandler.readFromFile("website.blacklist");
        dailyMessages = (DailyMessages) DailyMessages.create(DailyMessages.FILE_PATH, new DailyMessages());
        initCommands();
    }

    private static void initCommands() {
        //Load Commands
        commands = CommandInit.get();
        //Load DM Commands
//        commandsDM = CommandInit.getDM();
        //Load Guild Toggles
        guildGuildToggles = ToggleInit.get();

        slashCommands = CommandInit.getSlashCommands();

        channelSettings = InitChannels.get();

        creatorCommands = CommandInit.getCreatorCommands();

        //validate commands
        validate();

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
        Collections.sort(commandTypes);

        logger.info(commands.size() + " Commands Loaded.");
        logger.info(commandTypes.size() + " Command Types Loaded.");
        logger.info(channelSettings.size() + " Channel Types Loaded.");
        logger.info(guildGuildToggles.size() + " Guild Toggles Loaded.");
        logger.info(creatorCommands.size() + " Creator Commands Loaded.");
    }

    private static void validate() throws IllegalArgumentException {
        for (Command c : commands) {
            logger.trace("Validating Command: " + c.getClass().getName());
            String errorReport = c.validate();
            if (errorReport != null) {
                logger.error(errorReport);
                System.exit(-1);
            }
        }
        for (Command c : creatorCommands) {
            logger.trace("Validating Command: " + c.getClass().getName());
            String errorReport = c.validate();
            if (errorReport != null) {
                logger.error(errorReport);
                System.exit(-1);
            }
        }
        for (Command c : slashCommands) {
            logger.trace("Validating Command: " + c.getClass().getName());
            String errorReport = c.validate();
            if (errorReport != null) {
                logger.error(errorReport);
                System.exit(-1);
            }
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
        for (ChannelSetting s : channelSettings) {
            if (s.type() == null || s.type().isEmpty()) {
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
            for (DailyMessageObject d : configDailyMessages) {
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

    public static void initGuild(GuildObject guild) {
        for (GuildObject g : guilds) {
            if (g.longID == guild.longID) {
                return;
            }
        }
        guild.config.updateVariables(guild.get());
        guilds.add(guild);
    }

    public static void setVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(Main.class.getClassLoader().getResourceAsStream("project.properties"));
            version = properties.getProperty("version");
            logger.info("Bot version : " + version);
        } catch (IOException e) {
            Utility.sendStack(e);
        }
    }

    public static IDiscordClient getClient() {
        return client;
    }

    public static GuildObject getGuildContent(long guildID) {
        for (GuildObject g : guilds) {
            if (g.longID == guildID) {
                return g;
            }
        }
        return new GuildObject();
    }

    public static List<GuildObject> getGuilds() {
        return guilds;
    }

    public static void saveFiles() {
        if (shuttingDown) {
            return;
        }
        savingFiles = true;
        logger.debug("Saving Files.");
        dailyMessages.flushFile();
        for (GuildObject g : guilds) {
            for (GuildFile file : g.guildFiles) {
                file.flushFile();
            }
        }
        FileHandler.writeToJson(Constants.FILE_GLOBAL_DATA, getGlobalData());
        savingFiles = false;
    }

    public static void unloadGuild(long id) {
        ListIterator iterator = guilds.listIterator();
        while (iterator.hasNext()) {
            GuildObject guild = (GuildObject) iterator.next();
            if (guild.longID == id) {
                logger.trace("Guild: " + guild.get().getName() + " unloaded.");
                iterator.remove();
            }
        }
    }

    public static List<Command> getCommands(boolean isDm) {
        List<Command> getCommands = new ArrayList<>();
        for (Command c : commands) {
            if (isDm) {
                if (c.channel() != null && c.channel().equalsIgnoreCase(Command.CHANNEL_DM)) getCommands.add(c);
            } else {
                if (c.channel() == null || !c.channel().equalsIgnoreCase(Command.CHANNEL_DM)) getCommands.add(c);

            }
        }
        return getCommands;
    }

    public static List<Command> getAllCommands() {
        List<Command> allCommands = new ArrayList<>(commands);
        allCommands.addAll(creatorCommands);
        return allCommands;
    }

    public static GlobalData getGlobalData() {
        if (globalData != null) {
            return globalData;
        } else {
            return null;
        }
    }

//    public static List<Command> getCommandsDM() {
//        return commandsDM;
//    }

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

    public static ArrayList<RandomStatusObject> getRandomStatuses() {
        return randomStatuses;
    }

    public static List<String> getBlacklistedURls() {
        return blacklistedURls;
    }

    public static DailyMessages getDailyMessages() {
        return dailyMessages;
    }

    public static List<Command> getCreatorCommands(boolean isDm) {
        List<Command> getCommands = new ArrayList<>();
        for (Command c : creatorCommands) {
            if (isDm) {
                if (c.channel() != null && c.channel().equalsIgnoreCase(Command.CHANNEL_DM)) getCommands.add(c);
            } else {
                if (c.channel() == null || !c.channel().equalsIgnoreCase(Command.CHANNEL_DM)) getCommands.add(c);

            }
        }
        return getCommands;
    }

    public static List<Command> getALLCreatorCommands() {
        return creatorCommands;
    }
}
