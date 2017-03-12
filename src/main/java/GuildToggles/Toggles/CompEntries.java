package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class CompEntries implements GuildToggle {

    @Override
    public String name() {
        return "CompEntries";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.compEntries = !config.compEntries;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.compEntries;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
