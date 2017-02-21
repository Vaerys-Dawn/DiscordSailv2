package GuildToggles.Toggles;

import Commands.CommandObject;
import GuildToggles.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class AdminLogging implements GuildToggle {

    boolean state = false;

    @Override
    public String name() {
        return "AdminLogging";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.adminLogging = !config.adminLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.adminLogging;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
