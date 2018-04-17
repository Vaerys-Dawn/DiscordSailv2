package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class DebugMode extends GuildSetting {
    @Override
    public SAILType name() {
        return SAILType.DEBUG_MODE;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.debugMode = !config.debugMode;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.debugMode;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().debugMode;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the bypasses for the bot developer.";
    }

    @Override
    public void setup() {

    }
}
