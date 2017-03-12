package Commands.DMCommands;

import Interfaces.DMCommand;
import Commands.DMCommandObject;
import Main.Globals;
import POGOs.GlobalData;

/**
 * Created by Vaerys on 10/02/2017.
 */
public class BlockUser implements DMCommand{
    @Override
    public String execute(String args, DMCommandObject command) {
        if (command.authorID.equals(Globals.creatorID)){
            GlobalData globalData = Globals.getGlobalData();
            boolean worked = false;
            String userID = args.split(" ")[0];
            if (userID.equals(Globals.creatorID)){
                return "> What are you doing. Don't try to block yourself.";
            }
            if (globalData != null){
                worked = globalData.blockUserFromDMS(userID);
            }
            if (worked) {
                return "> User was Blocked.";
            }else {
                return "> An Error Occurred.";
            }
        }else {
            return command.notAllowed;
        }
    }

    @Override
    public String[] names() {
        return new String[]{"BlockUser"};
    }

    @Override
    public String description() {
        return "Allows the author to block a user from sending them DMs.";
    }

    @Override
    public String usage() {
        return "[User ID]";
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
