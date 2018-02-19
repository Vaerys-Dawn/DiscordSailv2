package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class GetAvatar extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = Utility.getUser(command, args, true,false);
        if (user != null) {
            String message = user.displayName + ":\n" + user.get().getAvatarURL();
            if (user.isPrivateProfile(command.guild) && user.longID != command.user.longID) {
                return "> User has set their profile to private.";
            } else if (user.isPrivateProfile(command.guild) && user.longID == command.user.longID) {
                RequestHandler.sendMessage(message, user.get().getOrCreatePMChannel());
                return "> Avatar sent to your direct messages.";
            }
            return message;
        } else {
            return "> Could not find user.";
        }
    }

    protected static final String[] NAMES = new String[]{"GetAvatar"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gets the Mentionee's Profile Image.";
    }

    protected static final String USAGE = "[@User]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.GENERAL;
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

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
