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

    @Override
    protected String[] names() {
        return new String[]{"SetCCPrefix", "SetPrefixCC"};
    }

    @Override
    public String description(CommandObject command) {
        return "Updates the Custom Command Prefix.\n" +
                "**Prefixes cannot:**\n" +
                "> Contain spaces, newlines, @ or #\n" +
                "> Be composed completely of letters and numbers.\n" +
                "> Be longer than 5 characters.";
    }

    @Override
    protected String usage() {
        return "[Prefix]";
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
