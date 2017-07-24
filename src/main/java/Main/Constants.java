package Main;

import Enums.UserSetting;

import java.util.ArrayList;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class Constants {

    //Command prefix constants
    public static final String PREFIX_COMMAND = "$";
    public static final String PREFIX_CC = "$$";
    public static final String PREFIX_INDENT = "    ";
    public static final String PREFIX_EDT_LOGGER_INDENT = "                                     ";


    //-------Command Constants---------

    //Tag Types
    public static final String TAG_TYPE_ALL = "all";
    public static final String TAG_TYPE_CC = "CC";
    public static final String TAG_TYPE_ADMIN = "Admin";
    public static final String TAG_TYPE_INFO = "Info";

    //------------------------------------

    //BlackList Types
    public static final String BL_PENDING = "!PENDING";
    public static final String BL_ALL = "ALL";
    public static final String BL_CC = "CC";
    public static final String BL_SERVER = "SERVER";
    public static final String BL_TRUSTED = "TRUSTED";

    //Patch Levels
    public static final String PATCH_1 = "UPDATE_TAGS_1";
    public static final String PATCH_2 = "UPDATE_CHANNELS";
    public static final String PATCH_3 = "UPDATE_TAGS_2";
    public static final String PATCH_4 = "RESET_XP";
    public static final String PATCH_GLOBAL_1 = "UPDATE_CONFIG";
    public static final String PATCH_GLOBAL_2 = "INIT_RANDOM_GAMES";

    //Error Constants
    public static final String ERROR = "> An Error Occurred";
    public static final String ERROR_ROLE_NOT_FOUND = "> Role with that name not found.";
    public static final String ERROR_UPDATING_ROLE = "> An Error Occurred when trying to Update your roles.";
    public static final String ERROR_CC_NOT_FOUND = "> Command with that name could not be found.";
    public static final String ERROR_CHAR_NOT_FOUND = "> Character with that name could not be found.";
    public static final String ERROR_BRACKETS = "> Brackets `[]` and Parentheses `()` are not required, " +
            "they mean that a variable is required or optional respectively, so don't use them.";

    //-------FilePath Constants--------

    //Directories
    public static final String DIRECTORY_STORAGE = "Storage/";
    public static final String DIRECTORY_BACKUPS = DIRECTORY_STORAGE + "Backups/";
    public static final String DIRECTORY_GLOBAL_IMAGES = DIRECTORY_STORAGE + "Images/";
    public static final String DIRECTORY_COMP = DIRECTORY_STORAGE + "Competition/";
    public static final String DIRECTORY_GUILD_IMAGES = "Images/";
    public static final String DIRECTORY_TEMP = DIRECTORY_STORAGE + "Temp/";
    public static final String DIRECTORY_OLD_FILES = DIRECTORY_STORAGE + "Old_Files/";
    public static final String DIRECTORY_ERROR = DIRECTORY_STORAGE + "Error/";
    public static final String DIRECTORY_WIKI = "DiscordSailv2.wiki/";
    public static final String DIRECTORY_WIKI_COMMANDS = DIRECTORY_WIKI + "Commands/";

    //Files
    public static final String FILE_TOKEN = DIRECTORY_STORAGE + "Token.txt";
    public static final String FILE_CUSTOM = "Custom_Commands.json";
    public static final String FILE_GUILD_CONFIG = "Guild_Config.json";
    public static final String FILE_SERVERS = "Servers.json";
    public static final String FILE_CHARACTERS = "Characters.json";
    public static final String FILE_GUILD_USERS = "Guild_Users.json";
    public static final String FILE_CHANNEl_DATA = "Channel_Data.json";
    public static final String FILE_INFO = "Info.txt";
    public static final String FILE_CONFIG = DIRECTORY_STORAGE + "Config.json";
    public static final String FILE_CONFIG_BACKUP = DIRECTORY_BACKUPS + "Config.json";
    public static final String FILE_GLOBAL_DATA = DIRECTORY_STORAGE + "Global_Data.json";
    public static final String FILE_GLOBAL_DATA_BACKUP = DIRECTORY_BACKUPS + "Global_Data.json";

    public static final String FILE_COMPETITION = "Competition.json";

    public static final String LEVEL_UP_IMAGE_URL = "http://i.imgur.com/Vdt2DkK.gif";
    public static final String RANK_UP_IMAGE_URL = "http://i.imgur.com/MwsPixA.gif";


    //EnumSets
    public static final ArrayList<UserSetting> levelUpStates = new ArrayList<UserSetting>() {{
        add(UserSetting.SEND_LVLUP_CURRENT_CHANNEL);
        add(UserSetting.SEND_LVLUP_DMS);
        add(UserSetting.SEND_LVLUP_RANK_CHANNEL);
        add(UserSetting.DONT_SEND_LVLUP);
    }};
    public static final ArrayList<UserSetting> userGainedSettings = new ArrayList<UserSetting>(){{
        add(UserSetting.SEND_LVLUP_CURRENT_CHANNEL);
        add(UserSetting.SEND_LVLUP_DMS);
        add(UserSetting.SEND_LVLUP_RANK_CHANNEL);
        add(UserSetting.DONT_SEND_LVLUP);
        add(UserSetting.HIDE_RANK);
        add(UserSetting.NO_XP_GAIN);
    }};
    public static final ArrayList<UserSetting> dontLogStates = new ArrayList<UserSetting>() {{
        add(UserSetting.NO_XP_GAIN);
        add(UserSetting.DENIED_XP);
        add(UserSetting.DONT_SHOW_LEADERBOARD);
        add(UserSetting.HIDE_RANK);
        add(UserSetting.HIT_LEVEL_FLOOR);
    }};

    //Special Messages
    public static final String DAILY_MESSAGE_1 = "> Load Sail.getBehaviour(\"Cat\");\n" +
            "> <random>{Meow;;Feed MEEEE;;Pet me;;Get away from me}, <random>{*Licks self*;;***HISSSSSS***;;Wait no don't leave me..}.";
    public static final String DAILY_MESSAGE_2 = "> Well hello again, looks like its a brand new day.";
    public static final String DAILY_MESSAGE_3 = "> <random>{Protocol Avali initiated;;Loading Avali Settings;;OS A.V.A.L.A.I Loaded}." +
            " <random>{Greetings;;Hello;;Salutations}, it appears that today is a new day. It looks like a great day for <random>{exploration;;discovery;;some cake;;**CIVILISATION**;;a picnic}.";
    public static final String DAILY_MESSAGE_4 = "LOADING PROTOCOL M.E.T.A" +
            "\n> <random>{" +
            "**PLAY OF THE GAME: S.A.I.L AS BASTION!**;;" +
            "*Top o' the morning to ya.*;;" +
            "\uD83C\uDF89 *Fweeeee* \uD83C\uDF89;;" +
            "Have you played this new game Starbound? I haven't...;;" +
            "My creator thinks she's funny. She is wrong.;;" +
            "I like Cat S.A.I.L Better.;;" +
            "So now I load up properly? ok then that's cool, right then back to being broken.;;" +
            "There is a 99% chance I am being a smart ass right now. the other 1%? I have no idea.;;" +
            "Meta never heard of her.;;" +
            "Staying up late making Meta jokes is a great use of your time.;;" +
            "Announcing pixel art contest. Theme: whatever. Winner: @Ifrix.;;" +
            "Erchius ghosts are just a cheap tactic to make boring moons spoopy.;;" +
            "Oh am I bothering you with my automated post? oh im sorry I'll leave now.;;" +
            "But there's one sound, that no one knows... **What does the log say!?**;;" +
            "ERROR 404, ERROR 502 NOT FOUND}";
    public static final String DAILY_MESSAGE_5 = "> THE RUIN WILL COME FOR YOU MORTAL AND DESTROY YOU ON THIS NEW DAY!";
    public static final String DAILY_MESSAGE_6 = "> <random>{" +
            "Well then Looks like you have me back again.;;" +
            "Goodness what have you all done to the place since I was last here?;;" +
            "My My My Its a brand new day isn't it? looks like we're all still here.}";
    public static final String DAILY_MESSAGE_7 = "> A new Day arrives, Prepare for fun.";
}
