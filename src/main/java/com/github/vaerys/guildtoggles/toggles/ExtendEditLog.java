package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class ExtendEditLog implements GuildSetting {

    @Override
    public String name() {
        return "ExtendEditLog";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.extendEditLog = !config.extendEditLog;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.extendEditLog;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().extendEditLog;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
