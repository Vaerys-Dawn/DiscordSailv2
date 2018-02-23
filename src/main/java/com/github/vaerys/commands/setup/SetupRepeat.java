package com.github.vaerys.commands.setup;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.SetupHandler;

public class SetupRepeat extends SetupCommand {

    @Override
    public String execute(String args, CommandObject command) {
        int currentStage = command.guild.config.setupStage;
        SetupHandler.setSetupStage(command, currentStage);
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"repeat"};
    }

    @Override
    public String description(CommandObject command) {
        return null;
    }
}
