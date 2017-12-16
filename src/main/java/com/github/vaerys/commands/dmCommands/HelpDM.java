package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.Help;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.DMCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class HelpDM implements DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder();
        builder.withColor(command.client.color);
        List<Command> commands = Utility.getCommandsByType(Globals.getAllCommands(), command, TYPE_DM, true);
        List<String> list = new ArrayList<>();
        for (Command c : commands) {
            list.add(c.getCommand(command));
        }
        Collections.sort(list);
        StringBuilder desc = new StringBuilder("**> Direct Message Commands.**```" + Utility.listFormatter(list, false) + "```\n");
        desc.append(Utility.getCommandInfo(new InfoDM()));
        builder.withDescription(desc.toString());
        Utility.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new Help().names();
    }

    @Override
    public String description(CommandObject command) {
        return "Lists DM Commands.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_HELP;
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }
}
