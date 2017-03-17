package Commands.Competition;

import Commands.CommandObject;
import Interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class EnterVote implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        if (command.guildConfig.compVoting) {
            return command.competition.addVote(command.authorID, args);
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
