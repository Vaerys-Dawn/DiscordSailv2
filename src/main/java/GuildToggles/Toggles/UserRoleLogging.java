package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class UserRoleLogging implements GuildToggle {

    @Override
    public String name() {
        return "UserRoleLogging";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.userRoleLogging = !config.userRoleLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.userRoleLogging;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
