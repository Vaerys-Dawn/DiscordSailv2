package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class MuteRepeatOffender implements GuildToggle {

    @Override
    public String name() {
        return "MuteRepeatOffender";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.muteRepeatOffenders = !config.muteRepeatOffenders;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.muteRepeatOffenders;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().muteRepeatOffenders;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
