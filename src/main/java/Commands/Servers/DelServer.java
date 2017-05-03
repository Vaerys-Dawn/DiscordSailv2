package Commands.Servers;

import Commands.CommandObject;
import Interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class DelServer implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        return command.servers.deleteServer(command.author.getStringID(), args, command.guild);
    }

    @Override
    public String[] names() {
        return new String[]{"DelServer"};
    }

    @Override
    public String description() {
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
