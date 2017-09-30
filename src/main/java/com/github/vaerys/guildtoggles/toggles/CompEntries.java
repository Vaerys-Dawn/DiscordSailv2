package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class CompEntries implements GuildSetting {

    @Override
    public String name() {
        return "CompEntries";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.compEntries = !config.compEntries;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.compEntries;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().compEntries;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
