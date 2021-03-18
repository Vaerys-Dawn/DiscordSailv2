package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.ChannelObject;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.masterobjects.GlobalUserObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Shutdown extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        sendShutdown(args, command.guildChannel, command.user);
        return null;
    }

    @Override
    public String executeDm(String args, DmCommandObject command) {
        sendShutdown(args, command.messageChannel, command.globalUser);
        return null;
    }

    private void sendShutdown(String args, ChannelObject channel, GlobalUserObject user) {
        channel.sendMessage("\\> Shutting Down.");
        Utility.sendGlobalAdminLogging(this, args, user);
        try {
            Thread.sleep(4000);
            Client.getClient().shutdown();
            System.exit(Constants.EXITCODE_STOP);
        } catch (InterruptedException e) {
            Utility.sendStack(e);
        }
    }

    @Override
    protected boolean hasDmVersion() {
        return true;
    }

    @Override
    protected String[] names() {
        return new String[]{"Shutdown"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shuts the bot down safely.\n" + OWNER_ONLY;
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
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
        return true;
    }

    @Override
    public void init() {

    }
}
