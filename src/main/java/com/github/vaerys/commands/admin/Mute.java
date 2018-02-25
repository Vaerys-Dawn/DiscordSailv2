package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.regex.Pattern;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class Mute extends Command {

    // using static as it will cause less memory to be used overall by orphaned data
    protected static final String[] NAMES = new String[]{"Mute", "UnMute"};
    protected static final String USAGE = "[@User] [+/-/add/del] (Time) (Reason)";
    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_MESSAGES};
    protected static final boolean REQUIRES_ARGS = true;
    protected static final boolean DO_ADMIN_LOGGING = true;

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject userCall = new SplitFirstObject(args);
        IRole mutedRole = command.guild.getMutedRole();
        UserObject muted = Utility.getUser(command, userCall.getFirstWord(), false, false);
        if (muted == null) return "> Could not find user";
        if (mutedRole == null) return "> Muted role is not configured.";

        // Un mute subtype
        if (isSubtype(command, "UnMute")) {
            if (!muted.roles.contains(mutedRole)) return "> " + muted.displayName + " is not muted.";
            command.guild.users.unMuteUser(muted.longID, command.guild.longID);
            return "> " + muted.displayName + " was UnMuted.";
        }

        if (muted.longID == command.user.longID) return "> Don't try to mute yourself you numpty.";
        if (!Utility.testUserHierarchy(command.client.bot.get(), mutedRole, command.guild.get()))
            return "> Cannot Mute " + muted.displayName + ". **" + mutedRole.getName() + "** role has a higher hierarchy than me.";
        if (!Utility.testUserHierarchy(command.user.get(), muted.get(), command.guild.get()))
            return "> Cannot Mute/UnMute " + muted.displayName + ". User hierarchy higher than yours.";
        StringHandler reason = new StringHandler(userCall.getRest());
        long timeSecs = Utility.getRepeatTimeValue(reason);
        boolean isStrike = false;
//        if (reason.toString().isEmpty()) return "> Reason Cannot be empty";

        //mute the offender
        command.guild.users.muteUser(muted.longID, timeSecs, command.guild.longID);

        //build the response
        //time value
        String timeValue = "";

        if (timeSecs > 0) {
            timeValue = " for " + Utility.formatTime(timeSecs, true);
        }

        if (Pattern.compile("(^⚠ | ⚠|⚠)").matcher(reason.toString()).find()) {
            reason.replaceRegex("(^⚠ | ⚠|⚠)", "");
            isStrike = true;
        }

        //builds prefix
        String content = "> **" + muted.displayName + "** was Muted" + timeValue;
        IChannel adminChannel = command.guild.getChannelByType(ChannelSetting.ADMIN);
        //builds reason
        if (reason.toString().isEmpty()) reason.setContent("No reason given");
        //builds final response
        String response = content + " by **" + command.user.displayName + "** in channel " + command.channel.mention + " with reason `" + reason + "`.";
        //sends response to admin channel
        if (adminChannel != null) {
            RequestHandler.sendMessage(response, adminChannel);
        }
        //adds note to modnotes
        muted.getProfile(command.guild).addSailModNote(response, command, isStrike);
        return content + ".";
    }

    @Override
    public String description(CommandObject command) {
        return "Mutes a user and adds a modnote to the user. if a ⚠ emoji is added to the mute reason the note will be a strike.\n" +
                "\n" +
                "**Sub Command - " + command.guild.config.getPrefixCommand() + "UnMute [@User]**\n" +
                "**Desc:** Allows for Users with ManageMessages to remove a mute from a user.\n";
    }

    @Override
    public void init() {

    }

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

}
