package com.github.vaerys.commands.servers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class EditServerIP extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
        String port = "N/a";
        if (splitArgs.size() > 2) {
            port = splitArgs.get(2);
        }
        if (splitArgs.size() < 2) {
            return "> Cannot edit server IP, missing arguments.";
        }
        return command.guild.servers.editIP(command.user.longID, splitArgs.get(0), splitArgs.get(1), port, command.guild.get());
    }

    @Override
    public String[] names() {
        return new String[]{"EditServerIP"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to edit your server IP and Port.";
    }

    @Override
    public String usage() {
        return "[Server Name] [IP] (Port)";
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
    public void init() {

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
