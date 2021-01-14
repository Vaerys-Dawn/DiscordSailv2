package com.github.vaerys.commands.help;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

import java.text.NumberFormat;

/**
 * Created by Vaerys on 12/07/2017.
 */
public class Ping extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        NumberFormat nf = NumberFormat.getInstance();
        return String.format("\\> Pong!\nGateway: %sms\nRest: %sms",
                nf.format(command.client.get().getGatewayPing()),
                nf.format(command.client.get().getRestPing().complete()));
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
    protected Permission[] perms() {
        return new Permission[0];
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
