package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class HelpChannel extends Command {

    protected static final String[] NAMES = new String[]{"HelpChannel", "ChannelHelp"};
    protected static final String USAGE = "[Channel Type/Setting]";
    protected static final SAILType COMMAND_TYPE = SAILType.HELP;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    protected static final boolean REQUIRES_ARGS = true;
    protected static final boolean DO_ADMIN_LOGGING = false;

    @Override
    public String execute(String args, CommandObject command) {
        for (ChannelSetting s : command.guild.channelSettings) {
            if (s.toString().equalsIgnoreCase(args)) {
                RequestHandler.sendEmbedMessage("", s.getInfo(command), command.channel.get());
                return null;
            }
        }
        return "> Could not find Channel Type.";
    }

    @Override
    protected String[] names() {
        return new String[]{"HelpChannel", "ChannelHelp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives information about a specific channel type or setting.";
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
    protected Permissions[] perms() {
        return new Permissions[0];
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
