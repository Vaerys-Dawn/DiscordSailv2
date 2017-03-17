package Commands.DMCommands;

import Commands.DMCommandObject;
import Interfaces.DMCommand;
import Main.Globals;
import Main.Utility;
import Objects.XEmbedBuilder;

import java.util.ArrayList;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class InfoDM implements DMCommand {
    @Override
    public String execute(String args, DMCommandObject command) {
        ArrayList<DMCommand> commands = Globals.getCommandsDM();

        for (DMCommand c : commands) {
            for (String s : c.names()) {
                if (args.equalsIgnoreCase(s)) {
                    XEmbedBuilder infoEmbed = new XEmbedBuilder();

                    //command info
                    StringBuilder builder = new StringBuilder();
                    builder.append("**" + Globals.defaultPrefixCommand + c.names()[0]);
                    if (c.usage() != null) {
                        builder.append(" " + c.usage());
                    }
                    builder.append("**\nDesc: **" + c.description() + "**\n");
                    builder.append("Type: **" + c.type() + "**\n");
                    if (c.type().equals(DMCommand.TYPE_CREATOR)) {
                        builder.append("**" + ownerOnly + "**");
                    }
                    infoEmbed.appendField("> Info - " + c.names()[0],builder.toString(),false);

                    //aliases
                    if (c.names().length > 1) {
                        StringBuilder aliasBuilder = new StringBuilder();
                        for (int i = 1; i < c.names().length; i++) {
                            aliasBuilder.append(Globals.defaultPrefixCommand + c.names()[i] + ", ");
                        }
                        aliasBuilder.delete(aliasBuilder.length() - 2, aliasBuilder.length());
                        aliasBuilder.append(".\n");
                        infoEmbed.appendField("Aliases:", aliasBuilder.toString(), false);
                    }
                    Utility.sendEmbedMessage("", infoEmbed, command.channel);
                    return "";
                }
            }
        }
        return "> Command with the name " + args + " not found.";
    }

    @Override
    public String[] names() {
        return new String[]{"Info"};
    }

    @Override
    public String description() {
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
