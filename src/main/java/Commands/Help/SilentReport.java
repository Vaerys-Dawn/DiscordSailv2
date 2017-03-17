package Commands.Help;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class SilentReport extends Report implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        Utility.deleteMessage(command.message);
        Report.report(args,command,true);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"SilentReport"};
    }

    @Override
    public String description() {
        return "Can be used to send a user report to the server staff.\n" +
                indent + " It will also remove the message used to call the command.";
    }

    @Override
    public String usage() {
        return "[@User]/[User ID] [Report]";
    }
}
