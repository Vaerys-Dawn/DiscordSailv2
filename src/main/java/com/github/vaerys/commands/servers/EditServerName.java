package com.github.vaerys.commands.servers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class EditServerName implements Command{
    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
        if (splitArgs.size() < 2) {
            return "> Cannot Edit Server Name as no new name was specified.";
        }
        return command.guild.servers.editServerName(command.user.longID, splitArgs.get(0), splitArgs.get(1), command.guild.get());
    }

    @Override
    public String[] names() {
        return new String[]{"EditServerName"};
    }

    @Override
    public String description() {
        return "Allows you to edit your server name.";
    }

    @Override
    public String usage() {
        return "[Server Name] [New Server Name]";
    }

    @Override
    public String type() {
        return TYPE_SERVERS;
    }

    @Override
    public String channel() {
        return CHANNEL_SERVERS;
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
