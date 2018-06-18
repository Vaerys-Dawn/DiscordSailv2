package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.pixels.SetLevelUpReaction;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildSetting;

public class ReactToLevelUp extends GuildSetting {
    @Override
    public SAILType name() {
        return SAILType.REACT_TO_LEVEL_UP;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.reactToLevelUp = !config.reactToLevelUp;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.reactToLevelUp;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().reactToLevelUp;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables level up reactions on messages";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the ability to have a reaction automatically added to the messages that caused a user to level up.";
    }

    @Override
    public void setup() {
        commands.add(SetLevelUpReaction.class);
    }
}
