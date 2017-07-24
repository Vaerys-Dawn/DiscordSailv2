package Commands.Pixels;

import Commands.CommandObject;
import Enums.EnumString;
import Enums.UserSetting;
import Interfaces.Command;
import Main.Constants;
import Main.Utility;
import Objects.UserTypeObject;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/07/2017.
 */
public class PixelSettings implements Command {
    String settings = "**Settings:**\n" +
            "> LevelChannel\n" +
            "> CurrentChannel\n" +
            "> DMs\n" +
            "> NoLvLMessages - `Hides your rank on the server`\n" +
            "> NoXP - `Stops you from gaining pixels`";

    @Override
    public String execute(String args, CommandObject command) {
        String message = "> Your Level messages will now be sent to ";
        UserTypeObject userObject = command.guildUsers.getUserByID(command.authorSID);
        switch (args.toLowerCase()) {
            case "levelchannel":
                removeLevelSettings(userObject);
                userObject.getSettings().add(UserSetting.SEND_LVLUP_RANK_CHANNEL);
                return message + "the server's level up channel.";
            case "currentchannel":
                removeLevelSettings(userObject);
                userObject.getSettings().add(UserSetting.SEND_LVLUP_CURRENT_CHANNEL);
                return message + "the current channel.";
            case "dms":
                removeLevelSettings(userObject);
                userObject.getSettings().add(UserSetting.SEND_LVLUP_DMS);
                return message + "your Direct messages.";
            case "nolvlmessages":
                removeLevelSettings(userObject);
                userObject.getSettings().add(UserSetting.DONT_SEND_LVLUP);
                return "> You will no longer see any level up messages.";
            case "noxp":
                if (userObject.getSettings().contains(UserSetting.NO_XP_GAIN)) {
                    for (UserSetting s : userObject.getSettings()) {
                        if (s == UserSetting.NO_XP_GAIN) {
                            userObject.getSettings().remove(s);
                            return "> You will now gain Xp again.";
                        }
                    }
                } else {
                    userObject.getSettings().add(UserSetting.NO_XP_GAIN);
                    return "> You will no longer gain XP";
                }
            case "hiderank":
                if (userObject.getSettings().contains(UserSetting.HIDE_RANK)) {
                    for (UserSetting s : userObject.getSettings()) {
                        if (s == UserSetting.HIDE_RANK) {
                            userObject.getSettings().remove(s);
                            return "> Your rank is now visible.";
                        }
                    }
                } else {
                    userObject.getSettings().add(UserSetting.HIDE_RANK);
                    return "> Your rank is now hidden.";
                }
            default:
                return settings + "\n\nCurrent Guild Default: **" + EnumString.get(command.guildConfig.getDefaultLevelMode()) + "**\n\n" +
                        Utility.getCommandInfo(this, command);
        }
    }

    private static void removeLevelSettings(UserTypeObject user) {
        for (int i = 0; i < user.getSettings().size(); i++) {
            if (Constants.levelUpStates.contains(user.getSettings().get(i))) {
                user.getSettings().remove(i);
            }
        }
    }

    @Override
    public String[] names() {
        return new String[]{"PixelSettings"};
    }

    @Override
    public String description() {
        return "Allows you to set where your level up messages will be sent.\n" + settings;
    }

    @Override
    public String usage() {
        return "[Setting]";
    }

    @Override
    public String type() {
        return TYPE_PIXEL;
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
        return true;
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
