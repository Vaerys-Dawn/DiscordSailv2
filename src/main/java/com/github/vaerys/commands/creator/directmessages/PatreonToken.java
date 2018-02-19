package com.github.vaerys.commands.creator.directmessages;

import java.util.ArrayList;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.DMCommand;

public class PatreonToken extends DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        FileHandler.writeToFile(Constants.FILE_PATREON_TOKEN, args, true);
        Client.initPatreon(new ArrayList<String>() {{
            add(args);
        }});
        Client.checkPatrons();
        return "> Token refreshed.";
    }

    protected static final String[] NAMES = new String[]{"PatreonToken"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "refreshes the token for the patreon integration.";
    }

    protected static final String USAGE = "[New Token]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CREATOR;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    public void init() {

    }
}