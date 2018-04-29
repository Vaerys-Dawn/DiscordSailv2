package com.github.vaerys.commands.setup;

import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.handlers.SetupHandler.SetupStage;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.SetupCommand;

public class SetupBack extends SetupCommand {

    @Override
    public String execute(String args, CommandObject command) {
        GuildConfig config = command.guild.config;
        if (!SetupHandler.isRunningSetup(command.guild)) return "> You aren't running setup you nincompoop.";

        // check if out of bounds.
        try {
            SetupStage prevStage = SetupStage.getPrevStage(command.guild.config.setupStage);
            if (prevStage == SetupStage.SETUP_MODULES && command.guild.config.setupStage == SetupStage.SETUP_MODULES) {
                return "> You are already *on* the first step you pillock";
            }
            SetupHandler.setSetupStage(command, prevStage);
            return "> Going back a step.";
        } catch (ArrayIndexOutOfBoundsException e) {
            // stop them from actually breaking shit...
            config.setupStage = SetupStage.SETUP_UNSET;
            return "> `ERROR: ArrayIndexOutOfBoundsException`... *cough*\n" +
                    "You broke it. Now I have to cancel the setup because of you. This shouldn't have even been possible. *angry bot noises*";
        }
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
