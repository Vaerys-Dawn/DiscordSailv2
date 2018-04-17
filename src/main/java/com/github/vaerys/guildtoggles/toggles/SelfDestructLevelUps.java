package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class SelfDestructLevelUps extends GuildSetting {
    @Override
    public SAILType name() {
        return SAILType.SELF_DESTRUCT_LEVEL_UPS;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.selfDestructLevelUps = !config.selfDestructLevelUps;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.selfDestructLevelUps;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().selfDestructLevelUps;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enable automatic deletion of \"Ding!\" messages.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the automatic deletion of level up messages (in every channel except for the " + ChannelSetting.LEVEL_UP.toString() + " channel and Direct messages) after 1 minute.";
    }

    @Override
    public void setup() {

    }
}
