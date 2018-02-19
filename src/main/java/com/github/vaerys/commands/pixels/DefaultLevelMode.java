package com.github.vaerys.commands.pixels;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
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

    protected static final String[] NAMES = new String[]{"DefaultLevelMode"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to set the default place that level up messages will be sent.\n" + settings;
    }

    protected static final String USAGE = "[Setting]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.PIXEL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
