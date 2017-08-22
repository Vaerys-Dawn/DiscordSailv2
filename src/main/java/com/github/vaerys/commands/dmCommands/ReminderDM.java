package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.general.RemindMe;
import com.github.vaerys.interfaces.DMCommand;

/**
 * Created by Vaerys on 19/04/2017.
 */
public class ReminderDM implements DMCommand{
    @Override
    public String execute(String args, CommandObject command) {
        return new RemindMe().execute(args,command);
    }

    @Override
    public String[] names() {
        return new RemindMe().names();
    }

    @Override
    public String description() {
        return new RemindMe().description();
    }

    @Override
    public String usage() {
        return new RemindMe().usage();
    }

    @Override
    public String type() {
        return TYPE_GENERAL;
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }
}
