package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class ChannelLogging extends GuildSetting {

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
    public String desc(CommandObject command) {
        return "Enables the logging of channel updates. Requires the " + Command.CHANNEL_SERVER_LOG + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
