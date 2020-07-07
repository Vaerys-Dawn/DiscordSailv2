package com.github.vaerys.commands.general;

import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class Invite extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        String user = command.user.displayName;
        String bot = command.client.bot.displayName;
        return String.format("\\> Hello %s, %s is currently still in beta and as such inviting the bot to your server " +
                "requires Developer help.\n\nIf you wish to get %s on your server you will need to " +
                "contact %s on the support server which you can find here: <https://discord.gg/XSyQQrR>",
                user, bot, bot, Globals.getCreator().getName());
    }

    @Override
    public String[] names() {
        return new String[]{"Invite"};
    }

    @Override
    public String description(CommandObject command) {
        return "Tells you how to invite this bot to your server.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public SAILType type() {
        return SAILType.GENERAL;
    }

    @Override
    public ChannelSetting channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permission[0];
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
}