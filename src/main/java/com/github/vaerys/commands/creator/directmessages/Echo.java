package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.templates.DMCommand;
import sx.blah.discord.handle.obj.IUser;

public class Echo implements DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        IUser recipient = command.client.get().getUserByID(Globals.lastDmUserID);
        if (recipient != null){
            return Respond.sendDM(args,command,recipient,"> ");
        }else {
            return "> no user to respond to.";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Echo","E"};
    }

    @Override
    public String description(CommandObject command) {
        return "Sail sends a response to the most recent user";
    }

    @Override
    public String usage() {
        return "[Message]";
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
