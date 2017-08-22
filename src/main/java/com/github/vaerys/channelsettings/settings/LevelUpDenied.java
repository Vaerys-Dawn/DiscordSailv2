package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.interfaces.ChannelSetting;
import com.github.vaerys.interfaces.Command;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class LevelUpDenied implements ChannelSetting {
    @Override
    public String type() {
        return Command.CHANNEL_LEVEL_UP_DENIED;
    }

    @Override
    public boolean isSetting() {
        return true;
    }
}
