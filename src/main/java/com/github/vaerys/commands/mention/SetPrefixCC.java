package com.github.vaerys.commands.mention;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.MentionCommand;
import sx.blah.discord.handle.obj.Permissions;

public class SetPrefixCC implements MentionCommand {

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
    public String[] names() {
        return new String[]{"SetCCPrefix","SetPrefixCC"};
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
    public String usage() {
        return "[Prefix]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
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