package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 18/03/2017.
 */
public class RoleIsToggle implements GuildToggle {
    @Override
    public String name() {
        return "RoleIsToggle";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.roleIsToggle = !config.roleIsToggle;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.roleIsToggle;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
