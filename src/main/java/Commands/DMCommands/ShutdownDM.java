package Commands.DMCommands;

import Commands.CommandObject;
import Commands.Creator.Shutdown;
import Commands.DMCommandObject;
import Interfaces.DMCommand;


/**
 * Created by Vaerys on 23/04/2017.
 */
public class ShutdownDM implements DMCommand {
    @Override
    public String execute(String args, DMCommandObject command) {
        CommandObject commandObject = new CommandObject(command);
        return new Shutdown().execute(args,commandObject);
    }

    @Override
    public String[] names() {
        return new String[]{"Shutdown"};
    }

    @Override
    public String description() {
        return "Shuts the bot down.";
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
    public boolean requiresArgs() {
        return false;
    }
}
