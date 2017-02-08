package Commands.DMCommands;

import Commands.DMCommand;
import Commands.DMCommandObject;
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
        if (recipient == null) {
            return "> Could Not Send Response, UserID is invalid.";
        }
        if (response.getRest() == null) {
            return "> Could Not Send Response, Contents cannot be empty.";
        }
        if (Utility.sendDM(response.getRest(), recipient.getID()).get()) {
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
