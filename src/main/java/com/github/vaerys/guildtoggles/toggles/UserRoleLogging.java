package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.templates.SAILType;

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
    public boolean get(GuildConfig config) {
        return config.userRoleLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().userRoleLogging;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of user role updates. Requires the " + ChannelSetting.SERVER_LOG.toString() + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
