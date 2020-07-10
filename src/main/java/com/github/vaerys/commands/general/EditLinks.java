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
import com.github.vaerys.objects.userlevel.UserLinkObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Vaerys on 17/06/2017.
 */
public class EditLinks extends Command {

    protected static final SubCommandObject ADMIN_EDIT = new SubCommandObject(
            new String[]{"EditLinks", "NewLink"},
            "[@User] [Link Name] (Link)",
            "Allows the modification of user links.",
            SAILType.MOD_TOOLS,
            Permissions.MANAGE_MESSAGES
    );

    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = command.user;
        SplitFirstObject userCall = new SplitFirstObject(args);
        boolean adminEdit = false;
        if (GuildHandler.testForPerms(command, ADMIN_EDIT.getPermissions()) || GuildHandler.canBypass(command.user.get(), command.guild.get())) {
            user = Utility.getUser(command, userCall.getFirstWord(), false);
            if (user != null && userCall.getRest() != null && user.getProfile() != null) {
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
        ProfileObject userObject = user.getProfile();
        if (userObject == null) {
            return "\\> " + user.displayName + " Has not Spoken yet thus they have nothing to edit.";
        }
        userObject.setLinks();
        for (UserLinkObject link : userObject.getLinks()) {
            if (link.getName().equalsIgnoreCase(linkName.getFirstWord())) {
                userObject.getLinks().remove(link);
                if (adminEdit) {
                    return "\\> " + user.displayName + "'s Link was removed.";
                } else {
                    return "\\> Link removed.";
                }
            }
        }
        int maxLinks = 5;
        if (user.isPatron) {
            maxLinks += 5;
        }
        if (linkName.getRest() == null) {
            return "\\> Cannot add link, Must specify a URL.";
        } else {
            if (userObject.getLinks().size() >= maxLinks) {
                if (adminEdit) {
                    return "\\> " + user.displayName + " already has " + maxLinks + " links, a link must be removed to add a new one.";
                } else {
                    return "\\> You already have " + maxLinks + " links, you must remove one to add a new one.";
                }
            }
            if (linkName.getFirstWord().length() > 15) {
                return "\\> Link Name too long. (Max 15 chars)";
            }
            if (linkName.getFirstWord().contains("\n")){
                return "\\> Link Name cannot contain Newlines.";
            }
            if (Utility.checkURL(linkName.getRest())) {
                try {
                    new URL(linkName.getRest());
                    userObject.getLinks().add(new UserLinkObject(linkName.getFirstWord(), linkName.getRest()));
                    if (adminEdit) {
                        return "\\> New link added for " + user.displayName + ".";
                    } else {
                        return "\\> New link added.";
                    }
                } catch (MalformedURLException e) {
                    return "\\> Cannot add link, Invalid URL.";
                }
            } else {
                return "\\> Cannot add link, Invalid URL.";
            }
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"EditLinks", "NewLink"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows uses to manage the links attached to their profile. Max 5 links per user (10 if user is a patron).";
    }

    @Override
    protected String usage() {
        return "[Link Name] (Link)";
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
    protected Permission[] perms() {
        return new Permission[0];
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
