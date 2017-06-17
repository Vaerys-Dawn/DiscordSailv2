package Commands.Groups;

import Commands.CommandObject;
import Interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class ClearGroupUp implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        command.channelData.getGroupUpObjects().clear();
        return "> GroupUp list cleared.";
    }

    @Override
    public String[] names() {
        return new String[]{"ClearGroupUp","PurgeGroupUp","EmptyGroupUp"};
    }

    @Override
    public String description() {
        return "Purges the GroupUp list.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_GROUPS;
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
        return false;
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
