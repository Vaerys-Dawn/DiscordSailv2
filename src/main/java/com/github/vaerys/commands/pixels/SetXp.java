package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 06/07/2017.
 */

/**
 * @deprecated Implementation of EditXP makes this redundant.
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
                GuildHandler.checkUsersRoles(user.longID, command.guild);
                return "> " + user.displayName + "'s Pixels is now set to: **" + xp + "**";
            } else {
                return "> User does not have a profile.";
            }
        } catch (NumberFormatException e) {
            return "> Invalid number";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"SetPixels", "SetXP"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the xp of a user.";
    }

    @Override
    protected String usage() {
        return "[@User] [Pixels]";
    }

    @Override
    protected SAILType type() {
        return SAILType.PIXEL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
