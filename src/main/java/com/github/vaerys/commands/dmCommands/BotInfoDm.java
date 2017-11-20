package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.help.BotInfo;

public class BotInfoDm extends BotInfo {

    @Override
    public String channel() {
        return CHANNEL_DM;
    }
}
