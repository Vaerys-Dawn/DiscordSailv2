package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.interfaces.ChannelSetting;
import com.github.vaerys.interfaces.Command;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class Groups implements ChannelSetting{
    @Override
    public String type() {
        return Command.CHANNEL_GROUPS;
    }

    @Override
    public boolean isSetting() {
        return true;
    }
}
