package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.roleSelect.CosmeticRoles;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 18/03/2017.
 */
public class RoleIsToggle extends GuildSetting {
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
    public String desc(CommandObject command) {
        return "Changes the " + new CosmeticRoles().getCommand(command) + " command to make it toggle roles.";
    }

    @Override
    public void setup() {

    }
}
