package com.github.vaerys.commands.setup;

import com.github.vaerys.handlers.SetupHandler;
import com.github.vaerys.handlers.SetupHandler.SetupStage;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.SetupCommand;

public class SetupRepeat extends SetupCommand {

    @Override
    public String execute(String args, CommandObject command) {
        SetupStage stage = command.guild.config.setupStage;
        SetupHandler.setSetupStage(command, stage);
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"repeat"};
    }

    @Override
    public String description(CommandObject command) {
        return "Repeat the current setup step.";
    }
}
