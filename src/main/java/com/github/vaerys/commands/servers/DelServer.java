package com.github.vaerys.commands.servers;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class DelServer extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        return command.guild.servers.deleteServer(command.user.get().getLongID(), args, command.guild.get());
    }

    @Override
    protected String[] names() {
        return new String[]{"DelServer"};
    }

    @Override
    public String description(CommandObject command) {
        return "Removes a server from the guild's server list.";
    }

    @Override
    protected String usage() {
        return "[Server Name]";
    }

    @Override
    protected SAILType type() {
        return SAILType.SERVERS;
    }

    @Override
    protected ChannelSetting channel() {
        return ChannelSetting.SERVERS;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
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
