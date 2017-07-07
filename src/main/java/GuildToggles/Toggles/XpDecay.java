package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class XpDecay implements GuildToggle {
    @Override
    public String name() {
        return "XpDecay";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.xpDecay = !config.xpDecay;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.xpDecay;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().xpDecay;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
