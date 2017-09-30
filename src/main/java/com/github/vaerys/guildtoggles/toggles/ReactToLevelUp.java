package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

public class ReactToLevelUp implements GuildSetting {
    @Override
    public String name() {
        return "ReactToLevelUp";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.reactToLevelUp = !config.reactToLevelUp;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.reactToLevelUp;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().reactToLevelUp;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
