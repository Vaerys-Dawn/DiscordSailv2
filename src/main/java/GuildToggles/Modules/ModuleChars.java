package GuildToggles.Modules;

import Commands.CommandObject;
import Interfaces.Command;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleChars implements GuildToggle {

    @Override
    public String name() {
        return "Chars";
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
        if (command.guildConfig.moduleChars){
            return;
        }else {
            command.removeCommandsByType(Command.TYPE_CHARACTER);
        }
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
