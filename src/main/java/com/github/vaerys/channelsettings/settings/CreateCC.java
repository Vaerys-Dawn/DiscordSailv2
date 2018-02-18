package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

public class CreateCC extends ChannelSetting{
    @Override
    public String name() {
        return Command.CHANNEL_EDIT_CC;
    }

    @Override
    public boolean isSetting() {
        return true;
    }

    @Override
    public String desc(CommandObject command) {
        return "Channel for creating, editing and deleting custom commands.";
    }
}
