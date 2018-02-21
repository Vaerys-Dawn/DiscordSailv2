package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.general.ClearReminder;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.DMCommand;

/**
 * Created by Vaerys on 19/04/2017.
 */
public class ClearReminderDM extends DMCommand{
    @Override
    public String execute(String args, CommandObject command) {
        return new ClearReminder().execute(args,command);
    }

    protected static final String[] NAMES = new ClearReminder().names;
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return new ClearReminder().description(command);
    }

    protected static final String USAGE = new ClearReminder().usage;
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.GENERAL;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    public void init() {

    }
}
