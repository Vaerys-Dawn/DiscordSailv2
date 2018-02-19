package com.github.vaerys.commands.competition;

import java.util.HashMap;
import java.util.Map;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class WeightedFinalTally extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (!command.guild.config.modulePixels)
            return "> Weighted final tally should only be used if the pixel module is enabled, please use **"
                    + new FinalTally().getUsage(command) + "** instead.";
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

    protected static final String[] NAMES = new String[]{"WeightedFinalTally", "WFinalTally"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Returns a tally based on the reward roles of the users.";
    }

    protected static final String USAGE = null;
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.COMPETITION;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}