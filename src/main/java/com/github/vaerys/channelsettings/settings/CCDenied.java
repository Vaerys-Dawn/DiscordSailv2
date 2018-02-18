package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

public class CCDenied extends ChannelSetting {
    @Override
    public String name() {
        return Command.CHANNEL_CC_DENIED;
    }

    @Override
    public boolean isSetting() {
        return true;
    }

    @Override
    public String desc(CommandObject command) {
        return "When this setting is enabled on a channel it will deny the use of custom commands in it.";
    }
}
