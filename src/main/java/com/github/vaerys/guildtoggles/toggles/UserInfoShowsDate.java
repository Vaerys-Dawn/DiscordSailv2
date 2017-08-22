package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

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
    public void execute(GuildObject guild) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
