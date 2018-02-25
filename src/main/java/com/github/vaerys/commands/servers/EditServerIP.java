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
public class EditServerIP extends Command {
    protected static final String[] NAMES = new String[]{"EditServerIP"};
    protected static final String USAGE = "[Server Name] [IP] (Port)";
    protected static final SAILType COMMAND_TYPE = SAILType.SERVERS;
    protected static final ChannelSetting CHANNEL_SETTING = ChannelSetting.SERVERS;
    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    protected static final boolean REQUIRES_ARGS = true;
    protected static final boolean DO_ADMIN_LOGGING = false;

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
        String port = "N/a";
        if (splitArgs.size() > 2) {
            port = splitArgs.get(2);
        }
        if (splitArgs.size() < 2) {
            return "> Cannot edit server IP, missing arguments.";
        }
        return command.guild.servers.editIP(command.user.longID, splitArgs.get(0), splitArgs.get(1), port, command.guild.get());
    }

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Allows you to edit your server IP and Port.";
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
