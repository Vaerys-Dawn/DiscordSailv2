package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;


/**
 * Created by AndrielChaoti 22-Aug-17
 */
public class EditXp extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        // Split args into String[]
        String[] splitArgs = args.split(" ");

        // Check if user passed enough args
        if (splitArgs.length != 3) return missingArgs(command);

        // Get user specified in command
        UserObject user = Utility.getUser(command, splitArgs[0], false);
        if (user == null) return "> Could not find user.";
        if (Utility.testUserHierarchy(user, command.user, command.guild)) {
            return "> You do not have permission to edit " + user.displayName + "'s pixels.";
        }

        // Parse passed XP into a number
        long xp;
        try {
            xp = Long.parseLong(splitArgs[2]);
            if (xp < 0) return "> I don't know what negative pixels are. What are you trying to do?";
            if (xp > Constants.PIXELS_CAP) return "> I can't give you that many pixels!";
        } catch (NumberFormatException e) {
            return "> **" + splitArgs[2] + "** is not a number.";
        }

        // Get userObject from passed user.
        ProfileObject profile = user.getProfile(command.guild);
        if (profile == null) return "> " + user.displayName + " doesn't have a profile yet.";

        // Parse and execute modifiers
        String out;
        boolean xpChanged = false;

        switch (splitArgs[1]) {
            // Add Pixels:
            case "+":
            case "add":
                profile.setXp(profile.getXP() + xp);
                xpChanged = true;
                out = "> Added **" + xp + "** pixels to **" + user.displayName + "**.";
                break;
            // Remove Pixels
            case "-":
            case "rem":
            case "sub":
                profile.setXp(profile.getXP() - xp);
                xpChanged = true;
                out = "> Removed **" + xp + "** pixels from **" + user.displayName + "**.";
                // Special handling if XP is set below 0
                if (profile.getXP() < 0) {
                    profile.setXp(0);
                    out = "> **" + user.displayName + "** did not have enough pixels. I just set them to **0**";
                }
                break;
            // Set Pixels
            case "=":
            case "set":
                profile.setXp(xp);
                xpChanged = true;
                out = "> Set Pixels to **" + xp + "** for user **" + user.displayName + "**.";
                break;
            // Failure case
            default:
                out = "> Invalid modifier. Valid modifiers are **[+/-/=]** or **add/sub/set**";
                break;
        }

        if (xpChanged) {
            profile.removeLevelFloor();
            GuildHandler.checkUsersRoles(user.longID, command.guild);
        }
        return out;
    }

    @Override
    protected String[] names() {
        return new String[]{"EditXp", "EditPixels"};
    }

    @Override
    public String description(CommandObject command) {
        String modifiers = "\n**Modifiers**:\n" +
                "> +/add - `Add [Pixels] pixels.`\n" +
                "> -/sub - `Remove [Pixels] pixels.`\n" +
                "> =/set - `Set pixels to [Pixels].`\n";
        return "Allows you to add or remove pixels from a user." + modifiers;
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
    protected String usage() {
        return "[@User] [modifier] [Pixels]";
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    public void init() {

    }
}
