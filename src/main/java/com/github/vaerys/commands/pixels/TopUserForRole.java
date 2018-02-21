package com.github.vaerys.commands.pixels;

import java.text.NumberFormat;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class TopUserForRole extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        IRole role = Utility.getRoleFromName(args, command.guild.get());
        if (role == null) {
            return "> Invalid Role.";
        }
        IMessage working = RequestHandler.sendMessage("`Working...`", command.channel.get()).get();
        IUser topUser = null;
        ProfileObject topUserObject;
        ProfileObject userTypeObject;
        for (IUser user : command.guild.getUsers()) {
            if (user.getRolesForGuild(command.guild.get()).contains(role)) {
                if (topUser == null) {
                    topUser = user;
                    topUserObject = command.guild.users.getUserByID(topUser.getLongID());
                    if (topUserObject == null) {
                        topUser = null;
                    } else if (XpHandler.rank(command.guild.users, command.guild.get(), topUserObject.getUserID()) == -1) {
                        topUser = null;
                    }
                } else {
                    userTypeObject = command.guild.users.getUserByID(user.getLongID());
                    topUserObject = command.guild.users.getUserByID(topUser.getLongID());
                    if (topUserObject != null && userTypeObject != null) {
                        if (XpHandler.rank(command.guild.users, command.guild.get(), userTypeObject.getUserID()) != -1) {
                            if (topUserObject.getXP() < userTypeObject.getXP()) {
                                topUser = user;
                            }
                        }
                    }
                }
            }
        }
        try {
            topUserObject = command.guild.users.getUserByID(topUser.getLongID());
            RequestHandler.deleteMessage(working);
            if (topUserObject != null) {
                return "> @" + topUser.getName() + "#" + topUser.getDiscriminator() + ", Pixels: " + NumberFormat.getInstance().format(topUserObject.getXP()) + ", Level: " + topUserObject.getCurrentLevel() + ", UserID: " + topUserObject.getUserID();
            } else {
                return "> User could not be found.";
            }
        } catch (NullPointerException e) {
            return "> An error occurred.";
        }
    }

    protected static final String[] NAMES = new String[]{"TopUserForRole"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gets the top user (Pixel wise) for a specific role.";
    }

    protected static final String USAGE = "[Role Name]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.PIXEL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }


    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_ROLES};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}