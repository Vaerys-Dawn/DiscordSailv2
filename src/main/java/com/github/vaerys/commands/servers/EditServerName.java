package com.github.vaerys.commands.servers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class EditServerName extends Command {
    protected static final String[] NAMES = new String[]{"EditServerName"};
    protected static final String USAGE = "[Server Name] [New Server Name]";
    protected static final SAILType COMMAND_TYPE = SAILType.SERVERS;
    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.SERVERS;
    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    protected static final boolean REQUIRES_ARGS = true;
    protected static final boolean DO_ADMIN_LOGGING = false;

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
        if (splitArgs.size() < 2) {
            return "> Cannot Edit Server Name as no new name was specified.";
        }
        return command.guild.servers.editServerName(command.user.longID, splitArgs.get(0), splitArgs.get(1), command.guild.get());
    }

    @Override
    protected String[] names() {
        return new String[]{"EditServerName"};
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to edit your server name.";
    }

    @Override
    protected String usage() {
        return "[Server Name] [New Server Name]";
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
    protected Permissions[] perms() {
        return new Permissions[0];
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
