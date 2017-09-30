package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class UseTimeStamps implements GuildSetting {

    @Override
    public String name() {
        return "UseTimeStamps";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.useTimeStamps = !config.useTimeStamps;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.useTimeStamps;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().useTimeStamps;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
