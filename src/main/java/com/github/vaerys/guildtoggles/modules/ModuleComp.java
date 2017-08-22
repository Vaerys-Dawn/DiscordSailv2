package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.guildtoggles.toggles.CompEntries;
import com.github.vaerys.guildtoggles.toggles.Voting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleComp implements GuildToggle {

    boolean state = false;

    @Override
    public String name() {
        return "Comp";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleComp = !config.moduleComp;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleComp;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleComp;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeCommandsByType(Command.TYPE_COMPETITION);
        guild.removeToggle(new Voting().name());
        guild.removeToggle(new CompEntries().name());
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
