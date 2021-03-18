package com.github.vaerys.commands.dmcommands;

import com.github.vaerys.commands.CommandList;
import com.github.vaerys.commands.help.Help;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.DMCommand;

import java.util.List;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class InfoDM extends DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        List<Command> commands = command.guild.getAllCommands(command);
        if (command.user.longID == command.client.creator.longID) {
            commands.addAll(CommandList.getCreatorCommands(true));
        }

        String error = "\\> Could not find information on any commands named **" + args + "**.";
        for (Command c : commands) {
            for (String s : c.names) {
                if (args.equalsIgnoreCase(s)) {
                    c.getCommandInfo(command).queue(command);
                    return "";
                }
            }

        }
        return error;
    }

    @Override
    protected String[] names() {
        return new Help().names;
    }

    @Override
    public String description(CommandObject command) {
        return "Tells you information about DM commands.";
    }

    @Override
    protected String usage() {
        return "[Command Name]";
    }

    @Override
    protected SAILType type() {
        return SAILType.HELP;
    }

    @Override
    protected boolean requiresArgs() {
        return true;
    }

    @Override
    public void init() {
        // does nothing
    }
}
