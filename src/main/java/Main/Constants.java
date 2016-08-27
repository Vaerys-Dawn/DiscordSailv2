package Main;

import java.util.ArrayList;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class Constants {

    //Command prefix constants
    public static final String COMMAND_PREFIX = "$";
    public static final String CC_PREFIX = "$$";


    //-------Command Constants---------

    //Type Constants
    public static final String TYPE_GENERAL = "General";
    public static final String TYPE_ADMIN = "Admin";
    public static final String TYPE_ROLE_SELECT = "Races";
    public static final String TYPE_SERVERS = "Servers";
    public static final String TYPE_CC = "CC";


    //Channel Constants
    public static final String CHANNEL_GENERAL = "General";
    public static final String CHANNEL_ANY = "Any";
    public static final String CHANNEL_SERVERS = "Servers";
    public static final String CHANNEL_RACE_SELECT = "RaceSelect";
    public static final String CHANNEL_LOGGING = "Logging";
    public static final String CHANNEL_ADMIN = "Admin";

    //------------------------------------

    //Error Constants
    public static final String GC_NOT_FOUND = "Error";

    //-------FilePath Constants--------

    //Directories
    public static final String DIRECTORY_STORAGE = "Storage/";

    //Files
    public static final String FILE_TOKEN = DIRECTORY_STORAGE+"Token.txt";
    public static final String FILE_CUSTOM = "Custom_Commands.json";
    public static final String FILE_GUILD_CONFIG = "Guild_Config.json";
    public static final String FILE_SERVERS = "Servers.json";
    public static final String FILE_CHARACTERS = "Characters.json";
    public static final String FILE_INFO = "Info.txt";

    public static ArrayList<String[]> FilePaths = new ArrayList<>();


    //Full Paths
}
