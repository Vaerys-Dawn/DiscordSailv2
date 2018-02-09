package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class DontLog extends ChannelSetting {
    @Override
    public String name() {
        return Command.CHANNEL_DONT_LOG;
    }

    @Override
    public boolean isSetting() {
        return true;
    }

    @Override
    public String desc(CommandObject command) {
        return "When enabled on a channel, no message logs will occur for the channel.";
    }
}
