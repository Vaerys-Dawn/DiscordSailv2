package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.templates.DMCommand;
import net.dv8tion.jda.api.entities.User;

public class Echo extends DMCommand {

    @Override
    public String executeDm(String args, DmCommandObject command) {
        User recipient = command.client.get().getUserById(Globals.lastDmUserID);
        if (recipient != null) {
            return Respond.sendDM(args, command, recipient, "\\> ");
        } else {
            return "\\> no globalUser to respond to.";
        }
    }

    @Override
    protected String[] names() {
        return new String[]{"Echo", "E"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sail sends a response to the most recent globalUser";
    }

    @Override
    protected String usage() {
        return "[Message]";
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
