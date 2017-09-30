package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class DontLogBot implements GuildSetting{

    @Override
    public String name() {
        return "DontlogBot";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.dontLogBot = !config.dontLogBot;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.dontLogBot;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().dontLogBot;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
