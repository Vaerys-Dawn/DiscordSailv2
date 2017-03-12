package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class LoginMessage implements GuildToggle {

    @Override
    public String name() {
        return "LoginMessage";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.loginMessage = !config.generalLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.loginMessage;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
