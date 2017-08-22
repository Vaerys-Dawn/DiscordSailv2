package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.interfaces.ChannelSetting;
import com.github.vaerys.interfaces.Command;

/**
 * Created by Vaerys on 17/06/2017.
 */
public class XpDenied implements ChannelSetting {
    @Override
    public String type() {
        return Command.CHANNEL_XP_DENIED;
    }

    @Override
    public boolean isSetting() {
        return true;
    }
}
