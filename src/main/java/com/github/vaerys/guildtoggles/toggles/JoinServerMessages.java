package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.admin.SetJoinMessage;
import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 07/07/2017.
 */
public class JoinServerMessages implements GuildSetting {
    @Override
    public String name() {
        return "JoinServerMessages";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.joinsServerMessages = !config.joinsServerMessages;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.joinsServerMessages;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().joinsServerMessages;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeCommand(new SetJoinMessage().names());
    }
}
