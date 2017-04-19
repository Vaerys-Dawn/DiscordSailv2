package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class DontLogBot implements GuildToggle{

    @Override
    public String name() {
        return "DontlogBot";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.dontLogBot = !config.dontLogBot;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.dontLogBot;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().dontLogBot;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
