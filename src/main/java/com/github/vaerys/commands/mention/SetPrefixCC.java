package com.github.vaerys.commands.mention;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.MentionCommand;
import sx.blah.discord.handle.obj.Permissions;

public class SetPrefixCC extends MentionCommand {

    @Override
    public String execute(String args, CommandObject command) {
        boolean isAlphanumeric = args.matches("[A-Za-z0-9]+");
        if (args.contains(" ") || args.contains("\n") || args.length() > 5 || args.contains("#") || args.contains("@") || isAlphanumeric) {
            return "> Not a valid Prefix.";
        }
        command.guild.config.setPrefixCC(args);
        return "> Custom Command Prefix set to: " + args;
    }

    protected static final String[] NAMES = new String[]{"SetCCPrefix","SetPrefixCC"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Updates the Custom Command Prefix.\n" +
                "**Prefixes cannot:**\n" +
                "> Contain spaces, newlines, @ or #\n" +
                "> Be composed completely of letters and numbers.\n" +
                "> Be longer than 5 characters.";
    }

    protected static final String USAGE = "[Prefix]";
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