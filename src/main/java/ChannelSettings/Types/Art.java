package ChannelSettings.Types;

import Interfaces.ChannelSetting;
import Interfaces.Command;

/**
 * Created by Vaerys on 12/05/2017.
 */
public class Art implements ChannelSetting {
    @Override
    public String type() {
        return Command.CHANNEL_ART;
    }

    @Override
    public boolean isSetting() {
        return false;
    }
}
