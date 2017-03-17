package Commands.Creator;

import Commands.CommandObject;
import Handlers.MessageHandler;
import Interfaces.Command;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/02/2017.
 */
public class Sudo implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject sudo = new SplitFirstObject(args);
        String commandArgs = "";
        if (command.message.getMentions().size() > 0) {
            command.setAuthor(command.message.getMentions().get(0));
            SplitFirstObject commandName = new SplitFirstObject(sudo.getRest());
            if (commandName.getFirstWord() == null) {
                return "> You need to specify some arguments.";
            }
            if (commandName.getRest() != null) {
                commandArgs = commandName.getRest();
            }
            new MessageHandler(commandName.getFirstWord(), commandArgs, command);
            return null;
        } else {
            return "> Error no user Specified.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Sudo"};
    }

    @Override
    public String description() {
        return "Runs a command as though you were someone else.\n" + ownerOnly;
    }

    @Override
    public String usage() {
        return "[@user] [Command + args]";
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
        return true;
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
