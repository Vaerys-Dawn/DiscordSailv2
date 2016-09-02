package POGOs;

import Main.Constants;
import Main.Globals;
import Objects.BlackListedPhrase;
import Objects.CCommand;

import java.util.ArrayList;

// TODO: 31/08/2016 Add the ability to create custom commands
// TODO: 31/08/2016 Add the ability to delete custom commands
// TODO: 31/08/2016 Add the ability to edit custom commands
// TODO: 31/08/2016 Add the ability to list all custom commands
// TODO: 31/08/2016 Add the ability to list all custom commands made by a certain user
// TODO: 31/08/2016 Add the ability to search for a custom command based on name or contents
// TODO: 31/08/2016 Add the ability to vote to remove a custom command (user must be trusted in order to initiate the vote) (must get 10 votes in 2 hour to remove)
// TODO: 31/08/2016 Add blacklisting of phrases to custom command creation, editing and execution
// TODO: 31/08/2016 Add the ability to see the amount of times the command is run
// TODO: 02/09/2016 Add Regex tags i.e Author, Args, RoleListRole, Embed, Random

// TODO: 31/08/2016 (Optional) Add Fweeee to CC.RewardBag (using voicebot functionality)
// TODO: 31/08/2016 (Optional) Add the ability to have images uploaded to the guild rather than posting a link (maybe)

/**
 * Created by Vaerys on 14/08/2016.
 */
public class CustomCommands {
    public boolean properlyInit = false;
    boolean blackListInit = false;
    ArrayList<BlackListedPhrase> blackList = new ArrayList<>();
    ArrayList<CCommand> commands = new ArrayList<>();
    final CCommand commandNotFound = new CCommand(true,"Error","404","> Command not found.");

    public String addCommand(boolean isLocked, String userID, String commandName, String commandContents){
        CCommand cCommand = new CCommand(isLocked,userID,commandName,commandContents);
        if (!exists(cCommand.getName())) {
            commands.add(cCommand);
            return "> Command Created, you can perform your new custom command by doing `" + Constants.CC_PREFIX + cCommand.getName()+  "`.";
        }
        return "> That command already exists.";
    }

    public void initCustomCommands(){
        if (!blackListInit) {
            blackList.add(new BlackListedPhrase("<@", "Please do not put **mentions** in Custom Commands."));
            blackList.add(new BlackListedPhrase("discord.gg", "Please do not put **invites** in Custom Commands."));
            blackList.add(new BlackListedPhrase("discordapp.com/Invite/", "Please do not put **invites** in Custom Commands."));
            blackList.add(new BlackListedPhrase("@everyone","Please go not put **mentions** in Custom Commands."));
            blackList.add(new BlackListedPhrase("@here","Please go not put **mentions** in Custom Commands."));
            blackListInit = true;
        }
        CCommand echo = new CCommand(true, Globals.creatorID,"Echo","#args#");
        if (!exists(echo.getName())) commands.add(echo);
    }

    private boolean exists(String name){
        for (CCommand c : commands){
            if (c.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    private ArrayList<CCommand> getCommandList(){
        return commands;
    }
}
