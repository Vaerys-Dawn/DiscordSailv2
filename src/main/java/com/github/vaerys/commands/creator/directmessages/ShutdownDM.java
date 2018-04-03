package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.commands.creator.Shutdown;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.DMCommand;


/**
 * Created by Vaerys on 23/04/2017.
 */
public class ShutdownDM extends DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        return get(Shutdown.class).execute(args, command);
    }

    @Override
    protected String[] names() {
        return new String[]{"Shutdown"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shuts the bot down.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    public void init() {

    }
}
