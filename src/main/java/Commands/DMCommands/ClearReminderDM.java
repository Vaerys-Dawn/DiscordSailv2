package Commands.DMCommands;

import Commands.CommandObject;
import Commands.DMCommandObject;
import Commands.General.ClearReminder;
import Interfaces.DMCommand;

/**
 * Created by Vaerys on 19/04/2017.
 */
public class ClearReminderDM implements DMCommand{
    @Override
    public String execute(String args, DMCommandObject command) {
        CommandObject object = new CommandObject(command);
        return new ClearReminder().execute(args,object);
    }

    @Override
    public String[] names() {
        return new ClearReminder().names();
    }

    @Override
    public String description() {
        return new ClearReminder().description();
    }

    @Override
    public String usage() {
        return new ClearReminder().usage();
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }
}
