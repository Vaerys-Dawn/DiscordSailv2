package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class KickLogging extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.KICK_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.kickLogging = !config.kickLogging;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.kickLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().kickLogging;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables globalUser kick logging.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of kicks by moderators.\n" +
                "Requires " + new JoinLeaveLogging().name() + " to be enabled and" +
                "requires the " + ChannelSetting.ADMIN_LOG.toString() + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
