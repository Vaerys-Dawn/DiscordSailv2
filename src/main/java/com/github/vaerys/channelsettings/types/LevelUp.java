package com.github.vaerys.channelsettings.types;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

/**
 * Created by Vaerys on 02/07/2017.
 */
public class LevelUp extends ChannelSetting {
    @Override
    public String name() {
        return Command.CHANNEL_LEVEL_UP;
    }

    @Override
    public boolean isSetting() {
        return false;
    }

    @Override
    public String desc(CommandObject command) {
        return "Where the LevelChannel profile setting will send level up messages.";
    }
}
