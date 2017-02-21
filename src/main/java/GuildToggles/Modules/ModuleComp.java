package GuildToggles.Modules;

import Commands.Command;
import Commands.CommandObject;
import GuildToggles.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleComp implements GuildToggle {

    boolean state = false;

    @Override
    public String name() {
        return "ModuleComp";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleComp = !config.moduleComp;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleComp;
    }

    @Override
    public void execute(CommandObject command) {
        if (command.guildConfig.moduleComp){
            return;
        }else {
            command.removeCommandsByType(Command.TYPE_COMPETITION);
        }
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
