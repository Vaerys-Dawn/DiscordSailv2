package com.github.vaerys.guildtoggles;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.modules.*;
import com.github.vaerys.guildtoggles.toggles.*;
import com.github.vaerys.main.Globals;
import com.github.vaerys.templates.GuildModule;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.templates.GuildToggle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ToggleList {

    final static Logger logger = LoggerFactory.getLogger(ToggleList.class);

    private static final List<GuildSetting> guildSettings = new ArrayList<GuildSetting>() {{
        add(new AdminLogging());
        add(new AutoArtPinning());
        add(new ChannelLogging());
        add(new CheckNewUsers());
        add(new CompEntries());
        add(new DailyMessage());
        add(new DeleteLogging());
        add(new DenyInvites());
        add(new DontLogBot());
        add(new EditLogging());
        add(new ExtendEditLog());
        add(new GeneralLogging());
        add(new JoinLeaveLogging());
        add(new MentionSpam());
        add(new MuteRepeatOffender());
        add(new RateLimiting());
        add(new RoleIsToggle());
        add(new SendJoinMessages());
        add(new ShitpostFiltering());
        add(new UserRoleLogging());
        add(new UseTimeStamps());
        add(new Voting());
        add(new XpDecay());
        add(new XpGain());
        add(new WelcomeMessages());
        add(new UserInfoShowsDate());
        add(new SelfDestructLevelUps());
        add(new ReactToLevelUp());
        add(new LikeArt());
        add(new DebugMode());
        add(new StopSpamWalls());
        add(new KickLogging());
        add(new BanLogging());
        add(new MuteRemovesRoles());
//        add(new DungeonChars());
    }};

    private static final List<GuildModule> guildModules = new ArrayList<GuildModule>() {{
        //modules
        add(new ModuleSlash());
        add(new ModuleRuleRewards());
        add(new ModuleGroups());
        add(new ModuleChars());
        add(new ModuleComp());
        add(new ModuleServers());
        add(new ModuleRoles());
        add(new ModuleCC());
        add(new ModuleMe());
        add(new ModuleModMuting());
        add(new ModuleArtPinning());
        add(new ModulePixels());
        add(new ModuleLogging());
        add(new ModuleJoinMessages());
        add(new ModuleAdminCC());
    }};

    public static List<GuildToggle> getAllToggles(boolean validate) {
        List<GuildToggle> toggles = new ArrayList<>(guildSettings);
        toggles.addAll(guildModules);
        //toggles
        if (validate) validate(toggles);
        return toggles;
    }

    public static void validate(List<GuildToggle> settings) {
        for (GuildToggle t : settings) {
            logger.trace("Validating Tag: " + t.getClass().getName());
            String errorReport = t.validate();
            Globals.addToErrorStack(errorReport);
            t.setup();
        }
    }

    public static List<GuildToggle> getAllToggles() {
        return getAllToggles(false);
    }

    public static List<GuildToggle> getToggles(boolean isModule) {
        if (isModule) return new ArrayList<>(guildModules);
        return new ArrayList<>(guildSettings);
    }

    public static GuildToggle getGuildToggle(String args, boolean isModule) {
        for (GuildToggle t : getToggles(isModule)) {
            if (SAILType.get(args) == t.name()) {
                return t;
            }
        }
        return null;
    }

    public static GuildToggle getToggle(Class obj) {
        if (!GuildToggle.class.isAssignableFrom(obj)) {
            throw new IllegalArgumentException("Cannot Get Toggle from Class (" + obj.getName() + ")");
        }
        for (GuildToggle c : getAllToggles()) {
            if (c.getClass().getName().equals(obj.getName())) {
                return c;
            }
        }
        throw new IllegalArgumentException("Could not find Toggle (" + obj.getName() + ")");
    }

    public static GuildSetting getSetting(SAILType obj) {
        for (GuildSetting c : guildSettings) {
            if (c.name() == obj) {
                return c;
            }
        }
        throw new IllegalArgumentException("Could not find Setting of SAILType: " + obj);
    }

    public static GuildModule getModule(SAILType obj) {
        for (GuildModule c : guildModules) {
            if (c.name() == obj) {
                return c;
            }
        }
        throw new IllegalArgumentException("Could not find Module of SAILType: " + obj);
    }
//
//    public static GuildSetting getSetting(Class obj) {
//        if (!GuildSetting.class.isAssignableFrom(obj)) {
//            throw new IllegalArgumentException("Cannot Get Setting from Class (" + obj.getName() + ")");
//        }
//        for (GuildSetting c : guildSettings) {
//            if (c.getClass().getName().equals(obj.getName())) {
//                return c;
//            }
//        }
//        throw new IllegalArgumentException("Could not find Setting (" + obj.getName() + ")");
//    }
//
//    public static GuildModule getModule(Class obj) {
//        if (!GuildModule.class.isAssignableFrom(obj)) {
//            throw new IllegalArgumentException("Cannot Get Module from Class (" + obj.getName() + ")");
//        }
//        for (GuildModule c : guildModules) {
//            if (c.getClass().getName().equals(obj.getName())) {
//                return c;
//            }
//        }
//        throw new IllegalArgumentException("Could not find Module (" + obj.getName() + ")");
//    }

    public static List<GuildSetting> getSettings(boolean validate) {
        if (validate) validate(new ArrayList<>(guildSettings));
        return guildSettings;
    }

    public static List<GuildSetting> getSettings() {
        return getSettings(false);
    }

    public static List<GuildModule> getModules(boolean validate) {
        if (validate) validate(new ArrayList<>(guildModules));
        return guildModules;
    }

    public static List<GuildModule> getModules() {
        return getModules(false);
    }

    public static GuildToggle getModule(String args) {
        return getModule(SAILType.get(args));
    }

    public static GuildToggle getModuleFromSetting(SAILType setting) {
        for (GuildToggle m : guildModules) {
            if (m.settings.contains(setting)) return m;
        }
        return null;
    }
}

