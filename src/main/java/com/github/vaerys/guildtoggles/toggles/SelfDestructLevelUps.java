package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

public class SelfDestructLevelUps implements GuildToggle {
    @Override
    public String name() {
        return "SelfDestructLevelUps";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.selfDestructLevelUps = !config.selfDestructLevelUps;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.selfDestructLevelUps;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().selfDestructLevelUps;
    }

    @Override
    public void execute(GuildObject guild) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
