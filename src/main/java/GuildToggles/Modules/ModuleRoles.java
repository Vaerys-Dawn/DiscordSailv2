package GuildToggles.Modules;

import Commands.CommandObject;
import Interfaces.Command;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleRoles implements GuildToggle {

    @Override
    public String name() {
        return "Roles";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleRoles = !config.moduleRoles;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleRoles;
    }

    @Override
    public void execute(CommandObject command) {
        if (command.guildConfig.moduleRoles){
            return;
        }else {
            command.removeCommandsByType(Command.TYPE_ROLE_SELECT);
        }
    }

    @Override
    public boolean isModule() {
        return true;
    }
}