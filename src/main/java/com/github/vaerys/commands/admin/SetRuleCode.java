package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

public class SetRuleCode extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        command.message.delete();
        if (args.equalsIgnoreCase("remove")) {
            command.guild.config.setRuleCode(null);
            return "> Rule Code removed.";
        } else {
            command.guild.config.setRuleCode(args);
            return "> Rule Code Added.";
        }
    }

    protected static final String[] NAMES = new String[]{"SetRuleCode"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        if (command.guild.config.xpGain) {
            return "Adds a Rule code to the bot for users to use to get some pixels and a star on their profile.";
        } else {
            return "Adds a Rule code to the bot for users to use to get a star on their profile.";
        }
    }

    protected static final String USAGE = "[New Code]/Remove";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}