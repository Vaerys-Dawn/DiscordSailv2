package com.github.vaerys.guildtoggles;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.modules.*;
import com.github.vaerys.guildtoggles.toggles.*;
import com.github.vaerys.main.Globals;
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

    private static final List<GuildToggle> guildSettings = new ArrayList<GuildToggle>() {{
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
        add(new ModuleSlash());
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
        add(new ModuleRuleRewards());
    }};

    private static final List<GuildToggle> guildModules = new ArrayList<GuildToggle>() {{
        //modules
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
        }
    }

    public static List<GuildToggle> getAllToggles() {
        return getAllToggles(false);
    }

    public static List<GuildToggle> getToggles(boolean isModule) {
        if (isModule) return guildModules;
        return guildSettings;
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
}

