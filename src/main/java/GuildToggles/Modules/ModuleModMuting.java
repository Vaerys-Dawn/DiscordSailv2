package GuildToggles.Modules;

import Commands.Admin.Mute;
import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleModMuting implements GuildToggle {
    @Override
    public String name() {
        return "ModMute";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleModMute = !config.moduleModMute;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleModMute;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleModMute;
    }

    @Override
    public void execute(CommandObject command) {
        command.removeCommand(new Mute().names());
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
