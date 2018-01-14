package com.github.vaerys.commands.creator;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class SetPlayingStatus implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        RequestHandler.changePresence(args);
        return "> Status set to: " + args;
    }

    @Override
    public String[] names() {
        return new String[]{"SetPlayingStatus"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sets the playing status";
    }

    @Override
    public String usage() {
        return null;
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
