package ChannelSettings.Settings;

import Interfaces.ChannelSetting;
import Interfaces.Command;

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
