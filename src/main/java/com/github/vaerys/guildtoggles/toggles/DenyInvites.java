package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class DenyInvites extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.DENY_INVITES;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.denyInvites = !config.denyInvites;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.denyInvites;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().denyInvites;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables deletion of instant invites for non-approved members.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the deletion of instant invites that are sent in chat, will not affect users with the Manage Messages permissions. " +
                "If any trusted roles are set up this feature will not affect users with those roles.";
    }

    @Override
    public void setup() {

    }

}
