package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class CheckNewUsers extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.CHECK_NEW_USERS;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.checkNewUsers = !config.checkNewUsers;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.checkNewUsers;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().checkNewUsers;
    }

    @Override
    public String desc(CommandObject command) {
        return "Checks to see if a user's account is less than 5 hours old and sends a message to the admin logs if set up or the general messageChannel if it is.";
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Simple Fresh account alert tool.";
    }

    @Override
    public void setup() {

    }
}