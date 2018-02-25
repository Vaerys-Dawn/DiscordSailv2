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

    @Override
    protected String[] names() {
        return new ClearReminder().names;
    }

    @Override
    public String description(CommandObject command) {
        return new ClearReminder().description(command);
    }

    @Override
    protected String usage() {
        return new ClearReminder().usage;
    }

    @Override
    protected SAILType type() {
        return SAILType.GENERAL;
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    public void init() {

    }
}
