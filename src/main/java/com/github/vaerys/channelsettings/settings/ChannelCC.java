package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

public class ChannelCC extends ChannelSetting {
    @Override
    public String name() {
        return Command.CHANNEL_CC_INFO;
    }

    @Override
    public boolean isSetting() {
        return true;
    }

    @Override
    public String desc(CommandObject command) {
        return "Channel for general use of the base CC commands.";
    }
}
