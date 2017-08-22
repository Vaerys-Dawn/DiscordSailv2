package com.github.vaerys.commands.servers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ServerObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Server implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        for (ServerObject s : command.guild.servers.getServers()) {
            if (s.getName().equalsIgnoreCase(args)) {
                StringBuilder builder = new StringBuilder();
                builder.append("**" + s.getName() + "**\n");
                builder.append("**IP:** " + s.getServerIP() + " **Port:** " + s.getServerPort() + "\n");
                builder.append("**Listing Creator:** " + command.guild.get().getUserByID(s.getCreatorID()).getDisplayName(command.guild.get()) + "\n");
                builder.append(s.getServerDesc());
                Utility.sendDM(builder.toString(), command.user.longID);
                return "> Server info has been sent to your DMs";
            }
        }
        return "> Server with that name not found.";
    }

    @Override
    public String[] names() {
        return new String[]{"Server"};
    }

    @Override
    public String description() {
        return "Lists the information about a specific server.";
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
