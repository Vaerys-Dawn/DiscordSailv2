package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class XpDecay extends GuildSetting {
    @Override
    public SAILType name() {
        return SAILType.XP_DECAY;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.xpDecay = !config.xpDecay;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.xpDecay;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().xpDecay;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the pixel decay feature.";
    }

    @Override
    public void setup() {

    }
}
