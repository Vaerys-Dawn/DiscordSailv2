package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class UserInfoShowsDate extends GuildSetting {
    @Override
    public SAILType name() {
        return SAILType.USER_INFO_SHOWS_DATE;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.userInfoShowsDate = !config.userInfoShowsDate;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.userInfoShowsDate;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().userInfoShowsDate;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Show full date on globalUser profile instead of time since created.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Changes the creation date display on profiles from time since to the date the account was created.";
    }

    @Override
    public void setup() {

    }
}
