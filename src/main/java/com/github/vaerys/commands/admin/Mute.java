package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class Mute implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject userCall = new SplitFirstObject(args);
        IRole mutedRole = command.client.get().getRoleByID(command.guild.config.getMutedRoleID());
        SplitFirstObject modifier = new SplitFirstObject(userCall.getRest());
        UserObject muted = Utility.getUser(command, userCall.getFirstWord(), false);
        if (muted == null) {
            return "> Could not find user.";
        }
        if (mutedRole == null) {
            return "> Cannot Mute/UnMute " + muted.displayName + ". No mute role exists.";
        }
        if (userCall.getRest() == null) {
            return "> Cannot Mute/UnMute " + muted.displayName + ". No modifier specified.";
        }
        if (!Utility.testUserHierarchy(command.client.bot, mutedRole, command.guild.get())) {
            return "> Cannot Mute/UnMute " + muted.displayName + ". **" + mutedRole.getName() + "** role has a higher hierarchy than me.";
        }
        if (muted.longID == command.user.longID) {
            return "> Don't try to mute yourself you numpty.";
        }
        if (Utility.testModifier(modifier.getFirstWord()) == null) {
            return "> Cannot Mute/UnMute " + muted.displayName + ". Modifier Invalid. Must be either +/-/add/del";
        }
        if (!Utility.testUserHierarchy(command.user.get(), muted.get(), command.guild.get())) {
            return "> Cannot Mute/UnMute " + muted.displayName + ". User hierarchy higher than yours.";
        }
        if (!Utility.testUserHierarchy(command.client.bot, muted.get(), command.guild.get())) {
            return "> Cannot Mute/UnMute " + muted.displayName + ". My hierarchy level is too low.";
        }
        if (Utility.testModifier(modifier.getFirstWord())) {
            SplitFirstObject time = new SplitFirstObject("");
            long timeSecs = -1;
            if (modifier.getRest() != null) {
                time = new SplitFirstObject(modifier.getRest());
                timeSecs = Utility.textToSeconds(time.getFirstWord());
            }
            command.guild.users.muteUser(muted.longID, timeSecs, command.guild.longID);
            if (time.getFirstWord() == null || timeSecs == -1) {
                return "> " + muted.displayName + " was Muted.";
            } else {
                return "> " + muted.displayName + " was Muted for " + time.getFirstWord() + ".";
            }
        } else {
            command.guild.users.unMuteUser(muted.longID, command.guild.longID);
            return "> " + muted.displayName + " was UnMuted.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Mute"};
    }

    @Override
    public String description() {
        return "Allows for Users with ManageMessages to mute a user.";
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
