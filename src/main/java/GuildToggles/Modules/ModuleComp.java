package GuildToggles.Modules;

import Commands.Command;
import Commands.CommandObject;
import GuildToggles.GuildToggle;
import GuildToggles.Toggles.CompEntries;
import GuildToggles.Toggles.Voting;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleComp implements GuildToggle {

    boolean state = false;

    @Override
    public String name() {
        return "Comp";
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
            command.removeToggle(new Voting().name());
            command.removeToggle(new CompEntries().name());
        }
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
