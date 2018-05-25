package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.general.GetReminders;
import com.github.vaerys.enums.ChannelSetting;

public class GetRemindersDM extends GetReminders {

    @Override
    public ChannelSetting channel() {
        return ChannelSetting.FROM_DM;
    }

}
