package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class AdminLogging extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.ADMIN_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.adminLogging = !config.adminLogging;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.adminLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().adminLogging;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables Admin command logging.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of admin commands. Requires the " + ChannelSetting.ADMIN_LOG.toString() + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
