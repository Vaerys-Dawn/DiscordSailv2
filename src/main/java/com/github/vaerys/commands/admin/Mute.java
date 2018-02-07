package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class Mute implements Command {

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
//        if (reason.toString().isEmpty()) return "> Reason Cannot be empty";

        //mute the offender
        command.guild.users.muteUser(muted.longID, timeSecs, command.guild.longID);

        //build the response
        //time value
        String timeValue = "";

        if (timeSecs > 0) {
            timeValue = " for " + Utility.formatTime(timeSecs, true);
        }

        //builds prefix
        String content = "> **" + muted.displayName + "** was Muted" + timeValue;
        IChannel adminChannel = command.guild.getChannelByType(CHANNEL_ADMIN);
        //builds reason
        if (reason.toString().isEmpty()) reason.setContent("No reason given");
        //builds final response
        String response = content + " by **" + command.user.displayName + "** in channel " + command.channel.mention + " with reason `" + reason + "`.";
        //sends response to admin channel
        if (adminChannel != null) {
            RequestHandler.sendMessage(response, adminChannel);
        }
        //adds note to modnotes
        muted.getProfile(command.guild).addSailModNote(response, command);
        return content + ".";
    }

    @Override
    public String[] names() {
        return new String[]{"Mute", "UnMute"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for Users with ManageMessages to mute a user.\n" +
                "\n" +
                "**Sub Command - " + command.guild.config.getPrefixCommand() + "UnMute [@User]**\n" +
                "**Desc:** Allows for Users with ManageMessages to remove a mute from a user.\n";
    }

    @Override
    public String usage() {
        return "[@User] [+/-/add/del] (Time) (Reason)";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
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
