package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;


/**
 * Created by AndrielChaoti 22-Aug-17
 */
public class EditXp extends Command {

    private static final SubCommandObject SET_XP = new SubCommandObject(
            new String[]{"SetPixels", "SetXp"},
            "[@User] [Amount]",
            "Set's the user's pixels to the amount specified.",
            SAILType.PIXEL
    );
    private static final SubCommandObject DEL_XP = new SubCommandObject(
            new String[]{"RemovePixels", "SubPixels", "RemoveXp", "SubXp"},
            "[@User] [Amount]",
            "Removes the specified amount of pixels from the user's profile.",
            SAILType.PIXEL
    );
    private static final SubCommandObject ADD_XP = new SubCommandObject(
            new String[]{"AddPixels", "AddXp"},
            "[@User] [Amount]",
            "Adds the specified amount of pixels to the user's profile.",
            SAILType.PIXEL
    );


    @Override
    public String execute(String args, CommandObject command) {

        String[] splitArgs = args.split(" ");

        boolean xpChanged = false;

        UserObject user = Utility.getUser(command, splitArgs[0], false);
        if (user == null) return "> Could not find user.";
        if (Utility.testUserHierarchy(user, command.user, command.guild)) {
            return "> You do not have permission to edit " + user.displayName + "'s pixels.";
        }

        long pixelAmount;
        try {
            if (isSubType(command)) {
                if (splitArgs.length < 2) return getMissingArgsSubCommand(command);
                pixelAmount = Long.parseLong(splitArgs[1]);
            } else {
                if (splitArgs.length < 3) return missingArgs(command);
                pixelAmount = Long.parseLong(splitArgs[2]);
            }
        } catch (NumberFormatException e) {
            String value = isSubType(command) ? splitArgs[1] : splitArgs[2];
            return "> **" + value + "** Not a valid Number.";
        }
        if (pixelAmount < 0) return "> I don't know what negative pixels are. What are you trying to do?";
        if (pixelAmount > Constants.PIXELS_CAP)
            return "> That's too many pixels for me to be working with. (Max: " + Constants.PIXELS_CAP + ")";

        ProfileObject profile = user.getProfile(command.guild);
        if (profile == null) return "> " + user.displayName + " doesn't have a profile yet.";

        String out;
        if (SET_XP.isSubCommand(command)) {
            out = setXp(profile, pixelAmount, user);
            xpChanged = true;
        } else if (ADD_XP.isSubCommand(command)) {
            out = addXp(profile, pixelAmount, user);
            xpChanged = true;
        } else if (DEL_XP.isSubCommand(command)) {
            out = delXp(profile, pixelAmount, user);
            xpChanged = true;
        } else {
            String modif = splitArgs[1].toLowerCase();
            switch (modif) {
                case "+":
                case "add":
                    out = addXp(profile, pixelAmount, user);
                    xpChanged = true;
                    break;
                case "-":
                case "rem":
                case "sub":
                    out = delXp(profile, pixelAmount, user);
                    xpChanged = true;
                    break;
                case "=":
                case "set":
                    out = setXp(profile, pixelAmount, user);
                    xpChanged = true;
                    break;
                default:
                    out = "> Invalid modifier. Valid modifiers are **[+/-/=]** or **add/sub/set**";
                    break;
            }
        }

        if (xpChanged) {
            profile.removeLevelFloor();
            GuildHandler.checkUsersRoles(user.longID, command.guild);
        }
        return out;
    }

    private String addXp(ProfileObject profile, long pixelAmount, UserObject user) {
        long diff = Constants.PIXELS_CAP - (profile.getXP());
        profile.setXp(profile.getXP() + pixelAmount);
        //pixels should never go over the pixel cap.
        if (profile.getXP() > Constants.PIXELS_CAP) {
            profile.setXp(Constants.PIXELS_CAP);
            return "> Added **" + diff + "** pixels to **" + user.displayName + "**. They are now at the Pixel cap.";
        }
        return "> Added **" + pixelAmount + "** pixels to **" + user.displayName + "**.";
    }

    private String setXp(ProfileObject profile, long pixelAmount, UserObject user) {
        profile.setXp(pixelAmount);
        return "> Set Pixels to **" + pixelAmount + "** for user **" + user.displayName + "**.";
    }

    private String delXp(ProfileObject profile, long pixelAmount, UserObject user) {
        profile.setXp(profile.getXP() - pixelAmount);
        // Special handling if XP is set below 0
        if (profile.getXP() < 0) {
            long diff = pixelAmount + profile.getXP();
            profile.setXp(0);
            return "> Removed **" + diff + "** Pixels from **" + user.displayName + "**. They no longer have any pixels.";
        }
        return "> Removed **" + pixelAmount + "** pixels from **" + user.displayName + "**.";
    }

    private String getMissingArgsSubCommand(CommandObject command) {
        for (SubCommandObject s : subCommands) {
            if (s.isSubCommand(command)) {
                return ">> **" + s.getUsage() + "** <<";
            }
        }
        return missingArgs(command);
    }

    private boolean isSubType(CommandObject command) {
        for (SubCommandObject s : subCommands) {
            if (s.isSubCommand(command)) return true;
        }
        return false;
    }

    @Override
    protected String[] names() {
        return new String[]{"EditPixels", "EditXp"};
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
        subCommands.add(SET_XP);
        subCommands.add(ADD_XP);
        subCommands.add(DEL_XP);
    }
}
