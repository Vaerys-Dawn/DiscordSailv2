package GuildToggles.Toggles;

import Commands.CommandObject;
import GuildToggles.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ShitpostFiltering implements GuildToggle {

    boolean state = false;

    @Override
    public String name() {
        return "ShitpostFiltering";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.shitPostFiltering = !config.shitPostFiltering;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.shitPostFiltering;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
