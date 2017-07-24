package Commands.Pixels;

import Commands.CommandObject;
import Handlers.XpHandler;
import Interfaces.Command;
import Main.Globals;
import Main.Utility;
import Objects.UserTypeObject;
import Objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class Pixels implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder();
        IUser iUser = command.author;
        if (command.message.getMentions().size() > 0) {
            iUser = command.message.getMentions().get(0);
        }
        UserTypeObject user = command.guildUsers.getUserByID(iUser.getStringID());
        if (user == null) {
            return "> That user currently does not have a profile.";
        }
        String xpTitle = "Total Pixels: ";
        String xpTotal = NumberFormat.getInstance().format(user.getXP());
        String levelTitle = "Level Progress: ";

        String rankTitle = "Rank: ";
        String rankTotal;
        if (XpHandler.rank(command.guildUsers, command.guild, user.getID()) != -1 && user.getXP() != 0) {
            rankTotal = XpHandler.rank(command.guildUsers, command.guild, user.getID()) + "/" + XpHandler.totalRanked(command);
        } else {
            rankTotal = "N/a";
        }
        long xpForNext = XpHandler.levelToXP(user.getCurrentLevel() + 1);
        long xpTillNext = XpHandler.totalXPForLevel(user.getCurrentLevel() + 1) - user.getXP();
        long xpProgress = xpForNext - xpTillNext;
        long percentToLvl = (xpProgress * 100) / xpForNext;
        StringBuilder xpBar = new StringBuilder("--------------------");
        int pos = (int) (percentToLvl / 5);
        xpBar.replace(pos, pos, "**>**");
        String levelTotal = "**" + user.getCurrentLevel() + "** [" + xpBar.toString() + "] **" + (user.getCurrentLevel() + 1) + "**";

        builder.withColor(Globals.pixelColour); //colour of pixels
        builder.withAuthorName(iUser.getDisplayName(command.guild) + "'s Pixel stats.");
        builder.withAuthorIcon("http://i.imgur.com/r5usgN7.png"); //pixel icon
        builder.appendField(xpTitle, xpTotal, true);
        builder.appendField(rankTitle, rankTotal, true);
        if (user.getXP() != 0) {
            builder.appendField(levelTitle, levelTotal, false);
        } else {
            builder.appendField(levelTitle, "N/a", false);
        }
        Utility.sendEmbedMessage("", builder, command.channel);
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
