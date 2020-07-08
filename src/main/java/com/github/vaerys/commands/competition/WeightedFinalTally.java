package com.github.vaerys.commands.competition;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.HashMap;
import java.util.Map;

public class WeightedFinalTally extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (!command.guild.config.modulePixels)
            return "\\> Weighted final tally should only be used if the pixel module is enabled, please use **"
                    + get(FinalTally.class).getUsage(command) + "** instead.";
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
                                int weight = PixelHandler.getRewardCount(command.guild, user.getIdLong());
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
        entryVotes.forEach((k, v) -> builder.append("\\> Entry " + k + ": " + v + " Votes.\n"));
        builder.append("Total votes: " + totalVotes + ".\n");
        builder.append("Total Entries: " + totalEntries + ".");
        return builder.toString();
    }

    @Override
    protected String[] names() {
        return new String[]{"WeightedFinalTally", "WFinalTally"};
    }

    @Override
    public String description(CommandObject command) {
        return "Returns a tally based on the reward roles of the users.";
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
        return new Permission[]{Permissions.MANAGE_SERVER};
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
