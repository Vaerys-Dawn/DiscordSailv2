package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
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

    protected static final String[] NAMES = new String[]{"SetQuote", "SetUserQuote", "SetDesc", "SetDescription"};
    @Override
    protected String[] names() {
        return NAMES;
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

    protected static final String USAGE = "[Quote...]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.GENERAL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.BOT_COMMANDS;
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
