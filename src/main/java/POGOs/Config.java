package POGOs;

import Main.Constants;
import Objects.DailyMessageObject;
import Objects.RandomStatusObject;

import java.time.DayOfWeek;
import java.util.ArrayList;

/**
 * Created by Vaerys on 14/01/2017.
 */
public class Config {
    public boolean resetToDefault = false;
    public String botName = "S.A.I.L";
    public String creatorID = "153159020528533505";
    public String defaultPrefixCommand = "$";
    public String defaultPrefixCC = "$$";
    public String defaultAvatarFile = "Avatar.png";
    public boolean doDailyAvatars = false;
    public String dailyAvatarName = "Avatar_For_#day#.png";
    public String playing = "Starbound";
    public long queueChannelID = -1;
    public boolean doRandomGames = false;
    public boolean showSaveWarning = false;
    public int argsMax = 500;
    public int maxWarnings = 3;
    public int baseXpModifier = 20;
    public int xpForLevelOne = 100;
    public int avgMessagesPerDay = 20;
    public ArrayList<DailyMessageObject> dailyMessages = new ArrayList<DailyMessageObject>() {{
        add(new DailyMessageObject(DayOfWeek.MONDAY, Constants.DAILY_MESSAGE_1, dailyAvatarName));
        add(new DailyMessageObject(DayOfWeek.TUESDAY, Constants.DAILY_MESSAGE_2, dailyAvatarName));
        add(new DailyMessageObject(DayOfWeek.WEDNESDAY, Constants.DAILY_MESSAGE_3, dailyAvatarName));
        add(new DailyMessageObject(DayOfWeek.THURSDAY, Constants.DAILY_MESSAGE_4, dailyAvatarName));
        add(new DailyMessageObject(DayOfWeek.FRIDAY, Constants.DAILY_MESSAGE_5, dailyAvatarName));
        add(new DailyMessageObject(DayOfWeek.SATURDAY, Constants.DAILY_MESSAGE_6, dailyAvatarName));
        add(new DailyMessageObject(DayOfWeek.SUNDAY, Constants.DAILY_MESSAGE_7, dailyAvatarName));
    }};
    public ArrayList<RandomStatusObject> randomStatuses = new ArrayList<RandomStatusObject>() {{
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


    public boolean initObject(Config config) {
        if (resetToDefault) {
            config = new Config();
        }
        for (DailyMessageObject d : dailyMessages) {
            d.updateFilePath(dailyAvatarName);
        }
        return false;
    }

    public void setDailyMessages(ArrayList<DailyMessageObject> dailyMessages) {
        this.dailyMessages = dailyMessages;
    }
}


