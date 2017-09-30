package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class UserRoleLogging implements GuildSetting {

    @Override
    public String name() {
        return "UserRoleLogging";
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
    public void execute(GuildObject guild) {

    }
}
