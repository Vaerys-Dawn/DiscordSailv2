package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.RewardRoleObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class ManagePixelRoles extends Command {

    private static String modes = "**Modes**\n" +
            "> Any positive number up to 256\n" +
            "> xpDenied\n" +
            "> topTen\n" +
            "> Remove\n" +
            "> List\n";

    @Override
    public String execute(String args, CommandObject command) {
        if (args.equalsIgnoreCase("list")){
            XEmbedBuilder builder = new XEmbedBuilder(command);
            builder.withTitle("Pixel Roles");
            IRole xpDenied = command.guild.getXPDeniedRole();
            IRole topTen = command.guild.getTopTenRole();
            List<RewardRoleObject> rewardRoles = command.guild.config.getRewardRoles();
            if (xpDenied == null && topTen == null && rewardRoles.size() == 0) {
                return "> No roles are set up.\n" + missingArgs(command);
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
            builder.withDesc(desc);
            builder.send(command.channel);
            return null;
        }
        SplitFirstObject mode = new SplitFirstObject(args);
        IRole role = GuildHandler.getRoleFromName(mode.getRest(), command.guild.get());
        if (role == null) {
            return "> **" + mode.getRest() + "** is not a valid Role name.";
        }
        try {
            long level = Long.parseLong(mode.getFirstWord());
            if (level > 256 || level <= 0) {
                return "> Level must be between 1-256.";
            }
            for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                if (r.getLevel() == level) {
                    return "> That level already has a reward set.";
                }
                if (r.getRoleID() == role.getLongID()) {
                    r.setLevel(level);
                    return "> Level to obtain **" + role.getName() + "** is now set to level **" + level + "**.";
                }
            }
            command.guild.config.getRewardRoles().add(new RewardRoleObject(role.getLongID(), level));
            return "> The role **" + role.getName() + "** will now be awarded at level **" + level + "**.";
        } catch (NumberFormatException e) {
            switch (mode.getFirstWord().toLowerCase()) {
                case "xpdenied":
                    for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                        if (r.getRoleID() == role.getLongID()) {
                            return "> **" + role.getName() + "** is already set as a reward role.";
                        }
                    }
                    if (role.getLongID() == command.guild.config.topTenRoleID) {
                        return "> **" + role.getName() + "** is already set as the server's TopTen role.";
                    }
                    command.guild.config.xpDeniedRoleID = role.getLongID();
                    return "> **" + role.getName() + "** is now the server's XpDenied role.";
                case "topten":
                    for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                        if (r.getRoleID() == role.getLongID()) {
                            return "> **" + role.getName() + "** is already set as a reward role.";
                        }
                    }
                    if (role.getLongID() == command.guild.config.xpDeniedRoleID) {
                        return "> **" + role.getName() + "** is already set as the server's xpDenied role.";
                    }
                    command.guild.config.topTenRoleID = role.getLongID();
                    return "> **" + role.getName() + "** is now the server's TopTen role.";
                case "remove":
                    if (role.getLongID() == command.guild.config.xpDeniedRoleID) {
                        command.guild.config.xpDeniedRoleID = -1;
                        return "> **" + role.getName() + "** is no longer the server's xpDenied role.";
                    } else if (role.getLongID() == command.guild.config.topTenRoleID) {
                        command.guild.config.topTenRoleID = -1;
                        return "> **" + role.getName() + "** is no longer the server's Top Ten role.";
                    }
                    for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                        if (r.getRoleID() == role.getLongID()) {
                            command.guild.config.getRewardRoles().remove(r);
                            return "> **" + role.getName() + "** is no longer set as a reward role.";
                        }
                    }
                    return "> **" + role.getName() + "** is not a valid Pixel role.";
                default:
                    return "> Invalid Mode.\n" + modes +
                            Utility.getCommandInfo(this, command);
            }
        }
    }

    protected static final String[] NAMES = new String[]{"ManagePixelRoles", "PixelRoles"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Allows for the editing of pixel roles such as reward roles, the xp denied role and the top ten role.\n" + modes;
    }

    protected static final String USAGE = "[Number/Mode] [RoleName]";
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
