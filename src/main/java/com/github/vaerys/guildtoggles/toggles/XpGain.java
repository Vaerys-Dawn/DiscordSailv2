package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class XpGain implements GuildSetting {
    @Override
    public String name() {
        return "XpGain";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.xpGain = !config.xpGain;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.xpGain;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().xpGain;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
