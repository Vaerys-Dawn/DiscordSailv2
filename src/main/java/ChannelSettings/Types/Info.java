package ChannelSettings.Types;

import Interfaces.ChannelSetting;
import Interfaces.Command;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class Info implements ChannelSetting{
    @Override
    public String type() {
        return Command.CHANNEL_INFO;
    }

    @Override
    public boolean isSetting() {
        return false;
    }
}
