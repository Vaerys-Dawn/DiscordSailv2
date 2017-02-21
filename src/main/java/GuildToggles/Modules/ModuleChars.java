package GuildToggles.Modules;

import Commands.Command;
import Commands.CommandObject;
import GuildToggles.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleChars implements GuildToggle {

    boolean state = false;

    @Override
    public String name() {
        return "ModuleChars";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleChars = !config.moduleChars;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleChars;
    }

    @Override
    public void execute(CommandObject command) {
        command.removeCommandsByType(Command.TYPE_CHARACTER);
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
