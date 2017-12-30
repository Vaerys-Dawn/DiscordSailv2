package com.github.vaerys.commands.creator;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Shutdown implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        RequestHandler.sendMessage("> Shutting Down.", command.channel.get());
        Utility.sendGlobalAdminLogging(this, args, command);
        try {
            Thread.sleep(4000);
            Globals.getClient().logout();
            System.exit(1);
        } catch (DiscordException e) {
            Utility.sendStack(e);
        } catch (InterruptedException e) {
            Utility.sendStack(e);
        }
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Shutdown"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shuts the bot down safely.\n" + ownerOnly;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
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
