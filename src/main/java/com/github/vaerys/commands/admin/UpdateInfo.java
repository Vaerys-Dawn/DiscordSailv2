package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.InfoHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class UpdateInfo implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        List<IChannel> channels = command.guild.config.getChannelsByType(Command.CHANNEL_INFO, command.guild);
        if (channels.size() == 0) {
            return "> No Info channel set up yet, you need to set one up in order to run this command.\n" + Utility.getCommandInfo(this, command);
        }
        if (channels.get(0).getLongID() == command.channel.longID) {
            new InfoHandler(command.channel.get(), command.guild.get());
            return null;
        } else {
            return "> Command must be performed in " + channels.get(0).mention() + ".";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"UpdateInfo"};
    }

    @Override
    public String description() {
        return "Posts the contents of the Guild's Info.TXT";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return CHANNEL_INFO;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
