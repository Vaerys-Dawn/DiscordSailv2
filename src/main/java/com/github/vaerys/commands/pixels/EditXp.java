package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import sx.blah.discord.handle.obj.Permissions;


/**
 *  Created by AndrielChaoti 22-Aug-17
 */
public class EditXp implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        // Split args into String[]
        String[] splitArgs = args.split(" ");

        // Check if user passed enough args
        if (splitArgs.length != 3) return missingArgs(command);

        // Get user specified in command
        UserObject user = Utility.getUser(command, splitArgs[0], false);
        if (user == null) return "> Could not find user.";

        // Parse passed XP into a number
        long xp;
        try {
            xp = Long.parseLong(splitArgs[2]);

            if (xp < 0) return "> I don't know what negative pixels are. What are you trying to do?";

        } catch (NumberFormatException e) {
            return "> **" + splitArgs[2] + "** is not a number.";
        }

        // Get userObject from passed user.
        ProfileObject userObject = user.getProfile(command.guild);
        if (userObject == null) return "> " + user.displayName + " does not have a profile.";

        // Parse and execute modifiers
        String out;
        boolean xpChanged = false;

        switch (splitArgs[1]) {
            // Add Pixels:
            case "+":
            case "add":
                userObject.setXp(userObject.getXP() + xp);
                xpChanged = true;

                out = "> Added **" + xp + "** pixels to **" + user.displayName + "**.";
                break;

            // Remove Pixels
            case "-":
            case "rem":
            case "sub":
                userObject.setXp(userObject.getXP() - xp);
                xpChanged = true;

                out = "> Removed **" + xp + "** pixels from **" + user.displayName + "**.";

                // Special handling if XP is set below 0
                if (userObject.getXP() < 0) {
                    userObject.setXp(0);
                    out = "> **" + user.displayName + "** did not have enough pixels. I just set them to **0**";
                }

                break;

            // Set Pixels
            case "=":
            case "set":
                userObject.setXp(xp);
                xpChanged = true;

                out = "> Set Pixels to **" + xp + "** for user **" + user.displayName + "**.";
                break;

            // Failure case
            default:
                out = "> Invalid modifier. Valid modifiers are **[+/-/=]** or **add/sub/set**";
                break;
        }

        if (xpChanged) {
            userObject.removeLevelFloor();
            XpHandler.checkUsersRoles(user.stringID, command.guild);
        }
        return out;
    }

    // Define Command parameters.
    @Override
    public String[] names(){return new String[]{"EditXp", "EditPixels"};}

    @Override
    public String description(){
        String modifiers = "\n**Modifiers**:\n" +
                "> +/add - `Add [Pixels] pixels.`\n" +
                "> -/sub - `Remove [Pixels] pixels.`\n" +
                "> =/set - `Set pixels to [Pixels].`\n";
        return "Allows you to add or remove pixels from a user." + modifiers;}

    @Override
    public String type(){return TYPE_PIXEL;}

    @Override
    public Permissions[] perms(){return new Permissions[]{Permissions.MANAGE_SERVER};}

    @Override
    public String usage() {return "[@User] [modifier] [Pixels]";}

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public boolean requiresArgs() {
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

    @Override
    public String channel() {
        return null;
    }
}
