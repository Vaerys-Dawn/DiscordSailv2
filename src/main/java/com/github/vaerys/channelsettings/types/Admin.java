package com.github.vaerys.channelsettings.types;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.Report;
import com.github.vaerys.commands.help.SilentReport;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class Admin extends ChannelSetting {
    @Override
    public String name() {
        return Command.CHANNEL_ADMIN;
    }

    @Override
    public boolean isSetting() {
        return false;
    }

    @Override
    public String desc(CommandObject command) {
        return "Where messages created by the " + new Report().getCommand(command) + " and " + new SilentReport().getCommand(command) + " commands are sent.";
    }
}
