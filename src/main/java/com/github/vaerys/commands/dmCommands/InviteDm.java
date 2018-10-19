package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.general.Invite;
import com.github.vaerys.enums.ChannelSetting;

public class InviteDm extends Invite {

    @Override
    public ChannelSetting channel() {
        return ChannelSetting.FROM_DM;
    }
}
