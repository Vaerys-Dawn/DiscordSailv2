package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class DenyInvites implements GuildToggle {

    @Override
    public String name() {
        return "DenyInvites";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.denyInvites = !config.denyInvites;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.denyInvites;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
