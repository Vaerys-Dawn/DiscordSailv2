package com.github.vaerys.commands.competition;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class EnterVote implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (command.guild.config.compVoting) {
            return command.guild.competition.addVote(command.user.longID, args);
        } else {
            return "> Competition Voting is closed.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Vote"};
    }

    @Override
    public String description() {
        return "Saves your vote.";
    }

    @Override
    public String usage() {
        return "[Vote...]";
    }

    @Override
    public String type() {
        return TYPE_COMPETITION;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return true;
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
