package com.github.vaerys.commands.mention;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.MentionCommand;
import sx.blah.discord.handle.obj.Permissions;

public class SetPrefix implements MentionCommand {

    @Override
    public String execute(String args, CommandObject command) {
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"SetCommandPrefix"};
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return null;
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
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
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