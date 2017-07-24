package Commands.Admin;

import Commands.CommandObject;
import Enums.UserSetting;
import Interfaces.Command;
import Main.Utility;
import Objects.SplitFirstObject;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 06/07/2017.
 */
public class UserSettings implements Command {
    private String settings = "**Settings**\n" +
            "- DeniedXp\n" +
            "- DontShowRank\n";

    @Override
    public String execute(String args, CommandObject command) {
        SplitFirstObject split = new SplitFirstObject(args);
        IUser user = null;
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
                    if (userObject.getSettings().contains(UserSetting.DENIED_XP)) {
                        for (UserSetting s : userObject.getSettings()) {
                            if (s == UserSetting.DENIED_XP) {
                                userObject.getSettings().remove(s);
                                return "> User will now gain xp again.";
                            }
                        }
                    } else {
                        userObject.getSettings().add(UserSetting.DENIED_XP);
                        return "> User will no longer gain XP.";
                    }
                case "dontshowrank":
                    if (userObject.getSettings().contains(UserSetting.DONT_SHOW_LEADERBOARD)) {
                        for (UserSetting s : userObject.getSettings()) {
                            if (s == UserSetting.DONT_SHOW_LEADERBOARD) {
                                userObject.getSettings().remove(s);
                                return "> User's rank is now visible.";
                            }
                        }
                    } else {
                        userObject.getSettings().add(UserSetting.DONT_SHOW_LEADERBOARD);
                        return "> User will no longer display their rank.";
                    }
                default:
                    return "> Invalid setting.\n" + settings + "\n\n" + Utility.getCommandInfo(this, command);
            }
        } else {
            return "> Invalid user.";
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
