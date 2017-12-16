package com.github.vaerys.commands.creator;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.creator.directmessages.WhoWasThat;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class WhoIsThis implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        return new WhoWasThat().execute(args,command);
    }

    @Override
    public String[] names() {
        return new String[]{"WhoIsThis"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives global info about a user";
    }

    @Override
    public String usage() {
        return "(UserID)";
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
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