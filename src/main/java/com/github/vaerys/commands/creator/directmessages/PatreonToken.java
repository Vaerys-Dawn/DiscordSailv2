package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.templates.DMCommand;

import java.util.ArrayList;

public class PatreonToken extends DMCommand {

    @Override
    public String executeDm(String args, DmCommandObject command) {
        FileHandler.writeToFile(Constants.FILE_PATREON_TOKEN, args, true);
        Client.initPatreon(new ArrayList<String>() {{
            add(args);
        }});
        Client.checkPatrons();
        return "\\> Token refreshed.";
    }

    @Override
    protected String[] names() {
        return new String[]{"PatreonToken"};
    }

    @Override
    public String description(CommandObject command) {
        return "refreshes the token for the patreon integration.";
    }

    @Override
    protected String usage() {
        return "[New Token]";
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    public void init() {

    }
}
