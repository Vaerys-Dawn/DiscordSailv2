package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;

/**
 * Created by Vaerys on 12/07/2017.
 */
public class Ping extends Command {

    protected static final String[] NAMES = new String[]{"Ping"};
    protected static final String USAGE = null;
    protected static final SAILType COMMAND_TYPE = SAILType.HELP;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    protected static final boolean REQUIRES_ARGS = false;
    protected static final boolean DO_ADMIN_LOGGING = false;

    @Override
    public String execute(String args, CommandObject command) {
        return "Pong! " + NumberFormat.getInstance().format(command.client.get().getShards().get(0).getResponseTime()) + "ms";
    }

    @Override
    protected String[] names() {
        return new String[]{"Ping"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sends a ping.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.HELP;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
