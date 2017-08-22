package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.guildtoggles.toggles.ShitpostFiltering;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleCC implements GuildToggle {

    @Override
    public String name() {
        return "CC";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleCC = !config.moduleCC;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleCC;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleCC;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeCommandsByType(Command.TYPE_CC);
        guild.removeChannel(Command.CHANNEL_SHITPOST);
        guild.removeToggle(new ShitpostFiltering().name());
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
