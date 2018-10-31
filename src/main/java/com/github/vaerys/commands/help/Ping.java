package com.github.vaerys.commands.help;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;

/**
 * Created by Vaerys on 12/07/2017.
 */
public class Ping extends Command {

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
