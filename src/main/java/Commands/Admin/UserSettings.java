package Commands.Admin;

import Commands.CommandObject;
import Enums.UserSetting;
import Interfaces.Command;
import Main.Utility;
import Objects.SplitFirstObject;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ListIterator;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class UserSettings implements Command {
    private String settings = "**Settings**\n" +
            "> DeniedXp\n" +
            "> DontShowRank\n" +
            "> DenyMakeCC\n" +
            "> DenyUseCCs\n" +
            "> AutoShitpost";

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject split = new SplitFirstObject(args);
        IUser user;
        if (command.message.getMentions().size() > 0) {
            user = command.message.getMentions().get(0);
        } else {
            user = command.guild.getUserByID(split.getFirstWord());
        }
        if (user == null) {
            return "> no user Specified";
        }
        UserTypeObject userObject = command.guildUsers.getUserByID(user.getStringID());
        if (userObject != null) {
            switch (split.getRest().toLowerCase()) {
                case "deniedxp":
                    return toggleSetting(userObject, UserSetting.DENY_MAKE_CC,
                            "> User will now gain xp again.",
                            "> User will no longer gain XP.");
                case "dontshowrank":
                    return toggleSetting(userObject, UserSetting.DENY_MAKE_CC,
                            "> User's rank is now visible.",
                            "> User will no longer display their rank.");
                case "denymakecc":
                    return toggleSetting(userObject, UserSetting.DENY_MAKE_CC,
                            "> User can now make custom commands.",
                            "> User can no longer make custom commands.");
                case "denyuseccs":
                    return toggleSetting(userObject, UserSetting.DENY_MAKE_CC,
                            "> User can now use custom commands.",
                            "> User can no longer use custom commands.");
                case "autoshitpost":
                    return toggleSetting(userObject, UserSetting.AUTO_SHITPOST,
                            "> User no longer has the shitpost tag automatically added to all of their new Custom Commands.",
                            "> User now has the shitpost tag automatically added to all of their new Custom Commands.");
                default:
                    return "> Invalid setting.\n" + settings + "\n\n" + Utility.getCommandInfo(this, command);
            }
        } else {
            return "> Invalid user.";
        }
    }

    private String toggleSetting(UserTypeObject user, UserSetting setting, String remove, String add) {
        if (user.getSettings().contains(setting)) {
            ListIterator iterator = user.getSettings().listIterator();
            while (iterator.hasNext()) {
                UserSetting s = (UserSetting) iterator.next();
                if (s == setting) {
                    iterator.remove();
                }
            }
            return remove;
        } else {
            user.getSettings().add(setting);
            return add;
        }
    }

    @Override
    public String[] names() {
        return new String[]{"UserSettings"};
    }

    @Override
    public String description() {
        return "allows setting of certain user settings.\n" + settings;
    }

    @Override
    public String usage() {
        return "[UserID/@User] [Setting]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
