package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.creator.Shutdown;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.DMCommand;


/**
 * Created by Vaerys on 23/04/2017.
 */
public class ShutdownDM extends DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        return new Shutdown().execute(args,command);
    }

    protected static final String[] NAMES = new String[]{"Shutdown"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Shuts the bot down.";
    }

    protected static final String USAGE = null;
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CREATOR;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
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
