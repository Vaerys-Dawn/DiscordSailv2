package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.creator.Shutdown;
import com.github.vaerys.templates.DMCommand;


/**
 * Created by Vaerys on 23/04/2017.
 */
public class ShutdownDM extends DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        return new Shutdown().execute(args,command);
    }

    @Override
    public String[] names() {
        return new String[]{"Shutdown"};
    }

    @Override
    public String description(CommandObject command) {
        return "Shuts the bot down.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public void init() {

    }
}
