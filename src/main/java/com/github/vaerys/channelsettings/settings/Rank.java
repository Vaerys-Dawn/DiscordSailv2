package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.interfaces.ChannelSetting;
import com.github.vaerys.interfaces.Command;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class Rank implements ChannelSetting{
    @Override
    public String type() {
        return Command.CHANNEL_PIXELS;
    }

    @Override
    public boolean isSetting() {
        return true;
    }
}
