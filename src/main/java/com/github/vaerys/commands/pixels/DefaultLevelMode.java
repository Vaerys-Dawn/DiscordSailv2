package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/07/2017.
 */
public class DefaultLevelMode extends Command {

    private String settings = "**Settings:**\n" +
            "- LevelChannel\n" +
            "- CurrentChannel\n" +
            "- DMs\n" +
            "- NoMessage";;

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
    protected String[] names() {
        return new String[]{"DefaultLevelMode"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the default place that level up messages will be sent.\n" + settings;
    }

    @Override
    protected String usage() {
        return "[Setting]";
    }

    @Override
    protected SAILType type() {
        return SAILType.PIXEL;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
