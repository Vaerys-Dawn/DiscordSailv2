package Commands.DMCommands;

import Commands.DMCommandObject;
import Interfaces.DMCommand;
import Main.Utility;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class Respond implements DMCommand {

    @Override
    public String execute(String args, DMCommandObject command) {
        SplitFirstObject response = new SplitFirstObject(args);
        IUser recipient = command.client.getUserByID(response.getFirstWord());
        return sendDM(response.getRest(),command,recipient);
    }

    public static String sendDM(String args, DMCommandObject command,IUser recipient){

        if (recipient == null) {
            return "> Could Not Send Response, UserID is invalid.";
        }
        if (args == null) {
            return "> Could Not Send Response, Contents cannot be empty.";
        }
        if (Utility.sendDM(command.authorUserName + ": " + args, recipient.getStringID()).get()) {
            return "> An Error occurred while attempting to run this command.";
        } else {
            return "> Message Sent.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Respond"};
    }

    @Override
    public String description() {
        return "Sends a response to a user.";
    }

    @Override
    public String usage() {
        return "[userID] [Contents]";
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }
}
