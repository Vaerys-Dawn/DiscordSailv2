package POGOs;

import Main.Globals;
import Objects.BlackListedString;
import Objects.CustomCommand;

import java.util.ArrayList;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class CustomCommands {
    boolean isInit = false;
    ArrayList<BlackListedString> bannedInputs = new ArrayList<>();
    ArrayList<BlackListedString> bannedOutputs = new ArrayList<>();
    ArrayList<CustomCommand> commands = new ArrayList<>();
    final CustomCommand commandNotFound = new CustomCommand(true,"Error","404","Command not found");

    public String addCommand(CustomCommand customCommand){
        commands.add(customCommand);
        return "Command Added";
    }

    public void initCustomCommands(){
        if (!isInit) {
            bannedInputs.add(new BlackListedString("<@", "Please do not put **mentions** in Custom Commands."));
            bannedInputs.add(new BlackListedString("discord.gg", "Please do not put **invites** in Custom Commands."));
            bannedInputs.add(new BlackListedString("discordapp.com/Invite/", "Please do not put **invites** in Custom Commands."));
            bannedOutputs.add(new BlackListedString("@everyone", "Please do not try to bypass mentions with S.A.I.L."));
            bannedOutputs.add(new BlackListedString("@here", "Please do not try to bypass mentions with S.A.I.L."));
            isInit = true;
        }
        CustomCommand echo = new CustomCommand(true, Globals.creatorID,"Echo","#args#");
        if (!exists(echo.getName())) commands.add(echo);
    }

    private boolean exists(String name){
        for (CustomCommand c : commands){
            if (c.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
