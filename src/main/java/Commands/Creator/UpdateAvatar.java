package Commands.Creator;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.DailyMessageObject;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.Image;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class UpdateAvatar implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        if (Globals.doDailyAvatars == true) {
            for (DailyMessageObject d : Globals.dailyMessages) {
                if (d.getDayOfWeek().equals(nowUTC.getDayOfWeek())) {
                    Image avatar = Image.forFile(new File(Constants.DIRECTORY_GLOBAL_IMAGES + d.getFileName()));
                    Utility.updateAvatar(avatar);
                }
            }
        } else {
            Image avatar = Image.forFile(new File(Constants.DIRECTORY_GLOBAL_IMAGES + Globals.defaultAvatarFile));
            Utility.updateAvatar(avatar);
        }
        Utility.sendGlobalAdminLogging(this, args, command);
        return "> Avatar Updated.";
    }

    @Override
    public String[] names() {
        return new String[]{"UpdateAvatar"};
    }

    @Override
    public String description() {
        return "Update's the bot's Avatar.\n" + ownerOnly;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public String channel() {
        return null;
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
