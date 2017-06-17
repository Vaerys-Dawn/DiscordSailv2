package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 13/05/2017.
 */
public class AutoArtPinning implements GuildToggle {
    @Override
    public String name() {
        return "AutoArtPinning";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.autoArtPinning = !config.autoArtPinning;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.autoArtPinning;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().autoArtPinning;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
