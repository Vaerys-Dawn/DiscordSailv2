package OldCode;

import Main.Globals;
import Objects.CCommandObject;

import java.util.ArrayList;

/**
 * Created by Vaerys on 25/06/2016.
 */
public class CustomCommands {


    //1 = Command name
    //0 = User ID
    //2 = Contents

    ArrayList<String[]> commands = new ArrayList<>();
//    final String[] commandNotFound = {"noUser", "404", "No Command with that name found."};

    public CCommandObject convertCommand(String command){
        boolean islocked = false;
        boolean isfound = false;
        String commandName = null;
        String userID = null;
        String commandContents = null;
        for (String[] c: commands){
            if (c[1].equalsIgnoreCase(command)){
                isfound = true;
                commandName = c[1];
                commandContents = c[2];
                if (c[0].contains("LockedCommand")){
                    islocked = true;
                    userID = Globals.getClient().getOurUser().getID();
                }else {
                    islocked = false;
                    userID = c[0];
                }
            }
        }
        if (isfound) {
            return new CCommandObject(islocked, userID, commandName, commandContents, false);
        }else {
            return null;
        }
    }
}
