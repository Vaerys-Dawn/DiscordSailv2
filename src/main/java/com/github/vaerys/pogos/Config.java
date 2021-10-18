package com.github.vaerys.pogos;

import com.github.vaerys.main.Constants;
import com.github.vaerys.objects.botlevel.RandomStatusObject;
import com.github.vaerys.objects.userlevel.DailyMessage;
import com.github.vaerys.templates.GlobalFile;

import java.util.ArrayList;

/**
 * Created by Vaerys on 14/01/2017.
 */
public class Config extends GlobalFile {
    public static final String FILE_PATH = "Config.json";
    private double fileVersion = 1.1;
    public boolean resetToDefault = false;
    public String botName = "S.A.I.L";
    public long creatorID = 0;
    public String defaultPrefixCommand = "$";
    public String defaultPrefixCC = "$$";
    public String defaultPrefixAdminCC = "$!";
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
    public ArrayList<DailyMessage> dailyMessages = new ArrayList<>();
    public ArrayList<RandomStatusObject> randomStatuses = new ArrayList<RandomStatusObject>() {{
        add(new RandomStatusObject("Starbound", 10));
        add(new RandomStatusObject("Wargroove", 5));
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

    public static Config check(Config config) {
        if (config.resetToDefault) {
            config = new Config();
            config.setPath(FILE_PATH);
        }
        if (config.dailyMessages.size() != 7) {
            config.dailyMessages = Constants.defaultDailyMessages(config.creatorID);
        }
        config.flushFile();
        return config;
    }

    public void setDailyMessages(ArrayList<DailyMessage> dailyMessages) {
        this.dailyMessages = dailyMessages;
    }
}


