package ChannelSettings.Settings;

import Interfaces.ChannelSetting;
import Interfaces.Command;

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
