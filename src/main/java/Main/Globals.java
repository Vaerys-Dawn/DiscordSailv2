package Main;

import POGOs.Config;
import Objects.DailyMessageObject;
import sx.blah.discord.api.IDiscordClient;
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

    public static boolean resetToDefault = true;
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

    public static void initConfig(IDiscordClient ourClient, Config config){
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
        if (defaultPrefixCC.contains("\n")){
            builder.append("> botName cannot contain Newlines.\n");
        }
        if (defaultPrefixCC.length() > 32){
            builder.append("> botName cannot be longer than 32 chars.\n");
        }
        if (defaultPrefixCommand == null || defaultPrefixCommand.isEmpty()){
            builder.append("> defaultPrefixCommand cannot be empty.\n");
        }
        if (defaultPrefixCC == null || defaultPrefixCC.isEmpty()){
            builder.append("> defaultPrefixCC cannot be empty.\n");
        }
        if (defaultPrefixCommand.contains(" ")){
            builder.append("> defaultPrefixCommand cannot contain spaces.\n");
        }
        if (defaultPrefixCC.contains(" ")){
            builder.append("> defaultPrefixCC cannot contain spaces.\n");
        }
        if (defaultPrefixCommand.contains("\n")){
            builder.append("> defaultPrefixCommand cannot contain Newlines.\n");
        }
        if (defaultPrefixCC.contains("\n")){
            builder.append("> defaultPrefixCommand cannot contain Newlines.\n");
        }
        if (doDailyAvatars) {
            if (!dailyAvatarName.contains("#day#")){
                builder.append("> dailyAvatarName must contain #day# for the feature to work as intended.\n");
            }
            for (DailyMessageObject d : dailyMessages) {
                if (!Files.exists(Paths.get(Constants.DIRECTORY_GLOBAL_IMAGES + d.getFileName()))) {
                    builder.append("> File " + Constants.DIRECTORY_GLOBAL_IMAGES + d.getFileName() + " does not exist.\n");
                }
            }
        }else {
            if (!Files.exists(Paths.get(Constants.DIRECTORY_GLOBAL_IMAGES + defaultAvatarFile))){
                builder.append("> File" + Constants.DIRECTORY_GLOBAL_IMAGES + defaultAvatarFile + " does not exist.\n");
            }
        }
        if (argsMax <= 0) {
            builder.append("> argsMax cannot be less than or equals 0.\n");
        }
        if (maxWarnings <= 0){
            builder.append("> maxWarnings cannot be less than or equals 0.\n");
        }
        return builder.toString();
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

}
