package Commands.CC;

import Commands.CommandObject;
import Interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class DelCC implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        return command.customCommands.delCommand(args, command.author, command.guild);

    }

    @Override
    public String[] names() {
        return new String[]{"DelCC"};
    }

    @Override
    public String description() {
        return "Deletes The custom command.";
    }

    @Override
    public String usage() {
        return "[Command Name]";
    }

    @Override
    public String type() {
        return TYPE_CC;
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
