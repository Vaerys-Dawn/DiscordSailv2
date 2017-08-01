package GuildToggles.Modules;

import Commands.CommandObject;
import GuildToggles.Toggles.ReactToLevelUp;
import GuildToggles.Toggles.SelfDestructLevelUps;
import GuildToggles.Toggles.XpDecay;
import GuildToggles.Toggles.XpGain;
import Interfaces.Command;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class ModulePixels implements GuildToggle {
    @Override
    public String name() {
        return "Pixels";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.modulePixels = !config.modulePixels;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.modulePixels;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().modulePixels;
    }

    @Override
    public void execute(CommandObject command) {
        command.removeCommandsByType(Command.TYPE_PIXEL);
        command.removeToggle(new XpDecay().name());
        command.removeToggle(new XpGain().name());
        command.removeToggle(new SelfDestructLevelUps().name());
        command.removeToggle(new ReactToLevelUp().name());
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
