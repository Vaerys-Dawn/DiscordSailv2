package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.interfaces.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/07/2017.
 */
public class DefaultLevelMode implements Command {
    private String settings = "**Settings:**\n" +
            "- LevelChannel\n" +
            "- CurrentChannel\n" +
            "- DMs\n" +
            "- NoMessage";

    @Override
    public String execute(String args, CommandObject command) {
        String message = "> Level messages will now be sent to ";
        switch (args.toLowerCase()) {
            case "levelchannel":
                command.guild.config.setDefaultLevelMode(UserSetting.SEND_LVLUP_RANK_CHANNEL);
                return message + "the server's level up channel.";
            case "currentchannel":
                command.guild.config.setDefaultLevelMode(UserSetting.SEND_LVLUP_CURRENT_CHANNEL);
                return message + "the current channel.";
            case "dms":
                command.guild.config.setDefaultLevelMode(UserSetting.SEND_LVLUP_DMS);
                return message + "the user's Direct messages.";
            case "nomessage":
                command.guild.config.setDefaultLevelMode(UserSetting.DONT_SEND_LVLUP);
                return "> Now set to now send level up messages";
            default:
                return "> Not a valid option.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"DefaultLevelMode"};
    }

    @Override
    public String description() {
        return "Allows you to set the default place that level up messages will be sent.\n" + settings;
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
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
