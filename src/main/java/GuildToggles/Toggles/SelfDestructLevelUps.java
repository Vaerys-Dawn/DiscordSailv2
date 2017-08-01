package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

public class SelfDestructLevelUps implements GuildToggle {
    @Override
    public String name() {
        return "SelfDestructLevelUps";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.selfDestructLevelUps = !config.selfDestructLevelUps;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.selfDestructLevelUps;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().selfDestructLevelUps;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
