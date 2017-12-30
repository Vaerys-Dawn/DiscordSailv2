package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.templates.Command;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class SilentReport extends Report implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        if (command.channel.get().getTypingStatus()) {
            command.channel.get().toggleTypingStatus();
        }
        RequestHandler.deleteMessage(command.message.get());
        RequestHandler.sendMessage(Report.report(args, command, true), command.user.get().getOrCreatePMChannel());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"SilentReport"};
    }

    @Override
    public String description(CommandObject command) {
        return "Can be used to send a user report to the server staff.\n" +
                indent + " It will also remove the message used to call the command.";
    }

    @Override
    public String usage() {
        return "[@User] [Report]";
    }
}
