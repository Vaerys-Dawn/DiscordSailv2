package com.github.vaerys.commands.admin;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.InfoHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

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

    @Override
    protected String[] names() {
        return new String[]{"UpdateInfo"};
    }

    @Override
    public String description(CommandObject command) {
        return "Posts the contents of the Guild's Info.TXT";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.INFO;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

    }
}
