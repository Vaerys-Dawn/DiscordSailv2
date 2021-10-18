package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class XpGain extends GuildSetting {
    @Override
    public SAILType name() {
        return SAILType.XP_GAIN;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.xpGain = !config.xpGain;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.xpGain;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().xpGain;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the ability to gain pixels.";
    }

    @Override
    public void setup() {

    }
}
