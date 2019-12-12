package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class SetLevel extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject xpArgs = new SplitFirstObject(args);
        UserObject user = Utility.getUser(command, xpArgs.getFirstWord(), false);
        if (user == null) {
            return "\\> Could not find user.";
        }
        try {
            long level = Long.parseLong(xpArgs.getRest());
            if (level > Constants.LEVEL_CAP)
                return "\\> No... " + level + " Is way too many levels. Im not setting your level that high.";
            long xp = PixelHandler.totalXPForLevel(level);
            ProfileObject userObject = user.getProfile(command.guild);
            if (userObject == null) {
                return "\\> User does not have a profile.";
            }
            if (Utility.testUserHierarchy(user, command.user, command.guild)) {
                return "\\> You do not have permission to edit " + user.displayName + "'s pixels.";
            }
            userObject.setXp(xp);
            userObject.removeLevelFloor();
            GuildHandler.checkUsersRoles(user.longID, command.guild);
            return "\\> " + user.displayName + "'s Level is now set to: **" + level + "**";
        } catch (NumberFormatException e) {
            return "\\> Invalid number";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"SetLevel"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the level of a user.";
    }

    @Override
    protected String usage() {
        return "[@User] [Level]";
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
        return new Permissions[]{Permissions.MANAGE_ROLES, Permissions.MANAGE_MESSAGES};
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
