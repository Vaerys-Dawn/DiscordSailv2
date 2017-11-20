package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.pixels.SetLevelUpReaction;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class ReactToLevelUp extends GuildSetting {
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
    public String desc(CommandObject command) {
        return "Enables the ability to have a reaction automatically added to the messages that caused a user to level up.";
    }

    @Override
    public void setup() {
        commands.add(new SetLevelUpReaction());
    }
}
