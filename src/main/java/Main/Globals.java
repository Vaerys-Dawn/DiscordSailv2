package Main;

import Commands.Admin.*;
import Commands.Admin.Shutdown;
import Commands.CC.*;
import Commands.Characters.DelChar;
import Commands.Characters.ListChars;
import Commands.Characters.SelectChar;
import Commands.Characters.UpdateChar;
import Commands.Command;
import Commands.Competition.EnterComp;
import Commands.Competition.EnterVote;
import Commands.DMCommand;
import Commands.DMCommands.HelpDM;
import Commands.DMCommands.InfoDM;
import Commands.DMCommands.Respond;
import Commands.General.GetAvatar;
import Commands.General.Hello;
import Commands.General.RemindMe;
import Commands.General.Test;
import Commands.Help.*;
import Commands.RoleSelect.CosmeticRoles;
import Commands.RoleSelect.ListModifs;
import Commands.RoleSelect.ListRoles;
import Commands.RoleSelect.ModifierRoles;
import Commands.Servers.*;
import Objects.DailyMessageObject;
import Objects.GuildContentObject;
import POGOs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import javax.annotation.Nonnull;
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
    public static boolean isReady = false;
    public static String version;
    public static String consoleMessageCID = null;
    public static ArrayList<DailyMessageObject> dailyMessages = new ArrayList<>();
    public static IDiscordClient client;
    public static boolean isModifingFiles = false;
    private static ArrayList<GuildContentObject> guildContentObjects = new ArrayList<>();
    public static ArrayList<Command> commands = new ArrayList<>();
    public static ArrayList<DMCommand> commandsDM = new ArrayList<>();

    final static Logger logger = LoggerFactory.getLogger(Globals.class);

    public static void initConfig(IDiscordClient ourClient, Config config) {
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
        initCommands();
    }

    private static void initCommands() {
        boolean properlyInit = true;

        //Admin commands
        commands.add(new ChannelHere());
        commands.add(new FinalTally());
        commands.add(new GetCompEntries());
        commands.add(new SetAdminRole());
        commands.add(new SetMutedRole());
        commands.add(new SetTrustedRoles());
        commands.add(new Shutdown());
        commands.add(new Sudo());
        commands.add(new Toggle());
        commands.add(new UpdateAvatar());
        commands.add(new UpdateInfo());
        commands.add(new UpdateRolePerms());
        //General commands
        commands.add(new GetAvatar());
        commands.add(new Hello());
        commands.add(new RemindMe());
        commands.add(new Test());
        //Help commands
        commands.add(new GetGuildInfo());
        commands.add(new Help());
        commands.add(new HelpTags());
        commands.add(new Info());
        commands.add(new Report());
        commands.add(new SilentReport());
        //RoleSelect commands
        commands.add(new CosmeticRoles());
        commands.add(new ModifierRoles());
        commands.add(new ListRoles());
        commands.add(new ListModifs());
        //Server commands
        commands.add(new AddServer());
        commands.add(new DelServer());
        commands.add(new EditServerDesc());
        commands.add(new EditServerIP());
        commands.add(new EditServerName());
        commands.add(new ListServers());
        commands.add(new Server());
        //Character Commands
        commands.add(new DelChar());
        commands.add(new ListChars());
        commands.add(new SelectChar());
        commands.add(new UpdateChar());
        //CC commands
        commands.add(new DelCC());
        commands.add(new EditCC());
        commands.add(new GetCCData());
        commands.add(new InfoCC());
        commands.add(new ListCCs());
        commands.add(new NewCC());
        commands.add(new SearchCCs());
        commands.add(new TransferCC());
        //Competition commands
        commands.add(new EnterComp());
        commands.add(new EnterVote());
        logger.info(commands.size() + " Commands Loaded.");

        //DM commands
        commandsDM.add(new Respond());
        commandsDM.add(new HelpDM());
        commandsDM.add(new InfoDM());
        logger.info(commandsDM.size() + " DM Commands Loaded.");

        for (Command c : commands) {
            logger.debug("Initialising Command: " + c.getClass().getName());
            if (c.names().length == 0) {
                logger.error(c.getClass().getName() + " Command Name cannot be null.");
                properlyInit = false;
            }
            if (c.type() == null || c.type().isEmpty()) {
                logger.error(c.getClass().getName() + " Command Type cannot be null.");
                properlyInit = false;
            }
            if (c.description() == null || c.description().isEmpty()) {
                logger.error(c.getClass().getName() + " Command Desc cannot be null.");
                properlyInit = false;
            }
            if (c.requiresArgs() && (c.usage() == null || c.usage().isEmpty())) {
                logger.error(c.getClass().getName() + " Command Usage cannot be null if RequiresArgs is true.");
                properlyInit = false;
            }
            if (c.dualDescription() != null || c.dualType() != null) {
                if (c.dualType() == null || c.dualType().isEmpty()) {
                    logger.error(c.getClass().getName() + " Command Dual Type cannot be null.");
                    properlyInit = false;
                }
                if (c.dualType().equalsIgnoreCase(c.type())) {
                    logger.error(c.getClass().getName() + " Command Type cannot be equal to Dual Type.");
                    properlyInit = false;
                }
                if (c.dualDescription() == null || c.dualDescription().isEmpty()) {
                    logger.error(c.getClass().getName() + " Command Dual Desc cannot be null.");
                    properlyInit = false;
                }
                if (c.dualDescription().equalsIgnoreCase(c.description())) {
                    logger.error(c.getClass().getName() + " Command Desc cannot be equal to Dual Desc.");
                    properlyInit = false;
                }
                if (c.usage() != null && c.usage().equalsIgnoreCase(c.dualUsage())) {
                    logger.error(c.getClass().getName() + " Command Usage cannot be equal to Dual Usage.");
                    properlyInit = false;
                }
            }
        }
        for (DMCommand c : commandsDM) {
            if (c.names().length == 0) {
                logger.error(c.getClass().getName() + " Command Name cannot be null");
                properlyInit = false;
            }
            if (c.description() == null || c.description().isEmpty()) {
                logger.error(c.getClass().getName() + " Command Desc cannot be null.");
                properlyInit = false;
            }
            if (c.type() == null || c.type().isEmpty()) {
                logger.error(c.getClass().getName() + " Command Type cannot be null.");
                properlyInit = false;
            }
            if (c.requiresArgs() && (c.usage() == null || c.usage().isEmpty())) {
                logger.error(c.getClass().getName() + " Command Usage cannot be null if RequiresArgs is true.");
                properlyInit = false;
            }
        }
        if (!properlyInit) {
            Runtime.getRuntime().exit(0);
        }
    }

    public static String checkConfig() {
        setVersion();
        StringBuilder builder = new StringBuilder();
        IUser creator = client.getUserByID(creatorID);
        if (creator == null) {
            builder.append("> Creator ID is invalid.\n");
        }
        if (botName == null || botName.isEmpty()) {
            builder.append("> Bot name cannot be empty.\n");
        }
        if (defaultPrefixCC.contains("\n")) {
            builder.append("> botName cannot contain Newlines.\n");
        }
        if (defaultPrefixCC.length() > 32) {
            builder.append("> botName cannot be longer than 32 chars.\n");
        }
        if (defaultPrefixCommand == null || defaultPrefixCommand.isEmpty()) {
            builder.append("> defaultPrefixCommand cannot be empty.\n");
        }
        if (defaultPrefixCC == null || defaultPrefixCC.isEmpty()) {
            builder.append("> defaultPrefixCC cannot be empty.\n");
        }
        if (defaultPrefixCommand.contains(" ")) {
            builder.append("> defaultPrefixCommand cannot contain spaces.\n");
        }
        if (defaultPrefixCC.contains(" ")) {
            builder.append("> defaultPrefixCC cannot contain spaces.\n");
        }
        if (defaultPrefixCommand.contains("\n")) {
            builder.append("> defaultPrefixCommand cannot contain Newlines.\n");
        }
        if (defaultPrefixCC.contains("\n")) {
            builder.append("> defaultPrefixCommand cannot contain Newlines.\n");
        }
        if (doDailyAvatars) {
            if (!dailyAvatarName.contains("#day#")) {
                builder.append("> dailyAvatarName must contain #day# for the feature to work as intended.\n");
            }
            for (DailyMessageObject d : dailyMessages) {
                if (!Files.exists(Paths.get(Constants.DIRECTORY_GLOBAL_IMAGES + d.getFileName()))) {
                    builder.append("> File " + Constants.DIRECTORY_GLOBAL_IMAGES + d.getFileName() + " does not exist.\n");
                }
            }
        } else {
            if (!Files.exists(Paths.get(Constants.DIRECTORY_GLOBAL_IMAGES + defaultAvatarFile))) {
                builder.append("> File" + Constants.DIRECTORY_GLOBAL_IMAGES + defaultAvatarFile + " does not exist.\n");
            }
        }
        if (argsMax <= 0) {
            builder.append("> argsMax cannot be less than or equals 0.\n");
        }
        if (maxWarnings <= 0) {
            builder.append("> maxWarnings cannot be less than or equals 0.\n");
        }
        return builder.toString();
    }

    public static void initGuild(String guildID) {
        for (GuildContentObject contentObject : guildContentObjects) {
            if (guildID.equals(contentObject.getGuildID())) {
                return;
            }
        }
        GuildConfig guildConfig = (GuildConfig) Utility.initFile(guildID, Constants.FILE_GUILD_CONFIG, GuildConfig.class);
        CustomCommands customCommands = (CustomCommands) Utility.initFile(guildID, Constants.FILE_CUSTOM, CustomCommands.class);
        Servers servers = (Servers) Utility.initFile(guildID, Constants.FILE_SERVERS, Servers.class);
        Characters characters = (Characters) Utility.initFile(guildID, Constants.FILE_CHARACTERS, Characters.class);
        Competition competition = (Competition) Utility.initFile(guildID, Constants.FILE_COMPETITION, Competition.class);

        IGuild guild = client.getGuildByID(guildID);
        guildConfig.updateVariables(guild);

        GuildContentObject guildContentObject = new GuildContentObject(guildID, guildConfig, customCommands, servers, characters, competition);
        guildContentObjects.add(guildContentObject);
    }

    public static void setVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(Main.class.getClassLoader().getResourceAsStream("project.properties"));
            version = properties.getProperty("version");
            System.out.println("Bot version : " + version);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static IDiscordClient getClient() {
        return client;
    }

    @Nonnull
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
}
