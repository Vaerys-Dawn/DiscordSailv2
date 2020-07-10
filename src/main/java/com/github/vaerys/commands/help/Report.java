package com.github.vaerys.commands.help;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.botlevel.AutoBlocker;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Report extends Command {

    private static List<AutoBlocker> lastUsers = new ArrayList<>(5);

    public static String report(String args, CommandObject command, boolean isSilent) {
        TextChannel channel = command.guild.getChannelByType(ChannelSetting.ADMIN);
        if (channel == null) {
            return "\\> Your report could not be sent as the server does not have an admin channel set up at this time.";
        }

        SplitFirstObject split = new SplitFirstObject(args);
        UserObject reported = Utility.getUser(command, split.getFirstWord(), false, false);

        addUser(command);

        if (command.user.isBlockedFromDms()) {
            return "\\> " + command.user.mention() + ", You have been blocked, you can no longer send any more report requests.";
        }
        if (reported == null) {
            return "\\> Cannot send report. Could not find user.";
        }
        if (reported.longID == command.user.longID) {
            return "\\> You can't report yourself.";
        }
        if (channel == null) {
            return "\\> Your report could not be sent as the server does not have an admin channel set up at this time.";
        }

        //build embed
//        XEmbedBuilder embed = new XEmbedBuilder(new Color(250, 166, 26));
        Role roleToMention = command.guild.getRoleById(command.guild.config.getRoleToMentionID());
        split.editRestReplace(split.getRest(), Utility.convertMentionToText(split.getRest()));
        String reason = split.getRest() != null ? split.getRest() : "No reason given.";
        String format = "**%s**\nReported User: %s\nReason: `%s`\nChannel: %s\n\nLink to Report:\n%s\nReported by: %s";
        String reportType = isSilent ? "User Report - Silent" : "User Report";
        String message = String.format(format, reportType, reported.mention(), reason, command.channel.mention, command.getMessageLink(), command.user.mention());
//        embed.setTitle(reportType);
        Message response;

        //send report
        if (roleToMention == null) {
            response = channel.sendMessage(message).complete();
        } else {
            response = channel.sendMessage(roleToMention.getAsMention() + "\n" + message).complete();
        }
        //send response
        if (response == null) {
            return "\\> User report was not be sent. Looks like I can't send messages to " + channel.getAsMention() + ".";
        } else {
            return "\\> User Report sent.";
        }
    }

    private static void addUser(CommandObject command) {
        AutoBlocker blocker = null;
        for (AutoBlocker a : lastUsers) {
            if (a.getUserID() == command.user.longID) {
                a.addCount(command.message.getTimestamp());
            }
        }
        if (blocker == null) lastUsers.add(new AutoBlocker(command));
        try {
            while (lastUsers.size() > 5) {
                lastUsers.remove(0);
            }
        } catch (ConcurrentModificationException e) {
            return;
        }
    }

    @Override
    public String execute(String args, CommandObject command) {
        return report(args, command, false);
    }

    @Override
    protected String[] names() {
        return new String[]{"Report"};
    }

    @Override
    public String description(CommandObject command) {
        return "Can be used to send a user report to the server staff.";
    }

    @Override
    protected String usage() {
        return "[@User] [Reason]";
    }

    @Override
    protected SAILType type() {
        return SAILType.HELP;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
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
        return true;
    }

    @Override
    public void init() {

    }
}
