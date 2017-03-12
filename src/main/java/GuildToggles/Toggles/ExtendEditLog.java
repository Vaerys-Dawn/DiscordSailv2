package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class ExtendEditLog implements GuildToggle {

    @Override
    public String name() {
        return "ExtendEditLog";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.extendEditLog = !config.extendEditLog;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.extendEditLog;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
