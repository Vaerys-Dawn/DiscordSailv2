package com.github.vaerys.commands.competition;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class EnterVote extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.config.compVoting) {
            return command.guild.competition.addVote(command.user.longID, args);
        } else {
            return "\\> Competition Voting is closed.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"Vote"};
    }

    @Override
    public String description(CommandObject command) {
        return "Saves your vote.";
    }

    @Override
    protected String usage() {
        return "[Vote...]";
    }

    @Override
    protected SAILType type() {
        return SAILType.COMPETITION;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.BOT_COMMANDS;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {
        // does nothing
    }
}
