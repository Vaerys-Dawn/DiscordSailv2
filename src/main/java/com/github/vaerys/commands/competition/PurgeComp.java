package com.github.vaerys.commands.competition;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 29/03/2017.
 */
public class PurgeComp extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        command.guild.competition.purgeEntries();
        command.guild.competition.purgeVotes();
        return "\\> Purge Complete.";
    }

    @Override
    protected String[] names() {
        return new String[]{"PurgeComp"};
    }

    @Override
    public String description(CommandObject command) {
        return "Purges the Current Comp to make way for a new one.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.COMPETITION;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[]{Permissions.ADMINISTRATOR};
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
