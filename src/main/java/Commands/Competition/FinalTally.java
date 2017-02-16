package Commands.Competition;

import Commands.Command;
import Commands.CommandObject;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class FinalTally implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        StringBuilder builder = new StringBuilder();
        builder.append("> tally being performed.\n");
        ArrayList<String> votes = command.competition.getVotes();
        int[] tally = new int[command.competition.getEntries().size()];
        for (int i = 0; i < tally.length; i++) {
            tally[i] = 0;
        }
        int userVoteClusters = 0;
        int totalVotes = 0;
        for (String s : votes) {
            userVoteClusters++;
            String[] splitSting = s.split(",");
            for (int i = 1; i < splitSting.length; i++) {
                int position = Integer.parseInt(splitSting[i]);
                tally[position - 1]++;
                totalVotes++;
            }
        }
        builder.append("total of users that voted: " + userVoteClusters + "\n");
        builder.append("total number of votes: " + totalVotes + "\n");
        int entry = 0;
        for (int i : tally) {
            entry++;
            builder.append("Entry " + entry + ": " + i + "\n");
        }
        return builder.toString();
    }

    @Override
    public String[] names() {
        return new String[]{"FinalTally"};
    }

    @Override
    public String description() {
        return "Posts the final scores.";
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
        return new Permissions[]{Permissions.MANAGE_MESSAGES,Permissions.MANAGE_ROLES};
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
