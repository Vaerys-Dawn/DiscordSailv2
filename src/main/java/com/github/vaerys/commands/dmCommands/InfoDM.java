package com.github.vaerys.commands.dmCommands;

import com.github.vaerys.commands.CommandList;
import com.github.vaerys.commands.help.Help;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
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
                    // if (!Utility.testForPerms(c.perms(), command.user.getAllToggles(), command.guild.getAllToggles())) {
                    // return error;
                    // }
                    RequestHandler.sendEmbedMessage("", c.getCommandInfo(command), command.channel.get());
                    return "";
                }
            }

        }
        return error;
        // List<Command> commands = Utility.getCommandsByType(Globals.getAll(), command, TYPE_DM,
        // true);
        //
        // for (Command c : commands) {
        // for (String s : c.names()) {
        // if (args.equalsIgnoreCase(s)) {
        // XEmbedBuilder infoEmbed = new XEmbedBuilder();
        //
        // //command info
        // StringBuilder builder = new StringBuilder();
        // builder.append("**" + Globals.defaultPrefixCommand + c.names()[0]);
        // if (c.usage() != null) {
        // builder.append(" " + c.usage());
        // }
        // builder.append("**\nDesc: **" + c.description() + "**\n");
        // builder.append("Type: **" + c.type() + "**\n");
        // if (c.type().equals(DMCommand.TYPE_CREATOR)) {
        // builder.append("**" + ownerOnly + "**");
        // }
        // infoEmbed.addField("> Info - " + c.names()[0], builder.toString(), false);
        //
        // //aliases
        // if (c.names().length > 1) {
        // StringBuilder aliasBuilder = new StringBuilder();
        // for (int i = 1; i < c.names().length; i++) {
        // aliasBuilder.append(Globals.defaultPrefixCommand + c.names()[i] + ", ");
        // }
        // aliasBuilder.delete(aliasBuilder.length() - 2, aliasBuilder.length());
        // aliasBuilder.append(".\n");
        // infoEmbed.addField("Aliases:", aliasBuilder.toString(), false);
        // }
        // Utility.sendEmbedMessage("", infoEmbed, command.channel.getAllToggles());
        // return "";
        // }
        // }
        // }
        // return "> Command with the name " + args + " not found.";
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

    }
}
