package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.toggles.CompEntries;
import com.github.vaerys.guildtoggles.toggles.Voting;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleComp extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.COMPETITION;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleComp = !config.moduleComp;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.moduleComp;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleComp;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module allows server moderators to set up competitions for their server.";
    }

    @Override
    public void setup() {
        settings.add(new Voting());
        settings.add(new CompEntries());
    }

    @Override
    public String stats(CommandObject command) {
        if (!GuildHandler.testForPerms(command, Permissions.MANAGE_SERVER)) return null;
        StringBuilder builder = new StringBuilder();
        builder.append("**Total Competition Entries:** " + command.guild.competition.getEntries().size());
        builder.append("\n**Total Voters:** " + command.guild.competition.getVoters().size());
        return builder.toString();
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }
}
