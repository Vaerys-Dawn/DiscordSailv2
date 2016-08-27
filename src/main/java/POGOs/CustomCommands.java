package POGOs;

import Main.Constants;
import Main.Globals;
import Objects.BlackListedPhrase;
import Objects.CCommand;

import java.util.ArrayList;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class CustomCommands {
    boolean isInit = false;
    ArrayList<BlackListedPhrase> blackList = new ArrayList<>();
    ArrayList<CCommand> commands = new ArrayList<>();
    final CCommand commandNotFound = new CCommand(true,"Error","404","> Command not found.");

    public String addCommand(CCommand cCommand){
        if (!exists(cCommand.getName())) {
            commands.add(cCommand);
            return "> Command Created, you can perform your new custom command by doing `" + Constants.CC_PREFIX + cCommand.getName()+  "`.";
        }
        return "> That command already exists.";
    }

    public void initCustomCommands(){
        if (!isInit) {
            blackList.add(new BlackListedPhrase("<@", "Please do not put **mentions** in Custom MessageHandler."));
            blackList.add(new BlackListedPhrase("discord.gg", "Please do not put **invites** in Custom MessageHandler."));
            blackList.add(new BlackListedPhrase("discordapp.com/Invite/", "Please do not put **invites** in Custom MessageHandler."));
            isInit = true;
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
