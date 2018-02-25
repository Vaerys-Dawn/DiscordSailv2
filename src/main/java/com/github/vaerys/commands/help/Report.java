package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

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

    protected static final String[] NAMES = new String[]{"Report"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Can be used to send a user report to the server staff.";
    }

    protected static final String USAGE = "[@User] [Reason]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.HELP;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
