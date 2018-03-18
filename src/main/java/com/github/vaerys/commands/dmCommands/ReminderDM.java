package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.general.RemindMe;
import com.github.vaerys.enums.ChannelSetting;

/**
 * Created by Vaerys on 27/02/2017.
 */
public class ReminderDM extends RemindMe {

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.FROM_DM;
    }
}
