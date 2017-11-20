package com.github.vaerys.channelsettings.types;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

/**
 * Created by Vaerys on 12/05/2017.
 */
public class Art implements ChannelSetting {
    @Override
    public String name() {
        return Command.CHANNEL_ART;
    }

    @Override
    public boolean isSetting() {
        return false;
    }

    @Override
    public String desc(CommandObject command) {
        return "Where art is enabled to be pinned by users via sail.";
    }
}
