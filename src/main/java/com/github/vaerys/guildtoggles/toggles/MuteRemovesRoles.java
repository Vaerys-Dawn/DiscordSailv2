package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

public class MuteRemovesRoles extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.MUTE_REMOVES_ROLES;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.muteRemovesRoles = !config.muteRemovesRoles;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.muteRemovesRoles;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().muteRemovesRoles;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Removes extra user roles when muting.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables automatic removal/replacing of user roles when they are muted and unmuted.";
    }

    @Override
    public void setup() {

    }
}
