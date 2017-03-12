package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class MentionSpam implements GuildToggle {

    @Override
    public String name() {
        return "MentionSpam";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.maxMentions = !config.maxMentions;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.maxMentions;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
