package ChannelSettings.Types;

import Interfaces.ChannelSetting;
import Interfaces.Command;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class General implements ChannelSetting {
    @Override
    public String type() {
        return Command.CHANNEL_GENERAL;
    }

    @Override
    public boolean isSetting() {
        return false;
    }
}
