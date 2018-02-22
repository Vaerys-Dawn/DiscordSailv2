package com.github.vaerys.commands.setup;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public class SetupBack extends Command {

    protected static final SAILType COMMAND_TYPE = SAILType.SETUP;
    protected static final String[] NAMES = new String[]{"back", "prev"};
    protected static final boolean REQUIRES_ARGS = false;
    protected static final boolean DO_ADMIN_LOGGING = false;

    @Override
    public String execute(String args, CommandObject command) {
        GuildConfig config = command.guild.config;
        if (!SetupHandler.isRunningSetup(command.guild)) return "> You aren't running setup you nincompoop.";

        // check if underflow
        if (--config.setupStage < 0) {
            // notify user of trying to go out of bounds.
            config.setupStage = 0;
            return "> You are already on the first step.";
        }

        // "log" step reversal.
        RequestHandler.sendMessage("> Going back a step...", command.channel);
        SetupHandler.setSetupStage(command, config.setupStage);
        return null;
    }

    @Override
    public String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Move to the previous setup step.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public SAILType type() { return COMMAND_TYPE; }

    @Override
    public ChannelSetting channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
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
