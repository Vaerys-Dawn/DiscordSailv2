package GuildToggles.Modules;

import Commands.CommandObject;
import Interfaces.Command;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleServers implements GuildToggle {

    @Override
    public String name() {
        return "Servers";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleServers = !config.moduleServers;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleServers;
    }

    @Override
    public void execute(CommandObject command) {
        command.removeCommandsByType(Command.TYPE_SERVERS);
        command.removeChannel(Command.CHANNEL_SERVERS);
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
