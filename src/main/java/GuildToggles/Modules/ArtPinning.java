package GuildToggles.Modules;

import Commands.CommandObject;
import GuildToggles.Toggles.AutoArtPinning;
import Interfaces.Command;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 13/05/2017.
 */
public class ArtPinning implements GuildToggle{
    @Override
    public String name() {
        return "ArtPinning";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.artPinning = !config.artPinning;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.artPinning;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().artPinning;
    }

    @Override
    public void execute(CommandObject command) {
        command.removeChannel(Command.CHANNEL_ART);
        command.removeToggle(new AutoArtPinning().name());
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
