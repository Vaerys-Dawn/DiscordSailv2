package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class StopSpamWalls extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.STOP_SPAM_WALLS;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.stopSpamWalls = !config.stopSpamWalls;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.stopSpamWalls;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().stopSpamWalls;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }

    @Override
    public String desc(CommandObject command) {
        return "Removes walls of spam.";
    }

    @Override
    public void setup() {

    }
}
