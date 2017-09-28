package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class Pixels implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder();
        UserObject user = command.user;
        if (args != null && !args.isEmpty()) {
            user = Utility.getUser(command, args, true);
            if (user == null) {
                return "> Could not find user.";
            }
        }
        ProfileObject profile = command.guild.users.getUserByID(user.longID);
        if (profile == null) {
            return "> " + user.displayName + " currently does not have a profile.";
        }
        if (user.isPrivateProfile(command.guild) && user.longID != command.user.longID) {
            return "> " + user.displayName + " has set their profile to private.";
        }
        String xpTitle = "Total Pixels: ";
        String xpTotal = NumberFormat.getInstance().format(profile.getXP());
        String levelTitle = "Level Progress: ";

        String rankTitle = "Rank: ";
        String rankTotal;
        if (XpHandler.rank(command.guild.users, command.guild.get(), profile.getUserID()) != -1 && profile.getXP() != 0) {
            rankTotal = XpHandler.rank(command.guild.users, command.guild.get(), profile.getUserID()) + "/" + XpHandler.totalRanked(command);
        } else {
            rankTotal = "N/a";
        }
        long xpForNext = XpHandler.levelToXP(profile.getCurrentLevel() + 1);
        long xpTillNext = XpHandler.totalXPForLevel(profile.getCurrentLevel() + 1) - profile.getXP();
        long xpProgress = xpForNext - xpTillNext;
        long percentToLvl = (xpProgress * 100) / xpForNext;
        StringBuilder xpBar = new StringBuilder("-------------------");
        int pos = (int) (percentToLvl / 5);
        if (pos < 0) {
            pos = 0;
        }
        if (pos > xpBar.length()) {
            pos = xpBar.length();
        }
        xpBar.replace(pos, pos, "**>**");
        String levelTotal = "**" + profile.getCurrentLevel() + "** [" + xpBar.toString() + "] **" + (profile.getCurrentLevel() + 1) + "**";

        builder.withColor(Globals.pixelColour); //colour of pixels
        builder.withAuthorName(user.displayName + "'s Pixel stats.");
        builder.withAuthorIcon("http://i.imgur.com/r5usgN7.png"); //pixel icon
        builder.appendField(xpTitle, xpTotal, true);
        builder.appendField(rankTitle, rankTotal, true);
        if (profile.getXP() != 0) {
            builder.appendField(levelTitle, levelTotal, false);
        } else {
            builder.appendField(levelTitle, "N/a", false);
        }

        if (profile.getSettings().contains(UserSetting.HIT_LEVEL_FLOOR)) {
            builder.withDescription("**You have decayed to the level floor,\nYou will need to level up again to see your rank.**");
        }
        if (user.getProfile(command.guild).getSettings().contains(UserSetting.PRIVATE_PROFILE)) {
            Utility.sendEmbedMessage("", builder, command.user.get().getOrCreatePMChannel());
            return "> Pixels sent to your Direct messages.";
        }
        Utility.sendEmbedMessage("", builder, command.channel.get());
        return null;

    }

    @Override
    public String[] names() {
        return new String[]{"Pixels"};
    }

    @Override
    public String description() {
        return "Shows you your current Pixel count and rank.";
    }

    @Override
    public String usage() {
        return "(@User)";
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
