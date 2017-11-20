package com.github.vaerys.commands.competition;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 29/03/2017.
 */
public class PurgeComp implements Command{

    @Override
    public String execute(String args, CommandObject command) {
        command.guild.competition.purgeEntries();
        command.guild.competition.purgeVotes();
        return "> Purge Complete.";
    }

    @Override
    public String[] names() {
        return new String[]{"PurgeComp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Purges the Current Comp to make way for a new one.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_COMPETITION;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.ADMINISTRATOR};
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
