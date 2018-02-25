package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.help.BotInfo;
import com.github.vaerys.enums.ChannelSetting;

public class BotInfoDm extends BotInfo {

    @Override
    public ChannelSetting channel() {
        return ChannelSetting.FROM_DM;
    }
}
