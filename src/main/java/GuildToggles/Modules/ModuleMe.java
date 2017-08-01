package GuildToggles.Modules;

import Commands.CommandObject;
import Commands.General.EditLinks;
import Commands.General.SetGender;
import Commands.General.SetQuote;
import Commands.General.UserInfo;
import GuildToggles.Toggles.UserInfoShowsDate;
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
    public boolean getDefault() {
        return new GuildConfig().moduleMe;
    }

    @Override
    public void execute(CommandObject command) {
        command.removeCommand(new UserInfo().names());
        command.removeCommand(new SetGender().names());
        command.removeCommand(new SetQuote().names());
        command.removeCommand(new EditLinks().names());
        command.removeToggle(new UserInfoShowsDate().name());
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
