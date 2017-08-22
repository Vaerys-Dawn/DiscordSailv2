package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleRoles implements GuildToggle {

    @Override
    public String name() {
        return "Roles";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleRoles = !config.moduleRoles;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleRoles;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleRoles;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeCommandsByType(Command.TYPE_ROLE_SELECT);
    }

    @Override
    public boolean isModule() {
        return true;
    }
}