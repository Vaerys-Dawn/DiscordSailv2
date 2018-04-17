package com.github.vaerys.objects;

import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.pogos.GuildConfig;
import sx.blah.discord.api.IDiscordClient;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.github.vaerys.enums.UserSetting.DONT_DECAY;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class ProfileObject {
    private static final String defaultQuote = "This person doesn't seem to have much to say for themselves.";
    private static final String defaultGender = "Unknown";
    public long lastTalked = -1;
    long userID;
    long xp = 0;
    long currentLevel = 0;
    String gender = defaultGender;
    String quote = defaultQuote;
    ArrayList<UserSetting> settings = new ArrayList<>();
    ArrayList<UserLinkObject> links = new ArrayList<>();
    public List<ModNoteObject> modNotes;

    public ProfileObject(long userID) {
        this.userID = userID;
        if (links == null) links = new ArrayList<>();
        if (settings == null) settings = new ArrayList<>();
    }

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

    public long getUserID() {
        return userID;
    }

    public void addXP(GuildConfig config) {
        xp += config.xpRate * config.xpModifier;
    }

    public void addXP(long xp, GuildConfig config) {
        this.xp += config.xpModifier * xp;
        if (xp > Constants.PIXELS_CAP) {
            this.xp = Constants.PIXELS_CAP;
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

    public ArrayList<UserSetting> getSettings() {
        if (settings == null) {
            return new ArrayList<>();
        }
        return settings;
    }

    public void setSettings(ArrayList<UserSetting> settings) {
        this.settings = settings;
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
        ListIterator iterator = settings.listIterator();
        while (iterator.hasNext()) {
            UserSetting setting = (UserSetting) iterator.next();
            if (setting == UserSetting.HIT_LEVEL_FLOOR) {
                iterator.remove();
            }
        }
    }

    public UserObject getUser(GuildObject content) {
        return new UserObject(userID, content);
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

    /**
     * @param contents the contents of the new mod note.
     * @param command  Used to parse in the variables needed to access the guild, channel, message,
     *                 and user objects. these objects allows access to the api.
     * @param isStrike weather the note is a strike or not.
     */
    public void addSailModNote(String contents, CommandObject command, boolean isStrike) {
        if (modNotes == null) modNotes = new LinkedList<>();
        modNotes.add(new ModNoteObject(contents, command.client.bot.longID, command.message.getTimestamp().toEpochSecond(), isStrike));
    }

    public void addSailModNote(String contents, long timeStamp, boolean isStrike) {
        if (modNotes == null) modNotes = new LinkedList<>();
        IDiscordClient client = Client.getClient();
        modNotes.add(new ModNoteObject(contents, client.getOurUser().getLongID(), timeStamp, isStrike));
    }

    public void addSailModNote(String contents, CommandObject command) {
        addSailModNote(contents, command, false);
    }

    public String getDefaultAvatarURL() {
        return String.format("https://cdn.discordapp.com/embed/avatars/%d.png", new Random(userID).nextInt(5));
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
}
