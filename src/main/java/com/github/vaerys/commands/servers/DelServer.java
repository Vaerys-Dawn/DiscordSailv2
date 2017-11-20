package com.github.vaerys.commands.servers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class DelServer implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        return command.guild.servers.deleteServer(command.user.get().getLongID(), args, command.guild.get());
    }

    @Override
    public String[] names() {
        return new String[]{"DelServer"};
    }

    @Override
    public String description(CommandObject command) {
        return "Removes a server from the guild's server list.";
    }

    @Override
    public String usage() {
        return "[Server Name]";
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
