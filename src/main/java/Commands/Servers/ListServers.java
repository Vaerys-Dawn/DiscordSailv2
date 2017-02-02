package Commands.Servers;

import Commands.Command;
import Commands.CommandObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ListServers implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        return command.servers.getServerList(command);
    }

    @Override
    public String[] names() {
        return new String[]{"ListServers","Servers","ServerList"};
    }

    @Override
    public String description() {
        return "Shows a listing of this guild's user registered servers.";
    }

    @Override
    public String usage() {
        return null;
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
