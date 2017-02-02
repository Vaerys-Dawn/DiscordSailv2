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

        //setting embed colour to match Bot's Colour
        Color color = Utility.getUsersColour(Globals.getClient().getOurUser(), command.guild);
        if (color != null) {
            helpEmbed.withColor(color);
        }

        //getting Types of commands.
        for (Command c : commands) {
            boolean typeFound = false;
            for (String s : types) {
                if (c.type().equals(s)) {
                    typeFound = true;
                }
            }
            if (!typeFound) {
                types.add(c.type());
            }
        }
        //sort types
        Collections.sort(types);

        //building the embed
        if (args.isEmpty()) {
            builder.append(codeBlock + "\n");
            for (String s : types) {
                builder.append(s + "\n");
            }
            String desc = "[Suport Sail on Patreon](https://www.patreon.com/DawnFelstar)\n" +
                    "[Find Sail on GitHub](https://github.com/Vaerys-Dawn/DiscordSailv2)";
            if (!command.guildID.equals("206792432560373761")) {
                desc += "\nSupport Discord - https://discord.gg/XSyQQrR";
            }
            builder.append(codeBlock + "\n");
            helpEmbed.withTitle("Here are the Command Types I have available for use:");
            builder.append(Utility.getCommandInfo(this, command) + "\n");
            helpEmbed.withDescription(builder.toString());
            helpEmbed.appendField("Helpful Links",desc, true);
            helpEmbed.withFooterText("Bot Version: " + Globals.version);
        } else {
            boolean isFound = false;
            for (String s : types) {
                if (args.equalsIgnoreCase(s)) {
                    isFound = true;
                    helpEmbed.withTitle("> Here are all of the " + s + " Commands I have available.");
                    for (Command c : commands) {
                        if (c.type().equalsIgnoreCase(s)) {
                            if (c.dualType() != null) {
                                commandList.add(command.guildConfig.getPrefixCommand() + c.names()[0] + indent + "*\n");
                            } else {
                                commandList.add(command.guildConfig.getPrefixCommand() + c.names()[0] + "\n");
                            }
                        }
                    }
                }
            }
            Collections.sort(commandList);
            builder.append(codeBlock + "\n");
            commandList.forEach(builder::append);
            builder.append(codeBlock + "\n");
            builder.append(Utility.getCommandInfo(new Info(), command));
            helpEmbed.withDescription(builder.toString());
            if (!isFound) {
                return "> There are no commands with the type: " + args + ".\n" + Utility.getCommandInfo(this, command);
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
        return "[Command Type]";
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
