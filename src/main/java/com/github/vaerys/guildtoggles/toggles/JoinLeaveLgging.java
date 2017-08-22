package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class JoinLeaveLgging implements GuildToggle{

    @Override
    public String name() {
        return "JoinLeaveLogging";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.joinLeaveLogging = !config.joinLeaveLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.joinLeaveLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().joinLeaveLogging;
    }

    @Override
    public void execute(GuildObject guild) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
