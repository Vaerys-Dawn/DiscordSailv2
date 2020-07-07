package com.github.vaerys.commands.competition;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
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
        builder.append("\\> tally being performed.\n");
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

    @Override
    protected String[] names() {
        return new String[]{"FinalTally"};
    }

    @Override
    public String description(CommandObject command) {
        return "Posts the final scores.";
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
        return new Permission[]{Permissions.MANAGE_MESSAGES, Permissions.MANAGE_ROLES};
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
