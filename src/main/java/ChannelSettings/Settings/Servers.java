package ChannelSettings.Settings;

import Interfaces.ChannelSetting;
import Interfaces.Command;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class Servers implements ChannelSetting {
    @Override
    public String type() {
        return Command.CHANNEL_SERVERS;
    }

    @Override
    public boolean isSetting() {
        return true;
    }
}
