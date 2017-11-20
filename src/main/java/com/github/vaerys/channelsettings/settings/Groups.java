package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class Groups implements ChannelSetting{
    @Override
    public String name() {
        return Command.CHANNEL_GROUPS;
    }

    @Override
    public boolean isSetting() {
        return true;
    }

    @Override
    public String desc(CommandObject command) {
        return "Channel for the group commands.";
    }
}
