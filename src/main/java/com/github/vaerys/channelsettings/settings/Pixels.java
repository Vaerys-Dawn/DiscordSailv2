package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class Pixels extends ChannelSetting{
    @Override
    public String name() {
        return Command.CHANNEL_PIXELS;
    }

    @Override
    public boolean isSetting() {
        return true;
    }

    @Override
    public String desc(CommandObject command) {
        return "Channel for pixel commands.";
    }
}
