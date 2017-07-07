package ChannelSettings.Types;

import Interfaces.ChannelSetting;
import Interfaces.Command;

/**
 * Created by Vaerys on 02/07/2017.
 */
public class LevelUp implements ChannelSetting {
    @Override
    public String type() {
        return Command.CHANNEL_LEVEL_UP;
    }

    @Override
    public boolean isSetting() {
        return false;
    }
}
