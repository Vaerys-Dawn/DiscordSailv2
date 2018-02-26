package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 27/02/2017.
 */
public class SetGender extends Command {

    public static final SubCommandObject ADMIN_EDIT = new SubCommandObject(
            new String[]{"SetUserGender"},
            "[@User] [Gender]",
            "Edits a user's gender.",
            SAILType.ADMIN,
            Permissions.MANAGE_MESSAGES
    );

    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = command.user;
        String quote = args;
        boolean adminEdit = false;
        if (isSubtype(command, "SetUserGender")) {
            if (GuildHandler.testForPerms(command, Permissions.MANAGE_MESSAGES)) {
                SplitFirstObject userCall = new SplitFirstObject(quote);
                user = Utility.getUser(command, userCall.getFirstWord(), false, true);
                if (user == null) return "> Could not find user.";
                quote = userCall.getRest();
                adminEdit = true;
            } else {
                return command.user.notAllowed;
            }
        }
        int maxLength = 20;
        if (user.isPatron) {
            maxLength += 20;
        }
        ProfileObject u = user.getProfile(command.guild);
        quote = Utility.removeFun(quote);
        if (quote == null || quote.isEmpty()) return "> You can't have an empty gender.";
        if (adminEdit) {
            if (quote.length() > maxLength) {
                return "> Gender's Length is too long...\n(Must be under " + maxLength + " chars)";
            }
            u.setGender(quote);
            return "> " + user.displayName + "'s Gender Edited";
        } else {
            if (quote.length() > maxLength) {
                return "> Your Gender's Length is too long...\n(Must be under " + maxLength + " chars)";
            }
            u.setGender(quote);
            return "> Gender Edited";
        }

    }

    @Override
    protected String[] names() {
        return new String[]{"SetGender", "SetUserGender"};
    }

    @Override
    public String description(CommandObject command) {
        String response = "Allows you to set your Gender. Limit 20 chars (or 40 if you are a patron).";
        if (GuildHandler.testForPerms(command, Permissions.MANAGE_MESSAGES)) {
            response += "\n\n**" + command.guild.config.getPrefixCommand() + names[1] + " [@User] [Gender]**\n" +
                    "**Desc:** Edits a user's gender.\n" +
                    "**Permissions:** " + Permissions.MANAGE_MESSAGES + ".\n";
        }
        return response;
    }

    @Override
    protected String usage() {
        return "[Gender]";
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.BOT_COMMANDS;
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
        subCommands.add(ADMIN_EDIT);
    }
}
