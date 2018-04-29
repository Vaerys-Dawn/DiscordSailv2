package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.CommandList;
import com.github.vaerys.commands.help.Commands;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.DMCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class HelpDM extends DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        List<Command> commands = Utility.getCommandsByType(CommandList.getAll(), command, SAILType.DM, true);
        List<String> list = new ArrayList<>();
        for (Command c : commands) {
            list.add(c.getCommand(command));
        }
        Collections.sort(list);
        StringBuilder desc = new StringBuilder("**> Direct Message Commands.**```" + Utility.listFormatter(list, false) + "```\n");
        desc.append(get(InfoDM.class).missingArgs(command));
        builder.withDescription(desc.toString());
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    protected String[] names() {
        return new Commands().names;
    }

    @Override
    public String description(CommandObject command) {
        return "Lists DM Commands.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.HELP;
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    public void init() {

    }
}
