package com.github.vaerys.commands.setup;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.pogos.GuildConfig;

public class SetupNext extends SetupCommand {

    protected static final String[] NAMES = new String[]{"next", "done"};

    @Override
    public String execute(String args, CommandObject command) {
        GuildConfig config = command.guild.config;
        if (!SetupHandler.isRunningSetup(command.guild)) return "> You aren't running setup you nincompoop.";

        // check if underflow
        if (++config.setupStage > SetupHandler.getNumSetupSteps()) {
            // notify user of trying to go out of bounds.
            config.setupStage = SetupHandler.SETUP_COMPLETE;
            return "> Congratulations! You're all done. Everything should be perfectly set up just the way you want it.";
        }

        // "log" step reversal.
        SetupHandler.setSetupStage(command, config.setupStage);
        return null;
    }

    @Override
    public String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Move to the next setup step";
    }

}
