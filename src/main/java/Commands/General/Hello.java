package Commands.General;

import Commands.Command;
import Commands.CommandObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Hello implements Command{
    @Override
    public String execute(String args, CommandObject command) {
        return "> Hello " + command.author.getDisplayName(command.guild) + ".";
    }

    @Override
    public String[] names() {
        return new String[]{"Hello","Hi","Greetings"};
    }

    @Override
    public String description() {
        return "Says Hello.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
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
