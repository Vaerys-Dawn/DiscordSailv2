package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.RewardRoleObject;
import com.github.vaerys.objects.SplitFirstObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class PixelHelp implements Command {

    String modes = "\n**Modes:** \n> LevelToXp - `Gives the total pixels for that level.`\n" +
            "`Aliases: level2xp, level->xp.`\n" +
            "> XpToLevel - `Gives the level for that amount of pixels.`\n" +
            "`Aliases: xp2level, xp->level.`";

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject obe = new SplitFirstObject(args);
        String mode = obe.getFirstWord();
        switch (mode.toLowerCase()) {
            case "leveltoxp":
                return leveltoxp(obe.getRest());
            case "level2xp":
                return leveltoxp(obe.getRest());
            case "level->xp":
                return leveltoxp(obe.getRest());
            case "xptolevel":
                return xptolevel(obe.getRest());
            case "xp2level":
                return xptolevel(obe.getRest());
            case "xp->level":
                return xptolevel(obe.getRest());
            default:
                List<String> channelMentions = Utility.getChannelMentions(command.guild.config.getChannelIDsByType(CHANNEL_XP_DENIED), command);
                StringBuilder builder = new StringBuilder();
                builder.append("**The rules for gaining xp are:**\n");
                if (channelMentions.size() != 0) {
                    builder.append("> Cannot gain xp in any of these channels: \n**>> " + Utility.listFormatter(channelMentions, true) + " <<**\n");
                }
                builder.append("> Cannot gain xp if the message starts with a command prefix.\n" +
                        "> Cannot gain xp if the message contains less than 10 chars.\n" +
                        "> Cannot gain xp if your profile has NoXp or XpDenied on it.\n" +
                        "> Can only gain xp once per min\n\n");
                if (command.guild.config.getRewardRoles().size() != 0) {
                    builder.append("**Reward Roles:**");
                    for (RewardRoleObject r : command.guild.config.getRewardRoles()) {
                        IRole reward = command.guild.get().getRoleByID(r.getRoleID());
                        if (reward == null) {
                            break;
                        }
                        builder.append("\n> **" + reward.getName() + "** -> Level: **" + r.getLevel() + "**");
                    }
                    builder.append("\n\n");
                }
                if (command.guild.config.xpModifier != 1) {
                    builder.append("**Current Xp Modifier:**\n");
                    builder.append("> **x" + command.guild.config.xpModifier + "**.\n\n");
                }
                return builder.toString() + Utility.getCommandInfo(this, command);
        }
    }

    private String leveltoxp(String args) {
        try {
            long level = Long.parseLong(args);
            if (level < 0) {
                return "> Please use a positive number.";
            }
            if (level > 1000) {
                return "> No, I don't want to calculate the total xp for level " + level + "!";
            }
            return "> Level: " + level + " = " + NumberFormat.getInstance().format(XpHandler.totalXPForLevel(level)) + " pixels.";
        } catch (NumberFormatException e) {
            return "> You must supply a valid number.";
        }
    }

    private String xptolevel(String args) {
        try {
            long xp = Long.parseLong(args);
            if (xp < 0) {
                return "> Please use a positive number.";
            }
            if (xp > 1345412000) {
                return "> Its something over level 1000, could you leave me alone.";
            }
            return "> " + xp + "XP = Level: " + XpHandler.xpToLevel(xp);
        } catch (NumberFormatException e) {
            return "> You must supply a valid number.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"PixelHelp", "HelpPixels"};
    }

    @Override
    public String description() {
        return "Gives you information about pixels." + modes;
    }

    @Override
    public String usage() {
        return ("(mode) (args)");
    }

    @Override
    public String type() {
        return TYPE_PIXEL;
    }

    @Override
    public String channel() {
        return CHANNEL_PIXELS;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
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
