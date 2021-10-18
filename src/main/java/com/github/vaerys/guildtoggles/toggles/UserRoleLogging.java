package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class UserRoleLogging extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.USER_ROLE_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.userRoleLogging = !config.userRoleLogging;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.userRoleLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().userRoleLogging;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables logging of globalUser role changes.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of globalUser role updates. Requires the " + ChannelSetting.SERVER_LOG.toString() + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
