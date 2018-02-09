package com.github.vaerys.channelsettings.settings;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class Shitpost extends ChannelSetting{
    @Override
    public String name() {
        return Command.CHANNEL_SHITPOST;
    }

    @Override
    public boolean isSetting() {
        return true;
    }

    @Override
    public String desc(CommandObject command) {
        return "When this setting is on a channel all custom commands create in it will be " +
                "automatically tagged shitpost. shitpost commands can only be run in shitpost channels.";
    }
}
