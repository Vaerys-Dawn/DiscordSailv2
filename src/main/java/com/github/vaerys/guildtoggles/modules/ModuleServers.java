package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.GuildModule;
import com.github.vaerys.enums.SAILType;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleServers extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.SERVERS;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleServers = !config.moduleServers;
    }

    @Override
    public boolean enabled(GuildConfig config) {
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
    public String stats(CommandObject command) {
        return "**Total Server Listings:** " + command.guild.servers.getServers().size();
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Allows users to create custom game server listings.";
    }
}
