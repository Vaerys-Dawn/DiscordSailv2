package Commands.DMCommands;

import Commands.CommandObject;
import Commands.DMCommandObject;
import Commands.General.RemindMe;
import Interfaces.DMCommand;

/**
 * Created by Vaerys on 19/04/2017.
 */
public class ReminderDM implements DMCommand{
    @Override
    public String execute(String args, DMCommandObject command) {
        CommandObject object = new CommandObject(command);
        return new RemindMe().execute(args,object);
    }

    @Override
    public String[] names() {
        return new RemindMe().names();
    }

    @Override
    public String description() {
        return new RemindMe().description();
    }

    @Override
    public String usage() {
        return new RemindMe().usage();
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }
}
