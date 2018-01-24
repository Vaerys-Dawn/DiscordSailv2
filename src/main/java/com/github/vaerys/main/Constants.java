package com.github.vaerys.main;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.StartUpGuide;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.objects.DailyMessage;
import com.sun.org.apache.xerces.internal.xpointer.XPointerHandler;

import java.time.DayOfWeek;
import java.util.ArrayList;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class Constants {

    //Command prefix constants
    public static final String PREFIX_INDENT = "    ";
    public static final String PREFIX_EDT_LOGGER_INDENT = "                                     ";

    //BlackList Types
    public static final String BL_PENDING = "!PENDING";
    public static final String BL_ALL = "ALL";
    public static final String BL_CC = "CC";
    public static final String BL_SERVER = "SERVER";
    public static final String BL_TRUSTED = "TRUSTED";

    //Error Constants
    public static final String ERROR = "> An Error Occurred";
    public static final String ERROR_ROLE_NOT_FOUND = "> I found no roles with that name.";
    public static final String ERROR_UPDATING_ROLE = "> An Error Occurred when trying to Update your roles.";
    public static final String ERROR_CC_NOT_FOUND = "> Command with that name could not be found.";
    public static final String ERROR_CHAR_NOT_FOUND = "> Character with that name could not be found.";
    public static final String ERROR_BRACKETS = "> Brackets `[]` and Parentheses `()` are not required, " +
            "they mean that a variable is required or optional respectively, so don't use them.";

    // constants reperesenting different exit codes
    public static final short EXITCODE_NORMAL = 0;
    public static final short EXITCODE_RESTART = 1;
    public static final short EXITCODE_CONF_ERROR = 2;
    public static final short EXITCODE_OTHER_ERROR = 3;
    public static final short EXITCODE_UNKNOWN = 255;


    public static String getWelcomeMessage(CommandObject object) {
        return "> I am S.A.I.L, your Server-Based Artificial Intelligence Lattice. I help manage servers.\n" +
                "I am also programmed to offer you information and tools.\n\n" +
                "The **" + new StartUpGuide().getCommand(object) + "** command will tell you more.\n\n" +
                "`(This message will remove itself in 5 mins)`";
    }

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

    //Files
    public static final String FILE_TOKEN = DIRECTORY_STORAGE + "Token.txt";
    public static final String FILE_PATREON_TOKEN = DIRECTORY_STORAGE + "Patreon_Token.txt";
    public static final String FILE_PASTEBIN_TOKEN = DIRECTORY_STORAGE + "Pastebin_Token.txt";
    public static final String FILE_IMGUR_TOKEN = DIRECTORY_STORAGE + "Imgur_Token.txt";
    public static final String FILE_RPC_TOKEN = DIRECTORY_STORAGE + "Rich_token.txt";
    public static final String FILE_INFO = "Info.txt";
    public static final String FILE_CONFIG = "Config.json";
    public static final String FILE_GLOBAL_DATA = "Global_Data.json";

    public static final String LEVEL_UP_IMAGE_URL = "http://i.imgur.com/Vdt2DkK.gif";
    public static final String RANK_UP_IMAGE_URL = "http://i.imgur.com/MwsPixA.gif";
    public static final String PATREON_ICON_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/Patreon_logo.svg/1024px-Patreon_logo.svg.png";
    public static final String PIXELS_ICON = "http://i.imgur.com/r5usgN7.png";

    public static final Long LEVEL_CAP = 1000l; //Anything more than this is too much CPU time to calculate
    public static final Long PIXELS_CAP = XpHandler.levelToXP(LEVEL_CAP); //1000 levels.


    //EnumSets
    public static final ArrayList<UserSetting> levelUpStates = new ArrayList<UserSetting>() {{
        add(UserSetting.SEND_LVLUP_CURRENT_CHANNEL);
        add(UserSetting.SEND_LVLUP_DMS);
        add(UserSetting.SEND_LVLUP_RANK_CHANNEL);
        add(UserSetting.DONT_SEND_LVLUP);
    }};
    public static final ArrayList<UserSetting> userGainedSettings = new ArrayList<UserSetting>() {{
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
        add(UserSetting.PRIVATE_PROFILE);
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

    //queue types
    public static final String QUEUE_DAILY = "DAILY_MESSAGE";
    public static final String DAILY_SPECIALID = "Default Daily Message.";

    public static ArrayList<DailyMessage> defaultDailyMessages(long creatorID) {
        ArrayList<DailyMessage> dailyMessages = new ArrayList<DailyMessage>() {{
            add(new DailyMessage(Constants.DAILY_MESSAGE_1, DayOfWeek.MONDAY, creatorID, DAILY_SPECIALID));
            add(new DailyMessage(Constants.DAILY_MESSAGE_2, DayOfWeek.TUESDAY, creatorID, DAILY_SPECIALID));
            add(new DailyMessage(Constants.DAILY_MESSAGE_3, DayOfWeek.WEDNESDAY, creatorID, DAILY_SPECIALID));
            add(new DailyMessage(Constants.DAILY_MESSAGE_4, DayOfWeek.THURSDAY, creatorID, DAILY_SPECIALID));
            add(new DailyMessage(Constants.DAILY_MESSAGE_5, DayOfWeek.FRIDAY, creatorID, DAILY_SPECIALID));
            add(new DailyMessage(Constants.DAILY_MESSAGE_6, DayOfWeek.SATURDAY, creatorID, DAILY_SPECIALID));
            add(new DailyMessage(Constants.DAILY_MESSAGE_7, DayOfWeek.SUNDAY, creatorID, DAILY_SPECIALID));
        }};
        return dailyMessages;
    }
}
