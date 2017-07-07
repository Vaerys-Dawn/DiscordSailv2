package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class XpGain implements GuildToggle {
    @Override
    public String name() {
        return "XpGain";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.xpGain = !config.xpGain;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.xpGain;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().xpGain;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
