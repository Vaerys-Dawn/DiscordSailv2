package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

/**
 * Created by Vaerys on 17/06/2017.
 */
public class XpDenied extends ChannelSetting {
    @Override
    public String name() {
        return Command.CHANNEL_XP_DENIED;
    }

    @Override
    public boolean isSetting() {
        return true;
    }

    @Override
    public String desc(CommandObject command) {
        return "When this setting is on a channel no pixels will be gained in the  channel.";
    }
}
