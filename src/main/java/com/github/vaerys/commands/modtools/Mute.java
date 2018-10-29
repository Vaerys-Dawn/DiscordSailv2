package com.github.vaerys.commands.modtools;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.objects.utils.SubCommandObject;
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
        // init vars
        SplitFirstObject userCall = new SplitFirstObject(args);
        IRole mutedRole = command.guild.getMutedRole();
        UserObject mutedUser = Utility.getUser(command, userCall.getFirstWord(), false, false);
        StringHandler response = new StringHandler("> %s %s.");
        StringHandler responseAdmin = new StringHandler("> %s %s by %s in %s with reason `%s`.");
        StringHandler modNote = new StringHandler("> %s by %s. Reason: `%s`. Time: %s. Channel: %s.");
        boolean isMute = !UN_MUTE.isSubCommand(command);
        String mode = isMute ? "**Muted**" : "**UnMuted**";
        StringHandler reason = new StringHandler(userCall.getRest());
        long timeSecs = Utility.getRepeatTimeValue(reason);
        if (reason.isEmpty()) reason.setContent("No reason given");
        StringHandler timeValue = new StringHandler("was %s", mode);
        String formattedTime = "Permanent";
        if (timeSecs >= 0) {
            formattedTime = Utility.formatTime(timeSecs, true);
            timeValue.setContent(isMute ? "was %s for %s" : "will be %s in %s");
            timeValue.format(mode, formattedTime);
        }
        IChannel adminChannel = command.guild.getChannelByType(ChannelSetting.ADMIN);

        // check for user and muted role
        if (mutedUser == null || mutedUser.get() == null) return "> Could not find user";
        if (mutedUser.getProfile(command.guild) == null) mutedUser.addProfile(command.guild);
        if (mutedRole == null) return "> Muted role is not configured.";

        // check hierarchy
        if (mutedUser.longID == command.user.longID && isMute) return "> Don't try to mute yourself you numpty.";
        if (!Utility.testUserHierarchy(command.client.bot.get(), mutedRole, command.guild.get()))
            return String.format("> Cannot %s %s. The **%s** role has a higher hierarchy than me.", mode, mutedUser.displayName, mutedRole.getName());
        if (!Utility.testUserHierarchy(command.user.get(), mutedUser.get(), command.guild.get(), false))
            return String.format("> Cannot %s %s. User hierarchy higher than yours.", mode, mutedUser.displayName);

        if (!isMute && !mutedUser.roles.contains(mutedRole) && !command.guild.users.isUserMuted(mutedUser.get())) {
            return String.format("> %s is not muted.", mutedUser.displayName);
        }

        // mute/un-mute user
        if (!isMute && timeSecs == -1) {
            command.guild.users.unMuteUser(mutedUser, command.guild);
        } else {
            command.guild.users.muteUser(mutedUser, command.guild, timeSecs);
        }

        // add mod note
        if (isMute) {
            boolean isStrike = false;
            if (Pattern.compile("(^⚠ | ⚠|⚠)").matcher(reason.toString()).find()) {
                reason.replaceRegex("(^⚠ | ⚠|⚠)", "");
                isStrike = true;
            }
            modNote.format(mode, command.user.displayName, reason, formattedTime, command.channel.mention);
            mutedUser.getProfile(command).addSailModNote(modNote.toString(), command, isStrike);
        }

        // send admin report
        if (adminChannel != null) {
            responseAdmin.format(mutedUser.mention(), timeValue, command.user.displayName, command.channel.mention, reason);
            RequestHandler.sendMessage(responseAdmin.toString(), adminChannel);
        }

        // send final response
        response.format(mutedUser.mention(), timeValue);
        return response.toString();
    }

    @Override
    public String description(CommandObject command) {
        return "Mutes a user and adds a modnote to the user. if a ⚠ emoji is added to the mute reason the note will be a strike.";
    }

    @Override
    protected String[] names() {
        return new String[]{"Mute", "UnMute"};
    }

    @Override
    protected String usage() {
        return "[@User] (Time) (Reason)";
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
        showIndividualSubs = true;
    }

}
