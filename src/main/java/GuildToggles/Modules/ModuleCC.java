package GuildToggles.Modules;

import Commands.CommandObject;
import GuildToggles.Toggles.ShitpostFiltering;
import Interfaces.Command;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleCC implements GuildToggle {

    @Override
    public String name() {
        return "CC";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleCC = !config.moduleCC;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleCC;
    }

    @Override
    public void execute(CommandObject command) {
        if (command.guildConfig.moduleCC) {
            return;
        } else {
            command.removeCommandsByType(Command.TYPE_CC);
            command.removeChannel(Command.CHANNEL_SHITPOST);
            command.removeToggle(new ShitpostFiltering().name());
        }
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
