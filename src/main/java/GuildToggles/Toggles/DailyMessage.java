package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class DailyMessage implements GuildToggle {

    @Override
    public String name() {
        return "DailyMessage";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.dailyMessage = !config.dailyMessage;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.dailyMessage;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().dailyMessage;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
