package Commands.Pixels;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Utility;
import Objects.RewardRoleObject;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class ManagePixelRoles implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject mode = new SplitFirstObject(args);
        String roleID = Utility.getRoleIDFromName(mode.getRest(), command.guild);
        IRole role = command.guild.getRoleByID(roleID);
        if (role == null) {
            return "> **" + mode.getRest() + "** is not a validrRole name.";
        }
        try {
            long level = Long.parseLong(mode.getFirstWord());
            if (level > 256 || level <= 0) {
                return "> Level must be between 1-256.";
            }
            for (RewardRoleObject r : command.guildConfig.getRewardRoles()) {
                if (r.getLevel() == level) {
                    return "> That level already has a reward set.";
                }
                if (r.getRoleID() == role.getLongID()) {
                    r.setLevel(level);
                    return "> Level to obtain **" + role.getName() + "** is now set to level **" + level + "**.";
                }
            }
            command.guildConfig.getRewardRoles().add(new RewardRoleObject(role.getLongID(), level));
            return "> The role **" + role.getName() + "** will now be awarded at level **" + level + "**.";
        } catch (NumberFormatException e) {
            switch (mode.getFirstWord().toLowerCase()) {
                case "xpdenied":
                    for (RewardRoleObject r : command.guildConfig.getRewardRoles()) {
                        if (r.getRoleID() == role.getLongID()) {
                            return "> **" + role.getName() + "** is already set as a reward role.";
                        }
                    }
                    if (role.getLongID() == command.guildConfig.topTenRoleID) {
                        return "> **" + role.getName() + "** is already set as the server's TopTen role.";
                    }
                    command.guildConfig.xpDeniedRoleID = role.getLongID();
                    return "> **" + role.getName() + "** is now the server's XpDenied role.";
                case "topten":
                    for (RewardRoleObject r : command.guildConfig.getRewardRoles()) {
                        if (r.getRoleID() == role.getLongID()) {
                            return "> **" + role.getName() + "** is already set as a reward role.";
                        }
                    }
                    if (role.getLongID() == command.guildConfig.xpDeniedRoleID) {
                        return "> **" + role.getName() + "** is already set as the server's xpDenied role.";
                    }
                    command.guildConfig.topTenRoleID = role.getLongID();
                    return "> **" + role.getName() + "** is now the server's TopTen role.";
                case "remove":
                    if (role.getLongID() == command.guildConfig.xpDeniedRoleID) {
                        command.guildConfig.xpDeniedRoleID = -1;
                        return "> **" + role.getName() + "** is no longer the server's xpDenied role.";
                    } else if (role.getLongID() == command.guildConfig.topTenRoleID) {
                        command.guildConfig.topTenRoleID = -1;
                        return "> **" + role.getName() + "** is no longer the server's xpDenied role.";
                    }
                    for (RewardRoleObject r : command.guildConfig.getRewardRoles()) {
                        if (r.getRoleID() == role.getLongID()) {
                            command.guildConfig.getRewardRoles().remove(r);
                            return "> **" + role.getName() + "** is no longer set as a reward role.";
                        }
                    }
                    return "> **" + role.getName() + "** is not a valid Pixel role.";
                default:
                    return "> Invalid Mode.\n" +
                            "**Modes**\n" +
                            "- Any positive number up to 256\n" +
                            "- xpDenied\n" +
                            "- topTen\n" +
                            "- Remove\n" +
                            Utility.getCommandInfo(this, command);
            }
        }
    }

    @Override
    public String[] names() {
        return new String[]{"ManagePixelRoles", "PixelRoles"};
    }

    @Override
    public String description() {
        return "Allows for the editing of pixel roles such as reward roles, the xp denied role and the top ten role.\n" +
                "**Modes**\n" +
                "- Any positive number up to 256\n" +
                "- xpDenied\n" +
                "- topTen\n" +
                "- remove";
    }

    @Override
    public String usage() {
        return "[Number/Mode] [RoleName]";
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
