package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleServers implements GuildToggle {

    @Override
    public String name() {
        return "Servers";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleServers = !config.moduleServers;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleServers;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleServers;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeCommandsByType(Command.TYPE_SERVERS);
        guild.removeChannel(Command.CHANNEL_SERVERS);
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
