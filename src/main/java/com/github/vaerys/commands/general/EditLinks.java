package com.github.vaerys.commands.general;

import java.net.MalformedURLException;
import java.net.URL;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.objects.UserLinkObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 17/06/2017.
 */
public class EditLinks extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = command.user;
        SplitFirstObject userCall = new SplitFirstObject(args);
        boolean adminEdit = false;
        if (Utility.testForPerms(command, ADMIN_EDIT.getPermissions()) || Utility.canBypass(command.user.get(), command.guild.get())) {
            user = Utility.getUser(command, userCall.getFirstWord(), false);
            if (user != null && userCall.getRest() != null && user.getProfile(command.guild) != null) {
                adminEdit = true;
            } else {
                user = command.user;
            }
        }

        SplitFirstObject linkName;
        if (adminEdit) {
            linkName = new SplitFirstObject(userCall.getRest());
        } else {
            linkName = new SplitFirstObject(args);
        }
        ProfileObject userObject = user.getProfile(command.guild);
        if (userObject == null) {
            return "> " + user.displayName + " Has not Spoken yet thus they have nothing to edit.";
        }
        userObject.setLinks();
        for (UserLinkObject link : userObject.getLinks()) {
            if (link.getName().equalsIgnoreCase(linkName.getFirstWord())) {
                userObject.getLinks().remove(link);
                if (adminEdit) {
                    return "> " + user.displayName + "'s Link was removed.";
                } else {
                    return "> Link removed.";
                }
            }
        }
        int maxLinks = 5;
        if (user.isPatron) {
            maxLinks += 5;
        }
        if (linkName.getRest() == null) {
            return "> Cannot add link, Must specify a URL.";
        } else {
            if (userObject.getLinks().size() >= maxLinks) {
                if (adminEdit) {
                    return "> " + user.displayName + " already has " + maxLinks + " links, a link must be removed to add a new one.";
                } else {
                    return "> You already have " + maxLinks + " links, you must remove one to add a new one.";
                }
            }
            if (linkName.getFirstWord().length() > 15) {
                return "> Link Name too long. (Max 15 chars)";
            }
            if (Utility.checkURL(linkName.getRest())) {
                try {
                    new URL(linkName.getRest());
                    userObject.getLinks().add(new UserLinkObject(linkName.getFirstWord(), linkName.getRest()));
                    if (adminEdit) {
                        return "> New link added for " + user.displayName + ".";
                    } else {
                        return "> New link added.";
                    }
                } catch (MalformedURLException e) {
                    return "> Cannot add link, Invalid URL.";
                }
            } else {
                return "> Cannot add link, Invalid URL.";
            }
        }
    }

    protected static final String[] NAMES = new String[]{"EditLinks", "NewLink"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Allows uses to manage the links attached to their profile. Max 5 links per user (10 if user is a patron).";
    }

    protected static final String USAGE = "[Link Name] (Link)";
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

    protected static final SubCommandObject ADMIN_EDIT = new SubCommandObject(
        NAMES,
        "[@User] [Link Name] (Link)",
        "Allows the modification of user links.",
        SAILType.ADMIN,
        Permissions.MANAGE_MESSAGES
    );
    @Override
    public void init() {
        subCommands.add(ADMIN_EDIT);
    }
}
