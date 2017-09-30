package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 13/05/2017.
 */
public class AutoArtPinning implements GuildSetting {
    @Override
    public String name() {
        return "AutoArtPinning";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.autoArtPinning = !config.autoArtPinning;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.autoArtPinning;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().autoArtPinning;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
