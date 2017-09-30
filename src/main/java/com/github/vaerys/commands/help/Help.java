package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Vaerys on 29/01/2017.
 */


public class Help implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder helpEmbed = new XEmbedBuilder();
        List<String> types = new ArrayList<>(command.guild.getAllTypes(command));
        List<Command> commands = new ArrayList<>(command.guild.getAllCommands(command));
        String error = "> There are no commands with the type: **" + args + "**.\n\n" + Utility.getCommandInfo(this, command);
        helpEmbed.withColor(command.client.color);
        ListIterator iterator = types.listIterator();
        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            if (Utility.getCommandsByType(commands, command, s, true).size() == 0) {
                iterator.remove();
            }
        }
        Collections.sort(types);
        if (args == null || args.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append(codeBlock + "\n");
            builder.append(Utility.listFormatter(types, false));
            builder.append(codeBlock + "\n");
            helpEmbed.withTitle("Here are the Command Types I have available for use:");
            builder.append(Utility.getCommandInfo(this, command) + "\n");
            helpEmbed.withDescription(builder.toString());
            Utility.sendEmbedMessage("", helpEmbed, command.channel.get());
            return null;
        } else {
            for (String s : types) {
                if (s.equalsIgnoreCase(args)) {
                    StringBuilder builder = new StringBuilder();
                    String suffix = Utility.getCommandInfo(new Info(), command);
                    helpEmbed.withTitle("> Here are all of the " + s + " Commands I have available.");
                    if (args.equalsIgnoreCase(TYPE_DM)) {
                        suffix = "**These commands can only be performed in DMs.**\n" +
                                "> If you send a non command message to my DMs it will send it to my creator.\n\n" + suffix;
                    } else if (args.equalsIgnoreCase(TYPE_CREATOR)) {
                        suffix = "**Only the creator of this bot can run these commands.**\n\n" + suffix;
                    }
                    builder.append(codeBlock + "\n");
                    List<String> commandNames = new ArrayList<>();
                    for (Command c : Utility.getCommandsByType(commands, command, s, true)) {
                        StringBuilder commandCall = new StringBuilder(c.getCommand(command));
                        if (c.dualType() != null) {
                            commandCall.append(indent + "*");
                        }
                        commandNames.add(commandCall.toString());
                    }
                    builder.append(Utility.listFormatter(commandNames, false));
                    builder.append(codeBlock + "\n");
                    builder.append(suffix);
                    helpEmbed.withDescription(builder.toString());
                    Utility.sendEmbedMessage("", helpEmbed, command.channel.get());
                    return null;
                }
            }
            return error;
        }
    }

    @Override
    public String[] names() {
        return new String[]{"Help"};
    }

    @Override
    public String description(CommandObject command) {
        return "Lists the commands that profiles can run.";
    }

    @Override
    public String usage() {
        return "(Command Type)";
    }

    @Override
    public String type() {
        return TYPE_HELP;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
