package com.github.vaerys.commands.pixels;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class Pixels extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(Constants.pixelColour);
        UserObject user = command.user;
        if (!args.isEmpty()) {
            user = Utility.getUser(command, args, true);
            if (user == null) return "\\> Could not find user.";
        }
        ProfileObject profile = user.getProfile();
        if (profile == null) {
            return String.format("\\> %s does not have a profile yet.", user.displayName);
        }
        if (user.isPrivateProfile() && !user.equals(command.user)) {
            return String.format("\\> %s has set their profile to private.", user.displayName);
        }
        //init vars

        long currentLevel = profile.getCurrentLevel();

        long pixels = profile.getXP();
        long rank = PixelHandler.rank(command.guild, user);
        long totalRanked = PixelHandler.totalRanked(command);


        //field headers.
        String titlePixels = "Total Pixels:";
        String titleRank = "Rank:";
        String titleProgress = "Level Progress:";

        //field contents
        String contentsPixels = pixels < 1 ? "N/a" : String.format("%,d", pixels);
        String contentsRank = rank == -1 ? "N/a" : String.format("%d/%d", rank, totalRanked);
        StringHandler contentsProgress = new StringHandler();

        //calculate progress
        if (pixels > 0) {
            int progress = getProgress(profile);
            contentsProgress.setContent("-------------------").replace(progress, progress, user.isDecaying() ? "**<**" : "**>**");
            contentsProgress.appendFrontFormatted("**%d** [", currentLevel).appendFormatted("] **%d**", currentLevel + 1);
        } else {
            contentsProgress.setContent("N/a");
        }

        //build embed
        builder.setAuthor(user.displayName + "'s Pixel stats.", Constants.PIXELS_ICON); //pixel icon
        builder.addField(titlePixels, contentsPixels, true);
        builder.addField(titleRank, contentsRank, true);
        builder.addField(titleProgress, contentsProgress.toString(), false);

        if (profile.getSettings().contains(UserSetting.HIT_LEVEL_FLOOR)) {
            builder.setDescription("**You have decayed to the level floor,\nYou will need to level up again to see your rank.**");
        }
        if (user.getProfile().getSettings().contains(UserSetting.PRIVATE_PROFILE)) {
            builder.queue(command.user.getDmChannel());
            return "\\> Pixels sent to your Direct messages.";
        }
        builder.queue(command);
        return null;
    }

    /***
     * Calculator for the progress for next rank based on a number between 0 and 19.
     *
     * @param profile the globalUser being tested
     * @return progress [0-19]
     */
    private int getProgress(ProfileObject profile) {
        long nextLevel = profile.getCurrentLevel() + 1;
        long xpForNext = PixelHandler.levelToXP(nextLevel);
        long xpTillNext = PixelHandler.totalXPForLevel(nextLevel) - profile.getXP();
        long progress = ((xpForNext - xpTillNext) * 100) / xpForNext;
        int pos = (int) (progress / 5);
        if (pos < 0) pos = 0;
        if (pos > 19) pos = 19;
        return pos;
    }

    @Override
    protected String[] names() {
        return new String[]{"Pixels"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shows you your current Pixel count and rank.";
    }

    @Override
    protected String usage() {
        return "(@User)";
    }

    @Override
    protected SAILType type() {
        return SAILType.PIXEL;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.PIXELS;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
