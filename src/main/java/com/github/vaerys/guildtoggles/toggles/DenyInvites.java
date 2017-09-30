package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class DenyInvites implements GuildSetting {

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
    public void execute(GuildObject guild) {

    }

}
