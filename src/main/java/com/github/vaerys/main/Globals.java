package com.github.vaerys.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.vaerys.commands.CommandInit;
import com.github.vaerys.guildtoggles.ToggleInit;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.DailyMessage;
import com.github.vaerys.objects.LogObject;
import com.github.vaerys.objects.RandomStatusObject;
import com.github.vaerys.objects.TimedEvent;
import com.github.vaerys.pogos.Config;
import com.github.vaerys.pogos.DailyMessages;
import com.github.vaerys.pogos.Events;
import com.github.vaerys.pogos.GlobalData;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildFile;
import com.github.vaerys.templates.GuildToggle;
import com.github.vaerys.templates.SlashCommand;
import com.github.vaerys.templates.TagObject;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class Globals {

    public static String botName = null;
    public static long creatorID = -1;
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
    public static long consoleMessageCID = -1;
    public static ArrayList<DailyMessage> configDailyMessages = new ArrayList<>();
    public static IDiscordClient client;
    public static boolean showSaveWarning = false;
    public static boolean shuttingDown = false;
    public static boolean savingFiles = false;
    private static List<GuildObject> guilds = new LinkedList<>();
    public static List<Command> commands = new LinkedList<>();
    private static List<String> commandTypes = new LinkedList<>();
    private static ChannelSetting[] channelSettings;
    private static List<GuildToggle> guildToggles = new LinkedList<>();
    private static List<SlashCommand> slashCommands = new LinkedList<>();
    private static List<RandomStatusObject> randomStatuses = new LinkedList<>();
    private static List<LogObject> allLogs = new LinkedList<>();
    private static List<TagObject> tags = new LinkedList<>();
    private static List<String> blacklistedURls;


    final static Logger logger = LoggerFactory.getLogger(Globals.class);
    private static GlobalData globalData;
    private static DailyMessages dailyMessages;
    public static int baseXPModifier;
    public static int xpForLevelOne;
    public static long lastDmUserID = -1;
    private static ArrayList<Command> creatorCommands = new ArrayList<>();
    private static List<Long> patrons = new ArrayList<>();
    public static int maxReminderSlots = 5;
    private static Events events;
    private static String currentEvent = null;
    public static String errorStack = null;


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
        events = (Events) Events.create(Events.FILE_PATH, new Events());
        updateEvent();
        initCommands();
    }

    private static void initCommands() {
        //Load Commands
        commands = CommandInit.get();
        //Load DM Commands


        // Load Guild Toggles
        guildToggles = ToggleInit.get();

        channelSettings = ChannelSetting.values();

        creatorCommands = CommandInit.getCreatorCommands();

        TagList.init();

        //validate commands
        if (errorStack != null) {
            logger.error("\n>> Begin Error Report <<\n" + errorStack + ">> End Error Report <<");
            System.exit(Constants.EXITCODE_CONF_ERROR);
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
        logger.info(creatorCommands.size() + " Creator Commands Loaded.");
        logger.info(commandTypes.size() + " Command Types Loaded.");
        logger.info(channelSettings.length + " Channel Types Loaded.");
        logger.info(guildToggles.size() + " Guild Toggles Loaded.");
        logger.info(TagList.get().size() + " Tags Loaded.");
    }

    public static void validateConfig() throws IllegalArgumentException {
        if (botName == null || botName.isEmpty())
            addToErrorStack("   > botName cannot be empty.\n");
        if (botName.contains("\n"))
            addToErrorStack("   > botName cannot contain Newlines.\n");
        if (botName.length() > 32)
            addToErrorStack("   > botName cannot be longer than 32 chars.\n");
        if (defaultPrefixCommand == null || defaultPrefixCommand.isEmpty())
            addToErrorStack("   > defaultPrefixCommand cannot be empty.\n");
        if (defaultPrefixCC == null || defaultPrefixCC.isEmpty())
            addToErrorStack("   > defaultPrefixCC cannot be empty.\n");
        if (defaultPrefixCommand.contains(" "))
            addToErrorStack("   > defaultPrefixCommand cannot contain spaces.\n");
        if (defaultPrefixCC.contains(" "))
            addToErrorStack("   > defaultPrefixCC cannot contain spaces.\n");
        if (defaultPrefixCommand.contains("\n"))
            addToErrorStack("   > defaultPrefixCommand cannot contain Newlines.\n");
        if (defaultPrefixCC.contains("\n"))
            addToErrorStack("   > defaultPrefixCommand cannot contain Newlines.\n");
        if (doDailyAvatars) {
            if (dailyAvatarName == null || dailyAvatarName.isEmpty())
                addToErrorStack("   > dailyAvatarName cannot be empty.\n");
            if (!dailyAvatarName.contains("#day#"))
                addToErrorStack("   > dailyAvatarName must contain #day# for the feature to work as intended.\n");
            if (!Utility.isImageLink(dailyAvatarName)) {
                addToErrorStack("   > dailyAvatarName must be a valid image link.\n");
            }
            for (DayOfWeek d : DayOfWeek.values()) {
                String dailyPath = Constants.DIRECTORY_GLOBAL_IMAGES + dailyAvatarName.replace("#day#", d.toString());
                if (!Files.exists(Paths.get(dailyPath)))
                    addToErrorStack("   > File " + dailyPath + " does not exist.\n");
            }
        } else {
            if (!Utility.isImageLink(defaultAvatarFile)) {
                addToErrorStack("   > defaultAvatarFile must be a valid image link.\n");
            }
            if (!Files.exists(Paths.get(Constants.DIRECTORY_GLOBAL_IMAGES + defaultAvatarFile)))
                addToErrorStack("   > File " + Constants.DIRECTORY_GLOBAL_IMAGES + defaultAvatarFile + " does not exist.\n");
        }
        if (argsMax <= 0)
            addToErrorStack("   > argsMax cannot be less than or equal 0.\n");
        if (maxWarnings <= 0)
            addToErrorStack("   > maxWarnings cannot be less than or equal 0.\n");
        if (avgMessagesPerDay <= 0) {
            addToErrorStack("   > avgMessagesPerDay cannot be less than or equal 0.\n");
        }
    }

    public static boolean isCreatorValid() {
        IUser creator = Client.getClient().fetchUser(creatorID);
        if (creator == null) {
            logger.error("\nError:" +
                    "   > creatorID is invalid.");
            return false;
        }
        return true;
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

    public static void saveFiles(boolean shuttingDown) {
        if (shuttingDown) Globals.shuttingDown = true;
        else if (Globals.shuttingDown || Globals.savingFiles) return;
        savingFiles = true;
        logger.debug("Saving Files.");
        //global files
        if (dailyMessages != null) dailyMessages.flushFile();
        if (events != null) events.flushFile();
        if (globalData != null) globalData.flushFile();
        //guild files
        for (GuildObject g : guilds) {
            for (GuildFile file : g.guildFiles) {
                file.flushFile();
            }
        }
        savingFiles = false;
    }

    public static void backupAll() {
        if (shuttingDown) {
            return;
        }
        savingFiles = true;
        logger.debug("Backing up Files.");
        //global files
        if (dailyMessages != null) dailyMessages.backUp();
        if (events != null) events.backUp();
        if (globalData != null) globalData.backUp();
        //guild files
        for (GuildObject g : guilds) {
            for (GuildFile file : g.guildFiles) {
                file.backUp();
            }
        }
        savingFiles = false;
    }

    public static void unloadGuild(long id) {
        ListIterator iterator = guilds.listIterator();
        while (iterator.hasNext()) {
            GuildObject guild = (GuildObject) iterator.next();
            if (guild.longID == id) {
                logger.info("Guild: " + guild.get().getName() + " unloaded.");
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

    public static List<String> getCommandTypes() {
        return commandTypes;
    }

    public static ChannelSetting[] getChannelSettings() {
        return channelSettings;
    }

    public static List<GuildToggle> getGuildToggles() {
        return guildToggles;
    }

    public static List<SlashCommand> getSlashCommands() {
        return slashCommands;
    }

    public static List<RandomStatusObject> getRandomStatuses() {
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

    public static List<TagObject> getTags() {
        return tags;
    }

    public static void setPatrons(List<Long> patrons) {
        Globals.patrons = patrons;
    }

    public static List<Long> getPatrons() {
        return patrons;
    }

    public static List<TimedEvent> getEvents() {
        return events.getEvents();
    }

    public static Events getEvent() {
        return events;
    }

    public static TimedEvent getCurrentEvent() {
        for (TimedEvent e : events.getEvents()) {
            if (e.getEventName().equalsIgnoreCase(currentEvent)) {
                return e;
            }
        }
        return null;
    }

    public static void updateEvent() {
        boolean eventFound = false;
        for (TimedEvent e : events.getEvents()) {
            e.sanitizeDates();
            if (e.isEventActive()) {
                eventFound = true;
                currentEvent = e.getEventName();
            }
        }
        if (eventFound == false) {
            currentEvent = null;
        }
    }

    public static DailyMessage getDailyMessage(DayOfWeek day) {
        for (DailyMessage d : configDailyMessages) {
            if (d.getDay() == day)
                return d;
        }
        return null;
    }

    public static void addToErrorStack(String errorReport) {
        if (errorReport == null) return;
        if (errorStack == null) {
            errorStack = errorReport;
        } else {
            errorStack += errorReport;
        }
    }

    public static void addToLog(LogObject object) {
        allLogs.add(object);
        if (allLogs.size() > 5) allLogs.remove(0);
    }

    public static List<LogObject> getAllLogs() {
        return allLogs;
    }
}
