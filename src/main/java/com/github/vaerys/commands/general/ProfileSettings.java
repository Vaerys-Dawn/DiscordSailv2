package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.EnumString;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.ProfileObject;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ListIterator;

/**
 * Created by Vaerys on 02/07/2017.
 */
public class ProfileSettings implements Command {
    String settings = "**Settings:**\n" +
            "> LevelChannel - `Level up messages will be sent to the levelup channel.`\n" +
            "> CurrentChannel - `Level up messages will be sent to the current channel.`\n" +
            "> DMs - `Level up messages will be sent to your DMs.`\n" +
            "> NoLvLMessages - `Hides your Level up messages.`\n" +
            "> NoXP - `Stops you from gaining pixels.`\n" +
            "> HideRank - `Hides your rank on the server.`\n" +
            "> NoLevelUpReactions - `Stops level up reactions.`\n" +
            "> PrivateProfile - `Hides your profile from other users.`";

    @Override
    public String execute(String args, CommandObject command) {
        String message = "> Your Level messages will now be sent to ";
        ProfileObject userObject = command.guild.users.getUserByID(command.user.longID);
        StringBuilder builder = new StringBuilder();
        for (String s : args.split(" ")) {
            switch (s.toLowerCase()) {
                case "levelchannel":
                    removeLevelSettings(userObject);
                    userObject.getSettings().add(UserSetting.SEND_LVLUP_RANK_CHANNEL);
                    builder.append(message + "the server's level up channel.");
                    break;
                case "currentchannel":
                    removeLevelSettings(userObject);
                    userObject.getSettings().add(UserSetting.SEND_LVLUP_CURRENT_CHANNEL);
                    builder.append(message + "the current channel.");
                    break;
                case "dms":
                    removeLevelSettings(userObject);
                    userObject.getSettings().add(UserSetting.SEND_LVLUP_DMS);
                    builder.append(message + "your Direct messages.");
                    break;
                case "nolvlmessages":
                    removeLevelSettings(userObject);
                    userObject.getSettings().add(UserSetting.DONT_SEND_LVLUP);
                    builder.append("> You will no longer see any level up messages.");
                    break;
                case "noxp":
                    builder.append(toggleSetting(userObject, UserSetting.NO_XP_GAIN,
                            "> You will now gain Xp again.",
                            "> You will no longer gain XP"));
                    break;
                case "hiderank":
                    builder.append(toggleSetting(userObject, UserSetting.HIDE_RANK,
                            "> Your rank is now visible.",
                            "> Your rank is now hidden."));
                    break;
                case "nolevelupreactions":
                    builder.append(toggleSetting(userObject, UserSetting.NO_LEVEL_UP_REACTIONS,
                            "> You will now get reactions added to the message that leveled you up.",
                            "> You will no longer get reactions added to the message that leveled you up."));
                    break;
                case "privateprofile":
                    builder.append(toggleSetting(userObject, UserSetting.PRIVATE_PROFILE,
                            "> Your profile is now public.",
                            "> Your profile is now private."));
                default:
                    if (builder.length() == 0) {
                        return settings + "\n\nCurrent Guild Default: **" + EnumString.get(command.guild.config.getDefaultLevelMode()) + "**\n\n" +
                                Utility.getCommandInfo(this, command);
                    }
                    break;
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private String toggleSetting(ProfileObject user, UserSetting setting, String remove, String add) {
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

    private static void removeLevelSettings(ProfileObject user) {
        for (int i = 0; i < user.getSettings().size(); i++) {
            if (Constants.levelUpStates.contains(user.getSettings().get(i))) {
                user.getSettings().remove(i);
            }
        }
    }

    @Override
    public String[] names() {
        return new String[]{"ProfileSettings", "PixelSettings"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set where your level up messages will be sent.\n" + settings;
    }

    @Override
    public String usage() {
        return "(Setting...)";
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
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
