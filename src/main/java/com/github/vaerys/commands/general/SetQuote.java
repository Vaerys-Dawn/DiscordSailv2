package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 27/02/2017.
 */
public class SetQuote extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = command.user;
        String quote = args;
        boolean adminEdit = false;
        if (isSubtype(command, "SetUserQuote")) {
            if (Utility.testForPerms(command, Permissions.MANAGE_MESSAGES)) {
                SplitFirstObject userCall = new SplitFirstObject(quote);
                user = Utility.getUser(command, userCall.getFirstWord(), false, true);
                if (user == null) return "> Could not find user.";
                quote = userCall.getRest();
                adminEdit = true;
            } else {
                return command.user.notAllowed;
            }
        }
        int maxLength = 140;
        if (user.isPatron) {
            maxLength += 140;
        }
        ProfileObject u = user.getProfile(command.guild);
        quote = Utility.removeFun(quote);
        if (quote == null || quote.isEmpty()) return "> You can't have an empty quote.";
        for (String s : quote.split(" ")) {
            if (!Utility.checkURL(s)) {
                return "> Cannot add quote. Malicious link found.";
            }
        }
        if (adminEdit) {
            if (quote.length() > maxLength) {
                return "> Quote is too long...\n(must be under " + maxLength + " chars)";
            }
            u.setQuote(quote);
            return "> " + user.displayName + "'s Quote Edited.";
        } else {
            if (quote.length() > maxLength) {
                return "> Your Quote is too long...\n(must be under " + maxLength + " chars)";
            }
            u.setQuote(quote);
            return "> Quote Edited.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"SetQuote", "SetUserQuote", "SetDesc", "SetDescription"};
    }

    @Override
    public String description(CommandObject command) {
        String response = "Allows you to set your quote. Limit 140 chars (or 280 if you are a patron)";
        if (Utility.testForPerms(command, Permissions.MANAGE_MESSAGES)) {
            response += "\n\n**" + command.guild.config.getPrefixCommand() + names[1] + " [@User] [Quote...]**\n" +
                    "**Desc:** Edits a user's quote.\n" +
                    "**Permissions:** " + Permissions.MANAGE_MESSAGES + ".\n";
        }
        return response;

    }

    @Override
    public String usage() {
        return "[Quote...]";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public String channel() {
        return CHANNEL_BOT_COMMANDS;
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
    public void init() {

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
