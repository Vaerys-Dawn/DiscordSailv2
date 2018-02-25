package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.general.ClearReminder;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.DMCommand;

/**
 * Created by Vaerys on 19/04/2017.
 */
public class ClearReminderDM extends DMCommand {
    protected static final String[] NAMES = new ClearReminder().names;
    protected static final String USAGE = new ClearReminder().usage;
    protected static final SAILType COMMAND_TYPE = SAILType.GENERAL;
    protected static final boolean REQUIRES_ARGS = false;

    @Override
    public String execute(String args, CommandObject command) {
        return new ClearReminder().execute(args, command);
    }

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return new ClearReminder().description(command);
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    public void init() {

    }
}
