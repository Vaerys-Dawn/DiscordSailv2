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
    public String execute(String args, CommandObject command){
        // Split args into String[]
        String[] splitArgs = args.split(" ");

        if (splitArgs.length != 3) {
            return missingArgs(command);
        } else {
            UserObject user = Utility.getUser(command, splitArgs[0], false);
            // Check for a valid user
            if (user != null) {
                try {
                    long xp = Long.parseLong(splitArgs[2]);

                    if (xp < 0) return "> I don't know what negative Pixels are. What are you trying to do?";

                    ProfileObject userObject = user.getProfile(command.guild);

                    // check if the user's profile exists.
                    if (userObject != null) {

                        //handle xp changes
                        String out;
                        Boolean xpChanged = false;

                        switch (splitArgs[1]) {
                            case "+":
                            case "add":
                                // Add Pixels
                                userObject.setXp(userObject.getXP() + xp);
                                out = "> Added **" + xp + "** pixels to **" + user.displayName + "**.";
                                xpChanged = true;
                                break;
                            case "-":
                            case "rem":
                            case "sub":
                                // Subtract Pixels
                                userObject.setXp(userObject.getXP() - xp);
                                // check for 0 underflow...
                                if (userObject.getXP() < 0){
                                    userObject.setXp(0);
                                    out = "> **"+ user.displayName + "** didn't have enough pixels, so I set them to **0**.";
                                } else {
                                    out = "> Subtracted **" + xp + "** pixels from **" + user.displayName + "**.";
                                }
                                xpChanged = true;
                                break;
                            case "=":
                            case "set":
                                // Set Pixels
                                userObject.setXp(xp);
                                xpChanged = true;
                                out = "> Set Pixels to **" + xp + "** for user **" + user.displayName + "**.";
                                break;
                            default:
                                out = "> Invalid modifier. Valid modifiers are **[+/-/=]** or **add/sub/set**";
                                break;
                        }
                        if (xpChanged) {
                            userObject.removeLevelFloor();
                            XpHandler.checkUsersRoles(user.stringID, command.guild);
                        }
                        return out;
                    } else return "> User does not have a profile";
                } catch (NumberFormatException e) {
                    return "> Invalid number";
                }
            } else return "> Could not find user";
        }
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
