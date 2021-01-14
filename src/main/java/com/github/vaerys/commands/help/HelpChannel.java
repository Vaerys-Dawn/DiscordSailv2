package com.github.vaerys.commands.help;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

public class HelpChannel extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        for (ChannelSetting s : command.guild.channelSettings) {
            if (s.toString().equalsIgnoreCase(args)) {
                s.getInfo(command).queue(command);
                return null;
            }
        }
        return "\\> Could not find Channel Type.";
    }

    @Override
    protected String[] names() {
        return new String[]{"HelpChannel", "ChannelHelp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives information about a specific messageChannel type or setting.";
    }

    @Override
    protected String usage() {
        return "[Channel Type/Setting]";
    }

    @Override
    protected SAILType type() {
        return SAILType.HELP;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
