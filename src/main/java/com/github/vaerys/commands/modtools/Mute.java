package com.github.vaerys.commands.modtools;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.regex.Pattern;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class Mute extends Command {

    private final static SubCommandObject UN_MUTE = new SubCommandObject(
            new String[]{"UnMute"},
            "[@User]",
            "Allows you to UnMute a user.",
            SAILType.MOD_TOOLS
    );


    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject userCall = new SplitFirstObject(args);
        IRole mutedRole = command.guild.getMutedRole();
        UserObject muted = Utility.getUser(command, userCall.getFirstWord(), false, false);
        if (muted == null) return "> Could not find user";
        if (muted.getProfile(command.guild) == null) muted.addProfile(command.guild);
        if (mutedRole == null) return "> Muted role is not configured.";

        // Un mute subtype
        if (UN_MUTE.isSubCommand(command)) {
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

        // setup muted messages
        String msgFormat = "> **%s** was muted%s"; // name was muted for timevalue;
        String adminMsg = " by %s in %s with reason `%s`."; // > name was muted for timevalue by mod in channel with `reason`;
        String modnote = "Muted by %s. Reason: `%s`. Time: %s. Channel: %s."; //name muted with reason `reason` for timevalue in channel;
        IChannel adminChannel = command.guild.getChannelByType(ChannelSetting.ADMIN);


        if (reason.toString().isEmpty()) reason.setContent("No reason given");
        // final responses:
        String response = String.format(msgFormat, muted.displayName, timeValue);

        if (adminChannel != null) {
            RequestHandler.sendMessage(response + String.format(adminMsg, command.user.displayName,
                    command.channel.mention, reason), adminChannel);
        }

        muted.getProfile(command.guild).addSailModNote(String.format(modnote, command.user.displayName, reason, timeValue, command.channel.mention), command, isStrike);
        return response + ".";
    }

    @Override
    public String description(CommandObject command) {
        return "Mutes a user and adds a modnote to the user. if a ⚠ emoji is added to the mute reason the note will be a strike.\n" +
                "\n" +
                "**Sub Command - " + command.guild.config.getPrefixCommand() + "UnMute [@User]**\n" +
                "**Desc:** Allows for Users with ManageMessages to remove a mute from a user.\n";
    }

    @Override
    protected String[] names() {
        return new String[]{"Mute", "UnMute"};
    }

    @Override
    protected String usage() {
        return "[@User] [+/-/add/del] (Time) (Reason)";
    }

    @Override
    protected SAILType type() {
        return SAILType.MOD_TOOLS;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
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
        subCommands.add(UN_MUTE);
    }

}
