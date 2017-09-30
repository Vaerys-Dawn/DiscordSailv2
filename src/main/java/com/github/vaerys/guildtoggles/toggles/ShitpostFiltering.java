package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ShitpostFiltering implements GuildSetting {

    @Override
    public String name() {
        return "ShitpostFiltering";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.shitPostFiltering = !config.shitPostFiltering;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.shitPostFiltering;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().shitPostFiltering;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
