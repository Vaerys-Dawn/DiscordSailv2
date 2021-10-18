package com.github.vaerys.objects.userlevel;

import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.ModNoteObject;
import com.github.vaerys.pogos.GuildConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.vaerys.enums.UserSetting.DONT_DECAY;
import static com.github.vaerys.enums.UserSetting.PRIVATE_PROFILE;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class ProfileObject {
    private static final String DEFAULT_QUOTE = "This person doesn't seem to have much to say for themselves.";
    private static final String DEFAULT_GENDER = "Unknown";
    public long lastTalked = -1;
    long userID;
    long xp = 0;
    long currentLevel = 0;
    String gender = DEFAULT_GENDER;
    String quote = DEFAULT_QUOTE;
    private List<UserSetting> settings = new ArrayList<>();
    private List<UserLinkObject> links = new ArrayList<>();
    public List<ModNoteObject> modNotes;

    public ProfileObject(long userID) {
        this.userID = userID;
    }

    public String getQuote() {
        if (quote == null) {
            quote = DEFAULT_QUOTE;
        }
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getGender() {
        if (gender == null) {
            gender = DEFAULT_GENDER;
        }
        return Utility.removeFun(gender);
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getUserID() {
        return userID;
    }

    public void addXP(GuildConfig config) {
        xp += config.xpRate * config.xpModifier;
    }

    public void addXP(long pixels, GuildConfig config, boolean doModifier) {
        this.xp += (doModifier ? config.xpModifier : 1) * pixels;
        if (xp > Constants.PIXELS_CAP) {
            this.xp = Constants.PIXELS_CAP;
        }
    }

    public void addXP(long pixels, GuildConfig config) {
        addXP(pixels, config, true);
    }

    public void removePixels(long pixels, GuildConfig config) {
        this.xp -= config.xpModifier * pixels;
        if (xp < 0) {
            this.xp = 0;
        }
    }

    public void setXp(long xp, boolean levelUp) {
        this.xp = xp;
        if (levelUp) {
            this.currentLevel = PixelHandler.xpToLevel(xp);
        }
    }

    public void setXp(long xp) {
        setXp(xp, true);
    }

    public long getXP() {
        return xp;
    }

    public List<UserSetting> getSettings() {
        if (settings == null) settings = new ArrayList<>();
        return settings;
    }

    public void setSettings(List<UserSetting> settings) {
        if (settings == null) {
            settings = new ArrayList<>();
        }
        this.settings = settings;
    }

    public List<UserLinkObject> getLinks() {
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

    public void setLastTalked(long lastTalked) {
        this.lastTalked = lastTalked;
    }

    public long getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(long currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void levelUp() {
        currentLevel = PixelHandler.xpToLevel(xp);
    }

    public UserSetting getLevelState() {
        for (UserSetting s : settings) {
            if (Constants.levelUpStates.contains(s)) {
                return s;
            }
        }
        return null;
    }

    public void removeLevelFloor() {
        settings.removeIf(setting -> setting == UserSetting.HIT_LEVEL_FLOOR);
    }

    public UserObject getUser(GuildObject content) {
        Member user = content.getUserByID(userID);
        if (user == null) return new UserObject(userID, content);
        return new UserObject(user, content);
    }

    public boolean isEmpty() {
        return xp == 0 &&
                quote.equals(DEFAULT_QUOTE) &&
                gender.equals(DEFAULT_GENDER) &&
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

    /**
     * @param contents the getContents of the new mod note.
     * @param command  Used to parse in the variables needed to access the guild, messageChannel, message,
     *                 and globalUser objects. these objects allows access to the api.
     * @param isStrike weather the note is a strike or not.
     */
    public void addSailModNote(String contents, CommandObject command, boolean isStrike) {
        if (modNotes == null) modNotes = new LinkedList<>();
        modNotes.add(new ModNoteObject(contents, command.client.bot.longID, command.message.getTimestampZone().toEpochSecond(), isStrike));
    }

    public void addSailModNote(String contents, long timeStamp, boolean isStrike) {
        if (modNotes == null) modNotes = new LinkedList<>();
        JDA client = Client.getClient();
        modNotes.add(new ModNoteObject(contents, client.getSelfUser().getIdLong(), timeStamp, isStrike));
    }

    public void addSailModNote(String contents, CommandObject command) {
        addSailModNote(contents, command, false);
    }

    public void toggleSetting(UserSetting setting) {
        if (!settings.remove(setting)) {
            settings.add(setting);
        }
    }

    public String toggleSetting(UserSetting setting, String remove, String add) {
        if (settings.remove(setting)) {
            return remove;
        } else {
            settings.add(setting);
            return add;
        }
    }

    public boolean showRank(GuildObject guild) {
        return !PixelHandler.isUnRanked(userID, guild.users, guild.get());
    }

    public String getDefaultAvatarURL() {
        return Utility.getDefaultAvatarURL(userID);
    }

    public boolean isPrivate() {
        return settings.contains(PRIVATE_PROFILE);
    }

    public boolean hasSetting(UserSetting setting) {
        return settings.contains(setting);
    }
}
