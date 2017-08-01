package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

public class ReactToLevelUp implements GuildToggle {
    @Override
    public String name() {
        return "ReactToLevelUp";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.reactToLevelUp = !config.reactToLevelUp;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.reactToLevelUp;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().reactToLevelUp;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
