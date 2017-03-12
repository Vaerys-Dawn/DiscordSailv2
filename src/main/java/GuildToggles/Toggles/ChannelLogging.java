package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class ChannelLogging implements GuildToggle {

    @Override
    public String name() {
        return "ChannelLogging";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.channelLogging = !config.channelLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.channelLogging;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
