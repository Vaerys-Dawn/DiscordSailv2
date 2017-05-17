package Commands.Help;

import Commands.CommandObject;
import Interfaces.Command;
import Main.Globals;
import Main.Utility;
import Objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 29/01/2017.
 */


public class Help implements Command {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> types = command.commandTypes;
        XEmbedBuilder helpEmbed = new XEmbedBuilder();
        StringBuilder builder = new StringBuilder();
        ArrayList<String> commandList = new ArrayList<>();
        ArrayList<Command> commands = command.commands;
        String error = "> There are no commands with the type: " + args + ".\n" + Utility.getCommandInfo(this, command);
        //setting embed colour to match Bot's Colour
        Color color = Utility.getUsersColour(Globals.getClient().getOurUser(), command.guild);
        if (color != null) {
            helpEmbed.withColor(color);
        }

        //getting Types of commands.

        if (!command.authorSID.equalsIgnoreCase(Globals.creatorID)) {
            for (int i = 0; i < types.size(); i++) {
                if (types.get(i).equals(TYPE_CREATOR)) {
                    types.remove(i);
                }
            }
        }

        //sort types
        Collections.sort(types);

        //building the embed
        if (args.isEmpty()) {
            builder.append(codeBlock + "\n");
            builder.append(Utility.listFormatter(types, false));
            builder.append(codeBlock + "\n");
            String desc = "[Suport Sail on Patreon](https://www.patreon.com/DawnFelstar)\n" +
                    "[Find Sail on GitHub](https://github.com/Vaerys-Dawn/DiscordSailv2)";
            if (!command.guildSID.equals("206792432560373761")) {
                desc += "\nSupport Discord - https://discord.gg/XSyQQrR";
            }

            helpEmbed.withTitle("Here are the Command Types I have available for use:");
            builder.append(Utility.getCommandInfo(this, command) + "\n");
            helpEmbed.withDescription(builder.toString());
            helpEmbed.appendField("Helpful Links", desc, true);
            helpEmbed.withFooterText("Bot Version: " + Globals.version);
        } else {
            boolean isFound = false;
            String title = "ERROR";
            String suffix = Utility.getCommandInfo(new Info(), command);
            for (String s : types) {
                if (args.equalsIgnoreCase(s) || args.equalsIgnoreCase(command.guildConfig.getPrefixCommand() + s)) {
                    title = "> Here are all of the " + s + " Commands I have available.";
                    isFound = true;
                    if (s.equalsIgnoreCase(TYPE_DM)) {
                        commandList.addAll(Globals.getCommandsDM().stream().map(c -> Globals.defaultPrefixCommand + c.names()[0]).collect(Collectors.toList()));
                        suffix = "**These commands can only be performed in DMs.**\n" +
                                "> If you send a non command message to my DMs it will send it to my creator.";
                    } else {
                        for (Command c : commands) {
                            if (c.type().equalsIgnoreCase(s)) {
                                if (c.dualType() != null) {
                                    commandList.add(command.guildConfig.getPrefixCommand() + c.names()[0] + indent + "*");
                                } else {
                                    commandList.add(command.guildConfig.getPrefixCommand() + c.names()[0]);
                                }
                            }
                        }
                    }
                }
            }
            Collections.sort(commandList);
            Utility.listFormatterEmbed(title, helpEmbed, commandList, false, suffix);
            if (!isFound) {
                return error;
            }
        }
        Utility.sendEmbedMessage("", helpEmbed, command.channel);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Help"};
    }

    @Override
    public String description() {
        return "Gives information about Sail, including the commands it can run.";
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
