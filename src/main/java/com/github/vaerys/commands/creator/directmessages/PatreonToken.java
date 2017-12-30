package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.DMCommand;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

public class PatreonToken implements DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        FileHandler.writeToFile(Constants.FILE_PATREON_TOKEN, args, true);
        Client.initPatreon(new ArrayList<String>() {{
            add(args);
        }});
        Client.checkPatrons();
        return "> Token refreshed.";
    }

    @Override
    public String[] names() {
        return new String[]{"PatreonToken"};
    }

    @Override
    public String description(CommandObject command) {
        return "refreshes the token for the patreon integration.";
    }

    @Override
    public String usage() {
        return "[New Token]";
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }
}