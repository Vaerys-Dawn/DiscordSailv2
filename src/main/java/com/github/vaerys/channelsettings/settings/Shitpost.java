package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.interfaces.ChannelSetting;
import com.github.vaerys.interfaces.Command;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class Shitpost implements ChannelSetting{
    @Override
    public String type() {
        return Command.CHANNEL_SHITPOST;
    }

    @Override
    public boolean isSetting() {
        return true;
    }
}
