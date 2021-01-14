package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class ChannelLogging extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.CHANNEL_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.channelLogging = !config.channelLogging;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.channelLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().channelLogging;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables logging of messageChannel setting changes.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of messageChannel updates. Requires the " + ChannelSetting.SERVER_LOG.toString() + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
