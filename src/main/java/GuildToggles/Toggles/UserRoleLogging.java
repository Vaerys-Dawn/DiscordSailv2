package GuildToggles.Toggles;

import Commands.CommandObject;
import GuildToggles.GuildToggle;
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
        return config.userRoleUpdatelogging = !config.userRoleUpdatelogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.userRoleUpdatelogging;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
