package POGOs;

import Main.Constants;
import Main.Globals;
import Objects.BlackListObject;
import Objects.CCommandObject;

import java.util.ArrayList;

// TODO: 31/08/2016 Add the ability to create custom commands
// TODO: 31/08/2016 Add the ability to delete custom commands
// TODO: 31/08/2016 Add the ability to edit custom commands
// TODO: 31/08/2016 Add the ability to list all custom commands
// TODO: 31/08/2016 Add the ability to list all custom commands made by a certain user
// TODO: 31/08/2016 Add the ability to search for a custom command based on name, contents, or ShitPost (use separate commands)
// TODO: 31/08/2016 Add the ability to vote to remove a custom command (user must be trusted in order to initiate the vote) (must get 10 votes in 2 hour to remove)
// TODO: 31/08/2016 Add blacklisting of phrases to custom command creation, editing and execution
// TODO: 31/08/2016 Add the ability to see the amount of times the command is run
// TODO: 02/09/2016 Add Regex tags i.e Author, Args, RoleListRole, Embed, Random
// TODO: 04/09/2016 Add ShitPost filtering
// TODO: 04/09/2016 Add on creation tags
// TODO: 04/09/2016 Add Command transferring.
// TODO: 04/09/2016 Make it so that the command is default to ShitPost upon creation in the #shitpost channel
// TODO: 04/09/2016 maye a helpful command list that those with manage messages can add to
// TODO: 31/08/2016 (Maybe) Add Fweeee to CC.RewardBag (using VoiceBot functionality)
// TODO: 31/08/2016 (Maybe) Add the ability to have images uploaded to the guild rather than posting a link (maybe)

/**
 * Created by Vaerys on 14/08/2016.
 */
public class CustomCommands {
    public boolean properlyInit = false;
    boolean blackListInit = false;
    ArrayList<BlackListObject> blackList = new ArrayList<>();
    ArrayList<CCommandObject> commands = new ArrayList<>();
    final CCommandObject commandNotFound = new CCommandObject(true,"Error","404","> Command not found.");

    public String addCommand(boolean isLocked, String userID, String commandName, String commandContents){
        CCommandObject cCommandObject = new CCommandObject(isLocked,userID,commandName,commandContents);
        if (!exists(cCommandObject.getName())) {
            commands.add(cCommandObject);
            return "> Command Created, you can perform your new custom command by doing `" + Constants.PREFIX_CC + cCommandObject.getName()+  "`.";
        }
        return "> That command already exists.";
    }

    public void initCustomCommands(){
        if (!properlyInit) {
            blackList.add(new BlackListObject("<@", "Please do not put **mentions** in Custom Commands."));
            blackList.add(new BlackListObject("discord.gg", "Please do not put **invites** in Custom Commands."));
            blackList.add(new BlackListObject("discordapp.com/Invite/", "Please do not put **invites** in Custom Commands."));
            blackList.add(new BlackListObject("@everyone","Please go not put **mentions** in Custom Commands."));
            blackList.add(new BlackListObject("@here","Please go not put **mentions** in Custom Commands."));
            blackListInit = true;
            commands.add(new CCommandObject(true, Globals.creatorID,"Echo","#args#"));
        }
    }

    private boolean exists(String name){
        for (CCommandObject c : commands){
            if (c.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    private ArrayList<CCommandObject> getCommandList(){
        return commands;
    }
}
