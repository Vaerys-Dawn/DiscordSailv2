package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class UserInfoShowsDate extends GuildSetting {
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
    public String desc(CommandObject command) {
        return "Changes the creation date display on profiles from time since to the date the account was created.";
    }

    @Override
    public void setup() {

    }
}
