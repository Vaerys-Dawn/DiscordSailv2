package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleServers extends GuildModule {

    @Override
    public String name() {
        return Command.TYPE_SERVERS;
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
    public String desc(CommandObject command) {
        return "This module allows the creation of server listings allowing users to advertise their servers.";
    }

    @Override
    public void setup() {
        channels.add(ChannelSetting.SERVERS);
    }

    @Override
    public String stats(CommandObject object) {
        return "**Total Server Listings:** " + object.guild.servers.getServers().size();
    }
}
