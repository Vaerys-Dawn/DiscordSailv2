package com.github.vaerys.commands.dmCommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.Help;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.DMCommand;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class HelpDM extends DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        List<Command> commands = Utility.getCommandsByType(Globals.getAllCommands(), command, SAILType.DM, true);
        List<String> list = new ArrayList<>();
        for (Command c : commands) {
            list.add(c.getCommand(command));
        }
        Collections.sort(list);
        StringBuilder desc = new StringBuilder("**> Direct Message Commands.**```" + Utility.listFormatter(list, false) + "```\n");
        desc.append(Utility.getCommandInfo(new InfoDM()));
        builder.withDescription(desc.toString());
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    protected String[] names() {
        return new Help().names;
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
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    public void init() {

    }
}
