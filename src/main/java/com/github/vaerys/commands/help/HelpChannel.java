package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class HelpChannel extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        for (ChannelSetting s : command.guild.channelSettings) {
            if (s.name().equalsIgnoreCase(args)) {
                RequestHandler.sendEmbedMessage("", s.getInfo(command), command.channel.get());
                return null;
            }
        }
        return "> Could not find Channel Type.";
    }

    @Override
    public String[] names() {
        return new String[]{"HelpChannel", "ChannelHelp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives information about a specific channel type or setting.";
    }

    @Override
    public String usage() {
        return "[Channel Type/Setting]";
    }

    @Override
    public String type() {
        return TYPE_HELP;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
