package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Restart extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        RequestHandler.sendMessage("> Restarting.", command.channel.get());
        Utility.sendGlobalAdminLogging(this, args, command);
        try {
            Thread.sleep(4000);
            Globals.getClient().logout();
            System.exit(Constants.EXITCODE_RESTART);
        } catch (DiscordException e) {
            Utility.sendStack(e);
        } catch (InterruptedException e) {
            Utility.sendStack(e);
        }
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"Restart"};
    }

    @Override
    public String description(CommandObject command) {
        return "Restarts the bot.\n" + ownerOnly;
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
    protected Permissions[] perms() {
        return new Permissions[0];
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
