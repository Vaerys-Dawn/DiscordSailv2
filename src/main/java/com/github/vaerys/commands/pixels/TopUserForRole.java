package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ProfileObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;

public class TopUserForRole implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        IRole role = Utility.getRoleFromName(args, command.guild.get());
        if (role == null) {
            return "> Invalid Role.";
        }
        IMessage working = Utility.sendMessage("`Working...`", command.channel.get()).get();
        IUser topUser = null;
        ProfileObject topUserObject;
        ProfileObject userTypeObject;
        for (IUser user : command.guild.get().getUsers()) {
            if (user.getRolesForGuild(command.guild.get()).contains(role)) {
                if (topUser == null) {
                    topUser = user;
                    topUserObject = command.guild.users.getUserByID(topUser.getStringID());
                    if (topUserObject == null) {
                        topUser = null;
                    } else if (XpHandler.rank(command.guild.users, command.guild.get(), topUserObject.getID()) == -1) {
                        topUser = null;
                    }
                } else {
                    userTypeObject = command.guild.users.getUserByID(user.getStringID());
                    topUserObject = command.guild.users.getUserByID(topUser.getStringID());
                    if (topUserObject != null && userTypeObject != null) {
                        if (XpHandler.rank(command.guild.users, command.guild.get(), userTypeObject.getID()) != -1) {
                            if (topUserObject.getXP() < userTypeObject.getXP()) {
                                topUser = user;
                            }
                        }
                    }
                }
            }
        }
        try {
            topUserObject = command.guild.users.getUserByID(topUser.getStringID());
            Utility.deleteMessage(working);
            if (topUserObject != null) {
                return "> @" + topUser.getName() + "#" + topUser.getDiscriminator() + ", Pixels: " + NumberFormat.getInstance().format(topUserObject.getXP()) + ", Level: " + topUserObject.getCurrentLevel() + ", UserID: " + topUserObject.getID();
            } else {
                return "> User could not be found.";
            }
        } catch (NullPointerException e) {
            return "> An error occurred.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"TopUserForRole"};
    }

    @Override
    public String description() {
        return "Gets the top user (Pixel wise) for a specific role.";
    }

    @Override
    public String usage() {
        return "[Role Name]";
    }

    @Override
    public String type() {
        return TYPE_PIXEL;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_ROLES};
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }

    @Override
    public boolean doAdminLogging() {
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
}