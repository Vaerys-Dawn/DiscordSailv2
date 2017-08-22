package com.github.vaerys.channelsettings.types;

import com.github.vaerys.interfaces.ChannelSetting;
import com.github.vaerys.interfaces.Command;

/**
 * Created by Vaerys on 12/05/2017.
 */
public class Art implements ChannelSetting {
    @Override
    public String type() {
        return Command.CHANNEL_ART;
    }

    @Override
    public boolean isSetting() {
        return false;
    }
}
