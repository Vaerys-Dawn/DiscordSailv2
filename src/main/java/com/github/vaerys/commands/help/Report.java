package com.github.vaerys.commands.help;

import java.util.List;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Report extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        return report(args, command, false);
    }

    public static String report(String args, CommandObject command, boolean isSilent) {
        List<IChannel> channels = command.guild.getChannelsByType(ChannelSetting.ADMIN);
        if (channels.size() != 0) {
            IChannel channel = channels.get(0);
            SplitFirstObject split = new SplitFirstObject(args);
            UserObject reported = Utility.getUser(command, split.getFirstWord(), false,false);
            if (reported == null) {
                return "> Cannot send report. Could not find user.";
            }
            if (reported.longID == command.user.longID) {
                return "> You can't report yourself.";
            }
            if (channel != null) {
                StringBuilder builder = new StringBuilder();
                IRole roleToMention = command.guild.getRoleByID(command.guild.config.getRoleToMentionID());
                if (roleToMention != null) {
                    builder.append(roleToMention.mention() + "\n");
                }
                if (isSilent) {
                    builder.append("**User Report - Silent**\n");
                } else {
                    builder.append("**User Report**\n");
                }
                split.editRestReplace(split.getRest(), Utility.convertMentionToText(split.getRest()));
                String reason = split.getRest();
                if (split.getRest() == null) {
                    reason = "No reason given.";
                }
                builder.append("Reporter: " + command.user.get().mention() + "\nReported: " + reported.get().mention() + "\nReason: `" + reason + "`");
                builder.append("\n" + command.channel.get().mention());
                IMessage message = RequestHandler.sendMessage(builder.toString(), channel).get();
                if (message == null) {
                    return "> User report was not be sent. Looks like I can't send messages to " + channel.mention() + ".";
                } else {
                    return "> User Report sent.";
                }
            }
            return "> Your report could not be sent as the server does not have an admin channel set up at this time.";
        } else {
            return "> Your report could not be sent as the server does not have an admin channel set up at this time.";
        }
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
    protected Permissions[] perms() {
        return new Permissions[0];
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
