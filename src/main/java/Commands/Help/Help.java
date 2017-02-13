package Commands.Help;

import Commands.Command;
import Commands.CommandObject;
import Main.Globals;
import Main.Utility;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

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
        ArrayList<String> types = new ArrayList<>();
        EmbedBuilder helpEmbed = new EmbedBuilder();
        StringBuilder builder = new StringBuilder();
        ArrayList<String> commandList = new ArrayList<>();
        ArrayList<Command> commands = Globals.getCommands();
        String error = "> There are no commands with the type: " + args + ".\n" + Utility.getCommandInfo(this, command);
        //setting embed colour to match Bot's Colour
        Color color = Utility.getUsersColour(Globals.getClient().getOurUser(), command.guild);
        if (color != null) {
            helpEmbed.withColor(color);
        }

        //getting Types of commands.
        types.add(TYPE_DM);
        for (Command c : commands) {
            boolean typeFound = false;
            for (String s : types) {
                if (c.type().equals(s)) {
                    typeFound = true;
                }
            }
            if (c.type().equalsIgnoreCase(Command.TYPE_SERVERS) && !command.guildConfig.doModuleServers()) {
            } else if (c.type().equalsIgnoreCase(Command.TYPE_CHARACTER) && !command.guildConfig.doModuleChars()) {
            } else if (c.type().equalsIgnoreCase(Command.TYPE_COMPETITION) && !command.guildConfig.doModuleComp()) {
            } else if (!typeFound) {
                types.add(c.type());
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
            if (!command.guildID.equals("206792432560373761")) {
                desc += "\nSupport Discord - https://discord.gg/XSyQQrR";
            }

            helpEmbed.withTitle("Here are the Command Types I have available for use:");
            builder.append(Utility.getCommandInfo(this, command) + "\n");
            helpEmbed.withDescription(builder.toString());
            helpEmbed.appendField("Helpful Links", desc, true);
            helpEmbed.withFooterText("Bot Version: " + Globals.version);
        } else {
            if (args.equalsIgnoreCase(Command.TYPE_SERVERS) && !command.guildConfig.doModuleServers()) {
                return error;
            } else if (args.equalsIgnoreCase(Command.TYPE_CHARACTER) && !command.guildConfig.doModuleChars()) {
                return error;
            } else if (args.equalsIgnoreCase(Command.TYPE_COMPETITION) && !command.guildConfig.doModuleComp()) {
                return error;
            }
            boolean isFound = false;
            String title = "ERROR";
            String suffix = Utility.getCommandInfo(new Info(), command);
            for (String s : types) {
                if (args.equalsIgnoreCase(s)) {
                    title = "> Here are all of the " + s + " Commands I have available.";
                    isFound = true;
                    if (s.equalsIgnoreCase(TYPE_DM)) {
                        commandList.addAll(Globals.commandsDM.stream().map(c -> Globals.defaultPrefixCommand + c.names()[0]).collect(Collectors.toList()));
                        suffix = "These commands can only be performed in DMs.\n" + suffix;
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
        Utility.sendEmbededMessage("", helpEmbed.build(), command.channel);
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
