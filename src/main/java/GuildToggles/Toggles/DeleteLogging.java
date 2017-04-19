package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class DeleteLogging implements GuildToggle {

    @Override
    public String name() {
        return "DeleteLogging";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.deleteLogging = !config.deleteLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.deleteLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().deleteLogging;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
