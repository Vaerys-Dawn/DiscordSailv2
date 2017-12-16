package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class StopSpamWalls extends GuildSetting {

    @Override
    public String name() {
        return "StopSpamWalls";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.stopSpamWalls = !config.stopSpamWalls;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.stopSpamWalls;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().stopSpamWalls;
    }

    @Override
    public String desc(CommandObject command) {
        return "Removes walls of spam.";
    }

    @Override
    public void setup() {

    }
}