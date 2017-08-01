package Commands.Pixels;

import Commands.CommandObject;
import Handlers.XpHandler;
import Interfaces.Command;
import Main.Utility;
import Objects.SplitFirstObject;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class PixelHelp implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject obe = new SplitFirstObject(args);
        try {
            if (obe.getFirstWord().equalsIgnoreCase("levelToXP")) {
                long level = Long.parseLong(obe.getRest());
                if (level < 0) {
                    return "> Please use a positive number.";
                }
                if (level > 1000) {
                    return "> No, I don't want to calculate the total xp for level " + level + "!";
                }
                return "> Level: " + level + " = " + NumberFormat.getInstance().format(XpHandler.totalXPForLevel(level)) + " pixels.";
            } else if (obe.getFirstWord().equalsIgnoreCase("xpToLevel")) {
                long xp = Long.parseLong(obe.getRest());
                if (xp < 0) {
                    return "> Please use a positive number.";
                }
                if (xp > 1345412000) {
                    return "> Its something over level 1000, could you leave me alone.";
                }
                return "> " + xp + "XP = Level: " + XpHandler.xpToLevel(xp);
            } else {
                return "> Pixels are S.A.I.L's Form of Xp. you can gain 20 pixels once every minute in channels that allow for pixel gain.\n\n" +
                        Utility.getCommandInfo(this, command);
            }
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
        return "Gives you information about pixels";
    }

    @Override
    public String usage() {
        return ("(LevelToXp/XpToLevel) (Level/XP)");
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
