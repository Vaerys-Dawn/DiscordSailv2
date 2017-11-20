package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class DenyInvites extends GuildSetting {

    @Override
    public String name() {
        return "DenyInvites";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.denyInvites = !config.denyInvites;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.denyInvites;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().denyInvites;
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
