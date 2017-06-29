package POGOs;

import Main.Constants;
import Main.Globals;
import Objects.DailyMessageObject;
import Objects.RandomStatusObject;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Vaerys on 14/01/2017.
 */
public class Config {
    public boolean resetToDefault;
    public String botName;
    public String creatorID;
    public String defaultPrefixCommand;
    public String defaultPrefixCC;
    public String defaultAvatarFile;
    public boolean doDailyAvatars;
    public String dailyAvatarName;
    public String playing;
    public boolean doRandomGames;
    public boolean showSaveWarning;
    public int argsMax = 500;
    public int maxWarnings = 3;
    public int baseXpModifier = 20;
    public int xpForLevelOne = 100;
    public int avgMessagesPerDay = 20;
    public ArrayList<DailyMessageObject> dailyMessages;
    public ArrayList<RandomStatusObject> randomStatuses;


    public boolean initObject() {
        if (resetToDefault) {
            resetToDefault = false;
            botName = "S.A.I.L";
            creatorID = "153159020528533505";
            defaultPrefixCommand = "$";
            defaultPrefixCC = "$$";
            defaultAvatarFile = "Avatar.png";
            doDailyAvatars = false;
            dailyAvatarName = "Avatar_For_#day#.png";
            playing = "Starbound";
            argsMax = 500;
            maxWarnings = 3;
            baseXpModifier = 5;
            xpForLevelOne = 100;
            avgMessagesPerDay = 20;
            doRandomGames = true;
            showSaveWarning = true;
            dailyMessages = new ArrayList<DailyMessageObject>() {{
                add(new DailyMessageObject(DayOfWeek.MONDAY, Constants.DAILY_MESSAGE_1, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.TUESDAY, Constants.DAILY_MESSAGE_2, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.WEDNESDAY, Constants.DAILY_MESSAGE_3, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.THURSDAY, Constants.DAILY_MESSAGE_4, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.FRIDAY, Constants.DAILY_MESSAGE_5, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.SATURDAY, Constants.DAILY_MESSAGE_6, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.SUNDAY, Constants.DAILY_MESSAGE_7, dailyAvatarName));
            }};
            randomStatuses = defaultStatuses();
            return true;
        }
        for (DailyMessageObject d : dailyMessages) {
            d.updateFilePath(dailyAvatarName);
        }
        return false;
    }

    public void setDailyMessages(ArrayList<DailyMessageObject> dailyMessages) {
        this.dailyMessages = dailyMessages;
    }

    public static ArrayList<RandomStatusObject> defaultStatuses() {
         return new ArrayList<RandomStatusObject>() {{
            add(new RandomStatusObject("Starbound", 10));
            add(new RandomStatusObject("WarGroove", 5));
            add(new RandomStatusObject("Stardew Valley", 1));
            add(new RandomStatusObject("Pocket Rumble", 1));
            add(new RandomStatusObject("The Siege and the SandFox", 1));
            add(new RandomStatusObject("Treasure Adventure World", 1));
            add(new RandomStatusObject("Interstellaria", 1));
            add(new RandomStatusObject("Halfway", 1));
            add(new RandomStatusObject("Lenna's Inception", 1));
            add(new RandomStatusObject("Risk of Rain", 1));
            add(new RandomStatusObject("Witchmarsh", 1));
            add(new RandomStatusObject("Wanderlust Adventure", 1));
            add(new RandomStatusObject("Wanderlust Rebirth", 1));
        }};
    }
}


