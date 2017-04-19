package GuildToggles.Toggles;

import Commands.CommandObject;
import Commands.General.SlashList;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class SlashCommands implements GuildToggle {
    @Override
    public String name() {
        return "SlashCommands";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.slashCommands = !config.slashCommands;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.slashCommands;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().slashCommands;
    }

    @Override
    public void execute(CommandObject command) {
        command.removeCommand(new SlashList().names());
    }

    @Override
    public boolean isModule() {
        return false;
    }
}
