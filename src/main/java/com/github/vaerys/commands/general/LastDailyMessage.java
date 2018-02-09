package com.github.vaerys.commands.general;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.creator.DailyMsg;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.DailyMessage;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class LastDailyMessage extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        DailyMessage messageObject = command.guild.config.getLastDailyMessage();
        if (messageObject != null) {
            RequestHandler.sendEmbedMessage("", new DailyMsg().getInfo(messageObject, command), command.channel.get());
            return null;
        } else return "> It appears that there have been no daily messages stored.";
    }

    @Override
    public String[] names() {
        return new String[]{"LastDailyMessage", "LastDailyMsg"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives the information of the last Daily message that was sent to this server.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
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
        return false;
    }

    @Override
    public void init() {

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