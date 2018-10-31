package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.commands.roleSelect.CosmeticRoles;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 18/03/2017.
 */
public class RoleIsToggle extends GuildSetting {
    @Override
    public SAILType name() {
        return SAILType.ROLE_IS_TOGGLE;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.roleIsToggle = !config.roleIsToggle;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.roleIsToggle;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().roleIsToggle;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }

    @Override
    public String desc(CommandObject command) {
        return "Changes the " + new CosmeticRoles().getCommand(command) + " command to make it toggle roles.";
    }

    @Override
    public void setup() {

    }
}
