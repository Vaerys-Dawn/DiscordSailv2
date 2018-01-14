package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.Info;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.DMCommand;

import java.util.List;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class InfoDM implements DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        List<Command> commands = command.guild.getAllCommands(command);
        if (command.user.longID == command.client.creator.longID) {
            commands.addAll(Globals.getCreatorCommands(true));
        }

        String error = "> Could not find information on any commands named **" + args + "**.";
        for (Command c : commands) {
            for (String s : c.names()) {
                if (args.equalsIgnoreCase(s)) {
//                    if (!Utility.testForPerms(c.perms(), command.user.get(), command.guild.get())) {
//                        return error;
//                    }
                    RequestHandler.sendEmbedMessage("", c.getCommandInfo(command), command.channel.get());
                    return "";
                }
            }

        }
        return error;
//        List<Command> commands = Utility.getCommandsByType(Globals.getAllCommands(), command, TYPE_DM, true);
//
//        for (Command c : commands) {
//            for (String s : c.names()) {
//                if (args.equalsIgnoreCase(s)) {
//                    XEmbedBuilder infoEmbed = new XEmbedBuilder();
//
//                    //command info
//                    StringBuilder builder = new StringBuilder();
//                    builder.append("**" + Globals.defaultPrefixCommand + c.names()[0]);
//                    if (c.usage() != null) {
//                        builder.append(" " + c.usage());
//                    }
//                    builder.append("**\nDesc: **" + c.description() + "**\n");
//                    builder.append("Type: **" + c.type() + "**\n");
//                    if (c.type().equals(DMCommand.TYPE_CREATOR)) {
//                        builder.append("**" + ownerOnly + "**");
//                    }
//                    infoEmbed.appendField("> Info - " + c.names()[0], builder.toString(), false);
//
//                    //aliases
//                    if (c.names().length > 1) {
//                        StringBuilder aliasBuilder = new StringBuilder();
//                        for (int i = 1; i < c.names().length; i++) {
//                            aliasBuilder.append(Globals.defaultPrefixCommand + c.names()[i] + ", ");
//                        }
//                        aliasBuilder.delete(aliasBuilder.length() - 2, aliasBuilder.length());
//                        aliasBuilder.append(".\n");
//                        infoEmbed.appendField("Aliases:", aliasBuilder.toString(), false);
//                    }
//                    Utility.sendEmbedMessage("", infoEmbed, command.channel.get());
//                    return "";
//                }
//            }
//        }
//        return "> Command with the name " + args + " not found.";
    }

    @Override
    public String[] names() {
        return new Info().names();
    }

    @Override
    public String description(CommandObject command) {
        return "Tells you information about DM commands.";
    }

    @Override
    public String usage() {
        return "[Command Name]";
    }

    @Override
    public String type() {
        return TYPE_HELP;
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }
}
