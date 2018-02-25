package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.List;

/**
 * Created by Vaerys on 29/01/2017.
 */
public class Info extends Command {

    @Override
    public String execute(String args, CommandObject command) {

        if (args == null || args.isEmpty()) {
            return "> If you are after a list of commands please run **" + new Help().getUsage(command) + "** instead.\n\n" +
                    missingArgs(command);
        }

        List<Command> commands = command.guild.getAllCommands(command);
        if (command.user.longID == command.client.creator.longID) {
            commands.addAll(Globals.getCreatorCommands(false));
        }

        String error = "> Could not find information on any commands named **" + args + "**.";
        for (Command c : commands) {
            for (String s : c.names) {
                if (args.equalsIgnoreCase(s) || args.equalsIgnoreCase(command.guild.config.getPrefixCommand() + s)) {
                    if (!GuildHandler.testForPerms(command, c.perms)) {
                        return error;
                    }
                    RequestHandler.sendEmbedMessage("", c.getCommandInfo(command), command.channel.get());
                    return "";
                }
            }

        }
        return error;
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
    protected Permissions[] perms() {
        return new Permissions[0];
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
