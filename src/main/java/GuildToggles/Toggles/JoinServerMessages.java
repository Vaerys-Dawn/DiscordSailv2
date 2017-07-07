package GuildToggles.Toggles;

import Commands.Admin.SetJoinMessage;
import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 07/07/2017.
 */
public class JoinServerMessages implements GuildToggle {
    @Override
    public String name() {
        return "JoinServerMessages";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.joinsServerMessages = !config.joinsServerMessages;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.joinsServerMessages;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().joinsServerMessages;
    }

    @Override
    public void execute(CommandObject command) {
        command.removeCommand(new SetJoinMessage().names());
    }

    @Override
    public boolean isModule() {
        return false;
    }
}
