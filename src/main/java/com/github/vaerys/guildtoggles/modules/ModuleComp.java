package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.guildtoggles.toggles.CompEntries;
import com.github.vaerys.guildtoggles.toggles.Voting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;
import com.github.vaerys.templates.SAILType;
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
    public boolean get(GuildConfig config) {
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
    public String stats(CommandObject object) {
        if (!Utility.testForPerms(object, Permissions.MANAGE_SERVER)) return null;
        StringBuilder builder = new StringBuilder();
        builder.append("**Total Competition Entries:** " + object.guild.competition.getEntries().size());
        builder.append("\n**Total Voters:** " + object.guild.competition.getVoters().size());
        return builder.toString();
    }
}
