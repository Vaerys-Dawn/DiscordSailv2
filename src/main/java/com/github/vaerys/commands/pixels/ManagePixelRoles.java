package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.RewardRoleObject;
import com.github.vaerys.objects.utils.SplitFirstObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class ManagePixelRoles extends Command {

    private static String modes = "**Modes**\n" +
            "\\> Any positive number up to 256\n" +
            "\\> xpDenied\n" +
            "\\> topTen\n" +
            "\\> Remove\n" +
            "\\> List\n";

    @Override
    public String execute(String args, CommandObject command) {
        if (args.equalsIgnoreCase("list")) {
            XEmbedBuilder builder = new XEmbedBuilder(command);
            builder.setTitle("Pixel Roles");
            IRole xpDenied = command.guild.getXPDeniedRole();
            IRole topTen = command.guild.getTopTenRole();
            List<RewardRoleObject> rewardRoles = command.guild.config.getRewardRoles();
            if (xpDenied == null && topTen == null && rewardRoles.size() == 0) {
                return "\\> No roles are set up.\n" + missingArgs(command);
            }
            String desc = "";
            if (rewardRoles.size() != 0) {
                desc += "**Reward Roles**";
                for (RewardRoleObject r : rewardRoles) {
                    desc += "\n> **" + command.guild.getRoleByID(r.getRoleID()).getName() + "** -> Level: **" + r.getLevel() + "**";
                }
                desc += "\n\n";
            }
            if (topTen != null) {
                desc += "**Top Ten Role**: " + topTen.getName() + "\n\n";
            }
            if (xpDenied != null) {
                desc += "**Pixel Denied Role**: " + xpDenied.getName();
            }
            builder.setDescription(desc);
            builder.send(command.channel);
            return null;
        }
        SplitFirstObject mode = new SplitFirstObject(args);
        IRole role = GuildHandler.getRoleFromName(mode.getRest(), command.guild.get());
        if (role == null) {
            return "\\> **" + mode.getRest() + "** is not a valid Role name.";
        }
        try {
            long level = Long.parseLong(mode.getFirstWord());
            if (level > 256 || level <= 0) {
                return "\\> Level must be between 1-256.";
            }
            for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                if (r.getLevel() == level) {
                    return "\\> That level already has a reward set.";
                }
                if (r.getRoleID() == role.getLongID()) {
                    r.setLevel(level);
                    return "\\> Level to obtain **" + role.getName() + "** is now set to level **" + level + "**.";
                }
            }
            command.guild.config.getRewardRoles().add(new RewardRoleObject(role.getLongID(), level));
            return "\\> The role **" + role.getName() + "** will now be awarded at level **" + level + "**.";
        } catch (NumberFormatException e) {
            switch (mode.getFirstWord().toLowerCase()) {
                case "xpdenied":
                    for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                        if (r.getRoleID() == role.getLongID()) {
                            return "\\> **" + role.getName() + "** is already set as a reward role.";
                        }
                    }
                    if (role.getLongID() == command.guild.config.topTenRoleID) {
                        return "\\> **" + role.getName() + "** is already set as the server's TopTen role.";
                    }
                    command.guild.config.xpDeniedRoleID = role.getLongID();
                    return "\\> **" + role.getName() + "** is now the server's XpDenied role.";
                case "topten":
                    for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                        if (r.getRoleID() == role.getLongID()) {
                            return "\\> **" + role.getName() + "** is already set as a reward role.";
                        }
                    }
                    if (role.getLongID() == command.guild.config.xpDeniedRoleID) {
                        return "\\> **" + role.getName() + "** is already set as the server's xpDenied role.";
                    }
                    command.guild.config.topTenRoleID = role.getLongID();
                    return "\\> **" + role.getName() + "** is now the server's TopTen role.";
                case "remove":
                    if (role.getLongID() == command.guild.config.xpDeniedRoleID) {
                        command.guild.config.xpDeniedRoleID = -1;
                        return "\\> **" + role.getName() + "** is no longer the server's xpDenied role.";
                    } else if (role.getLongID() == command.guild.config.topTenRoleID) {
                        command.guild.config.topTenRoleID = -1;
                        return "\\> **" + role.getName() + "** is no longer the server's Top Ten role.";
                    }
                    for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                        if (r.getRoleID() == role.getLongID()) {
                            command.guild.config.getRewardRoles().remove(r);
                            return "\\> **" + role.getName() + "** is no longer set as a reward role.";
                        }
                    }
                    return "\\> **" + role.getName() + "** is not a valid Pixel role.";
                default:
                    return "\\> Invalid Mode.\n" + modes +
                            missingArgs(command);
            }
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"ManagePixelRoles", "PixelRoles"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for the editing of pixel roles such as reward roles, the xp denied role and the top ten role.\n" + modes;
    }

    @Override
    protected String usage() {
        return "[Number/Mode] [RoleName]";
    }

    @Override
    protected SAILType type() {
        return SAILType.PIXEL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[]{Permissions.MANAGE_ROLES};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
