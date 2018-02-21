package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.enums.SAILType;

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
    public boolean get(GuildConfig config) {
        return config.xpDecay;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().xpDecay;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the pixel decay feature.";
    }

    @Override
    public void setup() {

    }
}
