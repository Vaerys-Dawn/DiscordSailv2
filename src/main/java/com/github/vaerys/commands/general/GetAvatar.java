package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class GetAvatar extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = Utility.getUser(command, args, true, false);
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

    @Override
    protected String[] names() {
        return new String[]{"GetAvatar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gets the Mentionee's Profile Image.";
    }

    @Override
    protected String usage() {
        return "[@User]";
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
