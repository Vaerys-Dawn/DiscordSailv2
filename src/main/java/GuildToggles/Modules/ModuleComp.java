package GuildToggles.Modules;

import Commands.CommandObject;
import GuildToggles.Toggles.CompEntries;
import GuildToggles.Toggles.Voting;
import Interfaces.Command;
import Interfaces.GuildToggle;
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
    public boolean getDefault() {
        return new GuildConfig().moduleComp;
    }

    @Override
    public void execute(CommandObject command) {
        command.removeCommandsByType(Command.TYPE_COMPETITION);
        command.removeToggle(new Voting().name());
        command.removeToggle(new CompEntries().name());
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
