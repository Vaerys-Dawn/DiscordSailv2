package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class ChannelLogging implements GuildSetting {

    @Override
    public String name() {
        return "ChannelLogging";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.channelLogging = !config.channelLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.channelLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().channelLogging;
    }

    @Override
    public void execute(GuildObject guild) {

    }
}
