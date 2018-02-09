package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 06/07/2017.
 */

/**
 * @deprecated
 * Implementation of EditXP makes this redundant.
 * Please use EditXP [user] "=" [amount] instead.
 */
@Deprecated
public class SetXp extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject xpArgs = new SplitFirstObject(args);
        UserObject user = Utility.getUser(command, xpArgs.getFirstWord(), false);
        if (user == null) {
            return "> Could not find user.";
        }
        try {
            long xp = Long.parseLong(xpArgs.getRest());
            ProfileObject userObject = user.getProfile(command.guild);
            if (userObject != null) {
                userObject.setXp(xp);
                userObject.removeLevelFloor();
                XpHandler.checkUsersRoles(user.longID, command.guild);
                return "> " + user.displayName + "'s Pixels is now set to: **" + xp + "**";
            } else {
                return "> User does not have a profile.";
            }
        } catch (NumberFormatException e) {
            return "> Invalid number";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetPixels", "SetXP"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the xp of a user.";
    }

    @Override
    public String usage() {
        return "[@User] [Pixels]";
    }

    @Override
    public String type() {
        return TYPE_PIXEL;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
    public void init() {

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
