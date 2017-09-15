package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.UserLinkObject;
import sx.blah.discord.handle.obj.Permissions;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Vaerys on 17/06/2017.
 */
public class EditLinks implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        UserObject user = command.user;
        SplitFirstObject userCall = new SplitFirstObject(args);
        boolean adminEdit = false;
        if (Utility.testForPerms(dualPerms(), command.user.get(), command.guild.get()) || Utility.canBypass(command.user.get(), command.guild.get())) {
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
        if (linkName.getRest() == null) {
            return "> Cannot add link, Must specify a URL.";
        } else {
            if (userObject.getLinks().size() > 4) {
                if (adminEdit) {
                    return "> " + user.displayName + " already has 5 links, a link must be removed to add a new one.";
                } else {
                    return "> You already have 5 links, you must remove one to add a new one.";
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

    @Override
    public String[] names() {
        return new String[]{"EditLinks", "NewLink"};
    }

    @Override
    public String description() {
        return "Allows uses to manage the links attached to their profile (Max 5 links per user).";
    }

    @Override
    public String usage() {
        return "[Link Name] (Link)";
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
    public String dualDescription() {
        return "Allows the modification of user links.";
    }

    @Override
    public String dualUsage() {
        return "[@User] [Link Name] (Link)";
    }

    @Override
    public String dualType() {
        return TYPE_ADMIN;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[]{Permissions.MANAGE_MESSAGES};
    }
}
