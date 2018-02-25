package com.github.vaerys.commands.setup;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.pogos.GuildConfig;

public class SetupBack extends SetupCommand {

    protected static final String[] NAMES = new String[]{"back", "prev"};

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
}
