package GuildToggles.Toggles;

import Commands.CommandObject;
import Interfaces.GuildToggle;
import POGOs.GuildConfig;

public class UserInfoShowsDate implements GuildToggle {
    @Override
    public String name() {
        return "UserInfoShowsDate";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.userInfoShowsDate = !config.userInfoShowsDate;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.userInfoShowsDate;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().userInfoShowsDate;
    }

    @Override
    public void execute(CommandObject command) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
