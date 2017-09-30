package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.guildtoggles.toggles.CompEntries;
import com.github.vaerys.guildtoggles.toggles.Voting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildModule;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ModuleComp implements GuildModule {

    boolean state = false;

    @Override
    public String name() {
        return "Comp";
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
    public void execute(GuildObject guild) {
        guild.removeCommandsByType(Command.TYPE_COMPETITION);
        guild.removeToggle(new Voting().name());
        guild.removeToggle(new CompEntries().name());
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
