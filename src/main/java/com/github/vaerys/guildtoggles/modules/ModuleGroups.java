package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class ModuleGroups implements GuildToggle{
    @Override
    public String name() {
        return "Groups";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleGroups = !config.moduleGroups;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleGroups;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleGroups;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeCommandsByType(Command.TYPE_GROUPS);
        guild.removeChannel(Command.CHANNEL_GROUPS);
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
