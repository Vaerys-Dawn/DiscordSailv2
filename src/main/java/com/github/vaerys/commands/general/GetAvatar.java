package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class GetAvatar implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = Utility.getUser(command, args, true);
        if (user != null) {
            String message = user.displayName + ":\n" + user.get().getAvatarURL();
            if (user.isPrivateProfile(command.guild) && user.longID != command.user.longID) {
                return "> User has set their profile to private.";
            } else if (user.isPrivateProfile(command.guild) && user.longID == command.user.longID) {
                Utility.sendMessage(message, user.get().getOrCreatePMChannel());
                return "> Avatar sent to your direct messages.";
            }
            return message;
        } else {
            return "> Could not find user.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"GetAvatar"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gets the Mentionee's Profile Image.";
    }

    @Override
    public String usage() {
        return "[@User]";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
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
