package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class UseTimeStamps implements GuildToggle {

    @Override
    public String name() {
        return "UseTimeStamps";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.useTimeStamps = !config.useTimeStamps;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.useTimeStamps;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
