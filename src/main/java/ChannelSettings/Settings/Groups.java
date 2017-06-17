package ChannelSettings.Settings;

import Interfaces.ChannelSetting;
import Interfaces.Command;

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
