package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class MuteRepeatOffender implements GuildSetting {

    @Override
    public String name() {
        return "MuteRepeatOffender";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.muteRepeatOffenders = !config.muteRepeatOffenders;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.muteRepeatOffenders;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().muteRepeatOffenders;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
