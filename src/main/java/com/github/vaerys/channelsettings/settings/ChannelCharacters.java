package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

public class ChannelCharacters extends ChannelSetting {
    @Override
    public String name() {
        return Command.CHANNEL_CHAR;
    }

    @Override
    public boolean isSetting() {
        return true;
    }

    @Override
    public String desc(CommandObject command) {
        return "General Channel for Character related commands.";
    }
}
