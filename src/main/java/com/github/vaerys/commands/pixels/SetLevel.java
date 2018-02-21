package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
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
            return "> Could not find user.";
        }
        try {
            long level = Long.parseLong(xpArgs.getRest());
            if (level > Constants.LEVEL_CAP) return "> No... " + level + " Is way too many levels. Im not setting your level that high.";
            long xp = XpHandler.totalXPForLevel(level);
            ProfileObject userObject = user.getProfile(command.guild);
            if (userObject == null) {
                return "> User does not have a profile.";
            }
            if (Utility.testUserHierarchy(user, command.user, command.guild)) {
                return "> You do not have permission to edit " + user.displayName + "'s pixels.";
            }
            userObject.setXp(xp);
            userObject.removeLevelFloor();
            XpHandler.checkUsersRoles(user.longID, command.guild);
            return "> " + user.displayName + "'s Level is now set to: **" + level + "**";
        } catch (NumberFormatException e) {
            return "> Invalid number";
        }
    }

    protected static final String[] NAMES = new String[]{"SetLevel"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the level of a user.";
    }

    protected static final String USAGE = "[@User] [Level]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.PIXEL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_ROLES, Permissions.MANAGE_MESSAGES};
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
