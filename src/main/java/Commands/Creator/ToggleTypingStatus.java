package Commands.Creator;

import Commands.CommandObject;
import Interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 10/05/2017.
 */
public class ToggleTypingStatus implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        command.channel.toggleTypingStatus();
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Typing"};
    }

    @Override
    public String description() {
        return "Toggles the typing status on a channel.";
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
