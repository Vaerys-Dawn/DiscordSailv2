package Commands.DMCommands;

import Commands.Command;
import Commands.DMCommand;
import Commands.DMCommandObject;
import Commands.Help.Info;
import Main.Globals;
import Main.Utility;
import org.apache.commons.lang3.ArrayUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class InfoDM implements DMCommand {
    @Override
    public String execute(String args, DMCommandObject command) {
        ArrayList<DMCommand> commands = Globals.commandsDM;

        for (DMCommand c : commands) {
            for (String s : c.names()) {
                if (args.equalsIgnoreCase(s)) {
                    EmbedBuilder infoEmbed = new EmbedBuilder();

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
                    Utility.sendEmbededMessage("", infoEmbed.build(), command.channel);
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
