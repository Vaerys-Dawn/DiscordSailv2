package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 18/03/2017.
 */
public class RoleIsToggle implements GuildSetting {
    @Override
    public String name() {
        return "RoleIsToggle";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.roleIsToggle = !config.roleIsToggle;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.roleIsToggle;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().roleIsToggle;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
