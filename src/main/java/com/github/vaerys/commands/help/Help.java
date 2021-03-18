package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandList;
import com.github.vaerys.commands.dmcommands.InfoDM;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vaerys on 29/01/2017.
 */
public class Help extends Command {

    @Override
    public String execute(String args, CommandObject command) {

        if (args == null || args.isEmpty()) {
            return "\\> If you are after a list of commands please run **" + new Commands().getUsage(command) + "** instead.\n\n" +
                    missingArgs(command);
        }

        List<Command> commands = command.guild.getAllCommands(command);
        if (command.user.longID == command.client.creator.longID) {
            commands.addAll(CommandList.getCreatorCommands(false));
        }

        Command foundCommand = null;
        for (Command c : commands) {
            if (c.isName(args, command)) {
                foundCommand = c;
            }
        }

        if (foundCommand == null) return "\\> Could not find information on any commands named **" + args + "**.";

        if (!GuildHandler.testForPerms(command, foundCommand.perms))
            return "\\> I'm sorry but you do not have permission to view the information for the **" + foundCommand.getCommand(command) + "** command.";
        foundCommand.getCommandInfo(command).queue(command);
        return null;
    }

    @Override
    public String executeDm(String commandArgs, DmCommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder();
        List<Command> commands = CommandList.getCommands(true);
        if (command.globalUser.checkIsCreator()) commands.addAll(CommandList.getCreatorCommands(true));
        List<String> list = new ArrayList<>();
        for (Command c : commands) {
            list.add(c.getCommand());
        }
        Collections.sort(list);
        StringBuilder desc = new StringBuilder("**\\> Direct Message Commands.**```" + Utility.listFormatter(list, false) + "```\n");
        desc.append(get(InfoDM.class).missingArgsDm());
        builder.setDescription(desc.toString());
        builder.queue(command);
        return null;
    }

    @Override
    protected boolean hasDmVersion() {
        return true;
    }

    @Override
    protected String[] names() {
        return new String[]{"Help"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives information about a command.";
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
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
