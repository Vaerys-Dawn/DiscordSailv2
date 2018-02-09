package com.github.vaerys.channelsettings.types;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class AdminLog extends ChannelSetting {
    @Override
    public String name() {
        return Command.CHANNEL_ADMIN_LOG;
    }

    @Override
    public boolean isSetting() {
        return false;
    }

    @Override
    public String desc(CommandObject command) {
        return "Where all of the admin type logging will be sent.";
    }
}
