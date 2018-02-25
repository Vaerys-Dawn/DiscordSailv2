package com.github.vaerys.commands.mention;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.MentionCommand;
import sx.blah.discord.handle.obj.Permissions;

public class SetPrefix extends MentionCommand {

    protected static final String[] NAMES = new String[]{"SetCommandPrefix", "SetPrefixCommand", "SetPrefix"};
    protected static final String USAGE = "[Prefix]";
    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
    protected static final boolean REQUIRES_ARGS = true;
    protected static final boolean DO_ADMIN_LOGGING = true;

    @Override
    public String execute(String args, CommandObject command) {
        boolean isAlphanumeric = args.matches("[A-Za-z0-9]+");
        if (args.contains(" ") || args.contains("\n") || args.length() > 5 || args.contains("#") || args.contains("@") || isAlphanumeric) {
            return "> Not a valid Prefix.";
        }
        command.guild.config.setPrefixCommand(args);
        return "> Command Prefix set to: " + args;
    }

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Updates the Command Prefix.\n" +
                "**Prefixes cannot:**\n" +
                "> Contain spaces, newlines, @ or #\n" +
                "> Be composed completely of letters and numbers.\n" +
                "> Be longer than 5 characters.";
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
