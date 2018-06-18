package com.github.vaerys.commands.general;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.objects.utils.SubCommandObject;
import com.github.vaerys.templates.Command;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 27/02/2017.
 */
public class SetQuote extends Command {

    public static final SubCommandObject ADMIN_EDIT = new SubCommandObject(
            new String[]{"SetUserQuote", "SetUserDesc"},
            "[@User] [Quote]",
            "Edits a user's quote.",
            SAILType.MOD_TOOLS,
            Permissions.MANAGE_MESSAGES
    );

    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = command.user;
        String quote = args;
        boolean adminEdit = false;
        if (ADMIN_EDIT.isSubCommand(command)) {
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
        int maxLength = 140;
        if (user.isPatron) {
            maxLength += 140;
        }
        int maxNewlines = 2;
        if (user.isPatron) {
            maxNewlines += 2;
        }
        ProfileObject u = user.getProfile(command.guild);
        quote = Utility.removeFun(quote);
        if (quote == null || quote.isEmpty()) return "> You can't have an empty quote.";
        if (StringUtils.countMatches(quote, "\n") > maxNewlines)
            return "> You have too many newlines in that quote. (Max: " + maxNewlines + ")";
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
    protected String[] names() {
        return new String[]{"SetQuote", "SetDesc", "SetDescription"};
    }

    @Override
    public String description(CommandObject command) {
        String response = "Allows you to set your quote. Limit 140 chars (or 280 if you are a patron)";
        return response;

    }

    @Override
    protected String usage() {
        return "[Quote...]";
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.PROFILES;
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
