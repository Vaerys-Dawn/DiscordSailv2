package GuildToggles.Modules;

import Commands.CommandObject;
import Commands.General.SetGender;
import Commands.General.SetQuote;
import Commands.General.UserInfo;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleMe implements GuildToggle {

    @Override
    public String name() {
        return "UserInfo";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleMe = !config.moduleMe;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleMe;
    }

    @Override
    public void execute(CommandObject command) {
        command.removeCommand(new UserInfo().names());
        command.removeCommand(new SetGender().names());
        command.removeCommand(new SetQuote().names());
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
