package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class LevelUpDenied extends ChannelSetting {
    @Override
    public String name() {
        return Command.CHANNEL_LEVEL_UP_DENIED;
    }

    @Override
    public boolean isSetting() {
        return true;
    }

    @Override
    public String desc(CommandObject command) {
        return "When this setting is on a channel no level up messages will be sent to the channel.";
    }
}
