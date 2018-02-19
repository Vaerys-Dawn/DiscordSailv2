package com.github.vaerys.commands.pixels;

import java.text.NumberFormat;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class Pixels extends Command {
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
        if (user.isDecaying(command.guild)){
            xpBar.replace(pos, pos, "**<**");
        }else {
            xpBar.replace(pos, pos, "**>**");
        }
        String levelTotal = "**" + profile.getCurrentLevel() + "** [" + xpBar.toString() + "] **" + (profile.getCurrentLevel() + 1) + "**";

        builder.withColor(Constants.pixelColour); //colour of pixels
        builder.withAuthorName(user.displayName + "'s Pixel stats.");
        builder.withAuthorIcon(Constants.PIXELS_ICON); //pixel icon
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
            RequestHandler.sendEmbedMessage("", builder, command.user.get().getOrCreatePMChannel());
            return "> Pixels sent to your Direct messages.";
        }
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;

    }

    protected static final String[] NAMES = new String[]{"Pixels"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Shows you your current Pixel count and rank.";
    }

    protected static final String USAGE = "(@User)";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.PIXEL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.PIXELS;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
