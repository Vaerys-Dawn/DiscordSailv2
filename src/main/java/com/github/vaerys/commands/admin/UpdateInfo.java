package com.github.vaerys.commands.admin;

import java.util.List;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.InfoHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class UpdateInfo extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        List<IChannel> channels = command.guild.getChannelsByType(ChannelSetting.INFO);
        if (channels.size() == 0) {
            return "> No Info channel set up yet, you need to set one up in order to run this command.\n" + Utility.getCommandInfo(this, command);
        }
        if (channels.get(0).getLongID() == command.channel.longID) {
            new InfoHandler(command);
            return null;
        } else {
            return "> Command must be performed in " + channels.get(0).mention() + ".";
        }
    }

    protected static final String[] NAMES = new String[]{"UpdateInfo"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Posts the contents of the Guild's Info.TXT";
    }

    protected static final String USAGE = null;
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.INFO;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = true;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
