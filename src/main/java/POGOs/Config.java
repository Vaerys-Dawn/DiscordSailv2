package POGOs;

import Main.Constants;
import Objects.DailyMessageObject;

import java.time.DayOfWeek;
import java.util.ArrayList;

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
    public int argsMax = 500;
    public int maxWarnings = 3;
    public ArrayList<DailyMessageObject> dailyMessages;
    public int baseXpModifier = 5;
    public int xpForLevelOne = 100;

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
            dailyMessages = new ArrayList<DailyMessageObject>() {{
                add(new DailyMessageObject(DayOfWeek.MONDAY, Constants.DAILY_MESSAGE_1, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.TUESDAY, Constants.DAILY_MESSAGE_2, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.WEDNESDAY, Constants.DAILY_MESSAGE_3, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.THURSDAY, Constants.DAILY_MESSAGE_4, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.FRIDAY, Constants.DAILY_MESSAGE_5, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.SATURDAY, Constants.DAILY_MESSAGE_6, dailyAvatarName));
                add(new DailyMessageObject(DayOfWeek.SUNDAY, Constants.DAILY_MESSAGE_7, dailyAvatarName));
            }};
            return true;
        }
        for (DailyMessageObject d: dailyMessages){
            d.updateFilePath(dailyAvatarName);
        }
        return false;
    }

}
