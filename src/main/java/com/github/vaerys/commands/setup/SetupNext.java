package com.github.vaerys.commands.setup;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.handlers.SetupHandler.SetupStage;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.SetupCommand;

public class SetupNext extends SetupCommand {

    @Override
    public String execute(String args, CommandObject command) {
        GuildConfig config = command.guild.config;
        if (!SetupHandler.isRunningSetup(command.guild)) return "> You aren't running setup you nincompoop.";

        // check if out of bounds.
        try {
            // getAllCommands next ordinal value
            SetupStage next = SetupStage.values()[config.setupStage.ordinal() + 1];
            if (next == SetupStage.SETUP_COMPLETE) {
                config.setupStage = SetupStage.SETUP_COMPLETE;
                return "> Congratulations! You're all done. Everything should be perfectly set up just the way you want it.";
            }
            // move to next stage
            SetupHandler.setSetupStage(command, next);
        } catch (ArrayIndexOutOfBoundsException e) {
            // stop them from actually breaking shit...
            config.setupStage = SetupStage.SETUP_COMPLETE;
            return "> Congratulations! You're all done. Everything should be perfectly set up just the way you want it.";
        }

        // "log" step change.
        SetupHandler.setSetupStage(command, config.setupStage);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"next", "done"};
    }

    @Override
    public String description(CommandObject command) {
        return "Move to the next setup step";
    }

}
