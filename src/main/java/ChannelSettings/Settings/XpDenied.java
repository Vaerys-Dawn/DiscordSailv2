package ChannelSettings.Settings;

import Interfaces.ChannelSetting;
import Interfaces.Command;

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
