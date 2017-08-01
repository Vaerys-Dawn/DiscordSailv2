package Commands.Pixels;

import Commands.CommandObject;
import Handlers.XpHandler;
import Interfaces.Command;
import Main.Utility;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;

public class TopUserForRole implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        IRole role = Utility.getRoleFromName(args, command.guild);
        if (role == null) {
            return "> Invalid Role.";
        }
        IMessage working = Utility.sendMessage("`Working...`", command.channel).get();
        IUser topUser = null;
        UserTypeObject topUserObject;
        UserTypeObject userTypeObject;
        for (IUser user : command.guild.getUsers()) {
            if (user.getRolesForGuild(command.guild).contains(role)) {
                if (topUser == null) {
                    topUser = user;
                    topUserObject = command.guildUsers.getUserByID(topUser.getStringID());
                    if (topUserObject == null) {
                        topUser = null;
                    } else if (XpHandler.rank(command.guildUsers, command.guild, topUserObject.getID()) == -1) {
                        topUser = null;
                    }
                } else {
                    userTypeObject = command.guildUsers.getUserByID(user.getStringID());
                    topUserObject = command.guildUsers.getUserByID(topUser.getStringID());
                    if (topUserObject != null && userTypeObject != null) {
                        if (XpHandler.rank(command.guildUsers, command.guild, userTypeObject.getID()) != -1) {
                            if (topUserObject.getXP() < userTypeObject.getXP()) {
                                topUser = user;
                            }
                        }
                    }
                }
            }
        }
        topUserObject = command.guildUsers.getUserByID(topUser.getStringID());
        Utility.deleteMessage(working);
        if (topUserObject != null) {
            return "> @" + topUser.getName() + "#" + topUser.getDiscriminator() + ", Pixels: " + NumberFormat.getInstance().format(topUserObject.getXP()) + ", Level: " + topUserObject.getCurrentLevel() + ", UserID: " + topUserObject.getID();
        } else {
            return "> User could not be found.";
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