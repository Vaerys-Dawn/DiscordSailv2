package GuildToggles.Modules;

import Commands.CommandObject;
import Interfaces.Command;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class ModuleGroups implements GuildToggle{
    @Override
    public String name() {
        return "Groups";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleGroups = !config.moduleGroups;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleGroups;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleGroups;
    }

    @Override
    public void execute(CommandObject command) {
        command.removeCommandsByType(Command.TYPE_GROUPS);
        command.removeChannel(Command.CHANNEL_GROUPS);
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
