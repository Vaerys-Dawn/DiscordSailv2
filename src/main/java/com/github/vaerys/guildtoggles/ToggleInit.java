package com.github.vaerys.guildtoggles;

import com.github.vaerys.guildtoggles.modules.*;
import com.github.vaerys.guildtoggles.toggles.*;
import com.github.vaerys.main.Constants;
import com.github.vaerys.templates.GuildToggle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ToggleInit {

    final static Logger logger = LoggerFactory.getLogger(ToggleInit.class);

    public static ArrayList<GuildToggle> get() {
        ArrayList<GuildToggle> guildToggles = new ArrayList<>();

        //toggles
        guildToggles.add(new AdminLogging());
        guildToggles.add(new AutoArtPinning());
        guildToggles.add(new ChannelLogging());
        guildToggles.add(new CompEntries());
        guildToggles.add(new DailyMessage());
        guildToggles.add(new DeleteLogging());
        guildToggles.add(new DenyInvites());
        guildToggles.add(new DontLogBot());
        guildToggles.add(new EditLogging());
        guildToggles.add(new ExtendEditLog());
        guildToggles.add(new GeneralLogging());
        guildToggles.add(new JoinLeaveLogging());
        guildToggles.add(new MentionSpam());
        guildToggles.add(new MuteRepeatOffender());
        guildToggles.add(new RateLimiting());
        guildToggles.add(new RoleIsToggle());
        guildToggles.add(new ShitpostFiltering());
        guildToggles.add(new ModuleSlash());
        guildToggles.add(new UserRoleLogging());
        guildToggles.add(new UseTimeStamps());
        guildToggles.add(new Voting());
        guildToggles.add(new XpDecay());
        guildToggles.add(new XpGain());
        guildToggles.add(new JoinServerMessages());
        guildToggles.add(new UserInfoShowsDate());
        guildToggles.add(new SelfDestructLevelUps());
        guildToggles.add(new ReactToLevelUp());
        guildToggles.add(new LikeArt());
        guildToggles.add(new DebugMode());
        guildToggles.add(new StopSpamWalls());

        //modules
        guildToggles.add(new ModuleGroups());
        guildToggles.add(new ModuleChars());
        guildToggles.add(new ModuleComp());
        guildToggles.add(new ModuleServers());
        guildToggles.add(new ModuleRoles());
        guildToggles.add(new ModuleCC());
        guildToggles.add(new ModuleMe());
        guildToggles.add(new ModuleModMuting());
        guildToggles.add(new ModuleArtPinning());
        guildToggles.add(new ModulePixels());

        validate(guildToggles);

        return guildToggles;
    }

    private static void validate(List<GuildToggle> settings) {
        for (GuildToggle t : settings) {
            logger.trace("Validating Tag: " + t.getClass().getName());
            String errorReport = t.validate();
            if (errorReport != null) {
                logger.error(errorReport);
                System.exit(Constants.EXITCODE_CONF_ERROR);
            }
        }
    }
}
