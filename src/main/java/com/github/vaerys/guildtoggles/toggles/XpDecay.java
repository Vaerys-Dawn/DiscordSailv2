package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class XpDecay extends GuildSetting {
    @Override
    public String name() {
        return "XpDecay";
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
