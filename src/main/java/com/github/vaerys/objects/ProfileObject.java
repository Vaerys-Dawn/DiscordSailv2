package com.github.vaerys.objects;

import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.pogos.GuildConfig;
import com.sun.corba.se.impl.orbutil.closure.Constant;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import static com.github.vaerys.main.UserSetting.DONT_DECAY;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class ProfileObject {
    private static final String defaultQuote = "This person doesn't seem to have much to say for themselves.";
    private static final String defaultGender = "Unknown";

    long userID;
    long xp = 0;
    long currentLevel = -1;
    String gender = "Unknown";
    String quote = defaultQuote;
    ArrayList<UserSetting> settings = new ArrayList<>();
    ArrayList<UserLinkObject> links = new ArrayList<>();
    public List<ModNoteObject> modNotes;
    public long lastTalked = -1;

    public String getQuote() {
        if (quote == null) {
            quote = "This person doesn't seem to have much to say for themselves.";
        }
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getGender() {
        if (gender == null) {
            gender = "Unknown";
        }
        return Utility.removeFun(gender);
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ProfileObject(long userID) {
        this.userID = userID;
        if (links == null) links = new ArrayList<>();
        if (settings == null) settings = new ArrayList<>();
    }

    public long getUserID() {
        return userID;
    }

    public void addXP(GuildConfig config) {
        xp += config.xpRate * config.xpModifier;
    }

    public void addXP(long xp, GuildConfig config) {
        this.xp += config.xpModifier * xp;
    }

    public void setXp(long xp, boolean levelUp) {
        if (xp > Constants.PIXELS_CAP) throw new IllegalArgumentException("argument out of valid range");
        this.xp = xp;

        if (levelUp) {
            this.currentLevel = XpHandler.xpToLevel(xp);
        }
    }

    public void setXp(long xp) {
        setXp(xp, true);
    }

    public long getXP() {
        return xp;
    }

    public ArrayList<UserSetting> getSettings() {
        if (settings == null) {
            return new ArrayList<>();
        }
        return settings;
    }

    public ArrayList<UserLinkObject> getLinks() {
        return links;
    }

    public void setLinks() {
        if (links == null) {
            links = new ArrayList<>();
        }
    }

    public long getLastTalked() {
        return lastTalked;
    }

    public long getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(long currentLevel) {
        this.currentLevel = currentLevel;
    }

    public UserSetting getLevelState() {
        for (UserSetting s : settings) {
            if (Constants.levelUpStates.contains(s)) {
                return s;
            }
        }
        return null;
    }

    public void setLastTalked(long lastTalked) {
        this.lastTalked = lastTalked;
    }

    public void removeLevelFloor() {
        ListIterator iterator = settings.listIterator();
        while (iterator.hasNext()) {
            UserSetting setting = (UserSetting) iterator.next();
            if (setting == UserSetting.HIT_LEVEL_FLOOR) {
                iterator.remove();
            }
        }
    }

    public UserObject getUser(GuildObject content) {
        return new UserObject(content.get().getUserByID(userID), content);
    }

    public boolean isEmpty() {
        return xp == 0 &&
                quote.equals(defaultQuote) &&
                gender.equals(defaultGender) &&
                links.size() == 0 &&
                settings.size() == 0;
    }

    public long daysDecayed(GuildObject guild) {
        if (!guild.config.modulePixels || !guild.config.xpDecay) return -1;
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        if (lastTalked != -1 && !settings.contains(DONT_DECAY)) {
            long diff = nowUTC.toEpochSecond() - lastTalked;
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
            if (days > 7) {
                return days;
            }
        }
        return -1;
    }
}
