package com.github.vaerys.commands.competition;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.HashMap;
import java.util.Map;

public class WeightedFinalTally implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        Map<Long, Long> entryVotes = new HashMap<>();
        long totalEntries = 0;
        long totalVotes = 0;
        StringBuilder builder = new StringBuilder();
        for (long i = 0; i < command.guild.competition.getEntries().size(); i++) {
            entryVotes.put(i + 1, 0L);
        }
        for (String s : command.guild.competition.getVotes()) {
            String[] splitVotes = s.split(",");
            if (splitVotes.length != 1 && splitVotes.length != 0) {
                for (long i = 1; i < splitVotes.length; i++) {
                    try {
                        long vote = Long.parseLong(splitVotes[(int) i]);
                        if (entryVotes.containsKey(vote)) {
                            IUser user = command.guild.getUserByID(Utility.stringLong(splitVotes[0]));
                            if (user != null) {
                                int weight = XpHandler.getRewardCount(command.guild, user.getLongID());
                                entryVotes.put(vote, entryVotes.get(vote).longValue() + weight + 1);
                                totalVotes += weight + 1;
                            }
                        }
                    } catch (NumberFormatException e) {
                        //Ignore
                    }
                    totalEntries++;
                }
            }
        }
        builder.append("**Total Weighted Votes\n**");
        entryVotes.forEach((k, v) -> builder.append("> Entry " + k + ": " + v + " Votes.\n"));
        builder.append("Total votes: " + totalVotes + ".\n");
        builder.append("Total Entries: " + totalEntries + ".");
        return builder.toString();
    }

    @Override
    public String[] names() {
        return new String[]{"WeightedFinalTally", "WFinalTally"};
    }

    @Override
    public String description(CommandObject command) {
        return "Returns a tally based on the reward roles of the users.";
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
        return new Permissions[]{Permissions.MANAGE_SERVER};
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