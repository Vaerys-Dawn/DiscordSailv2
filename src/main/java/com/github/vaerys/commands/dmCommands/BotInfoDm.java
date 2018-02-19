package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.help.BotInfo;
import com.github.vaerys.templates.ChannelSetting;

public class BotInfoDm extends BotInfo {


    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.FROM_DM;
    @Override
    public ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    public void init() {

    }
}
