package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.setup.SetupBack;
import com.github.vaerys.commands.setup.SetupNext;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class SetupWizard extends Command {

    protected static final String[] NAMES = new String[] {"SetupWizard", "StartupWizard"};
    protected static final SAILType COMMAND_TYPE = SAILType.SETUP;
    protected static final boolean REQUIRES_ARGS = false;
    protected static final boolean DO_ADMIN_LOGGING = true;
    protected static final Permissions[] PERMISSIONS = {Permissions.MANAGE_SERVER};

    @Override
    public String execute(String args, CommandObject command) {
        // Lots of sanity checking.
        if (SetupHandler.isRunningSetup(command.guild)) {
            return "> Setup is already running for this guild. I can't run it twice!";
        }

        // user can only run setup for one guild at a time
        UserObject user = command.user;
        for (GuildObject guildObject : Globals.getGuilds()) {
            if (guildObject.config.setupUser == user.longID) {
                return "> You're running setup for a different guild. I can't do more than one at a time.";
            }
        }

        // Handle Setup beginning
        String message = "Hi! I'm " + command.client.bot.displayName + " and I'm here to help you get your server set up.\n" +
                "This guide will walk you through a bunch of commands and things you can do to make your server exactly the way you want it.\n\n" +
                "You can navigate forwards and backwards in this setup with **" + new SetupBack().getUsage(command) +
                "** and **" + new SetupNext().getUsage(command) + "**";

        if (command.user.sendDm(message) == null) {
            return "> **ERROR**: I wasn't able to send you a DM! Check your server privacy settings!";
        }

        // send first setup stage response
        SetupHandler.setSetupStage(command, 0);

        return "> Check your DMs for more instructions.";
    }

    @Override
    public String[] names() {
        return new String[]{"SetupWizard", "StartupWizard"};
    }

    @Override
    public String description(CommandObject command) {
        return "Used to help new server admins configure all of the features available to SAIL.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    public ChannelSetting channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    public boolean requiresArgs() {
    return REQUIRES_ARGS;
    }

    @Override
    public boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
