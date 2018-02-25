package com.github.vaerys.commands.competition;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 01/02/2017.
 */
public class FinalTally extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        StringBuilder builder = new StringBuilder();
        builder.append("> tally being performed.\n");
        ArrayList<String> votes = command.guild.competition.getVotes();
        int[] tally = new int[command.guild.competition.getEntries().size()];
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

    protected static final String[] NAMES = new String[]{"FinalTally"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Posts the final scores.";
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

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_MESSAGES,Permissions.MANAGE_ROLES};
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
