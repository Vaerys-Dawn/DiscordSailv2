package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildSetting;

public class SelfDestructLevelUps extends GuildSetting {
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
    public String desc(CommandObject command) {
        return "Enables the automatic deletion of level up messages (in every channel except for the " + Command.CHANNEL_LEVEL_UP + " channel and Direct messages) after 1 minute.";
    }

    @Override
    public void setup() {

    }
}
