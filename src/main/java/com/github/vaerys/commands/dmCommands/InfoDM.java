package com.github.vaerys.commands.dmCommands;

import java.util.List;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.Info;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.DMCommand;

/**
 * Created by Vaerys on 05/02/2017.
 */
public class InfoDM extends DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        List<Command> commands = command.guild.getAllCommands(command);
        if (command.user.longID == command.client.creator.longID) {
            commands.addAll(Globals.getCreatorCommands(true));
        }

        String error = "> Could not find information on any commands named **" + args + "**.";
        for (Command c : commands) {
            for (String s : c.names) {
                if (args.equalsIgnoreCase(s)) {
                    // if (!Utility.testForPerms(c.perms(), command.user.getToggles(), command.guild.getToggles())) {
                    // return error;
                    // }
                    RequestHandler.sendEmbedMessage("", c.getCommandInfo(command), command.channel.get());
                    return "";
                }
            }

        }
        return error;
        // List<Command> commands = Utility.getCommandsByType(Globals.getAllCommands(), command, TYPE_DM,
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
        // infoEmbed.appendField("> Info - " + c.names()[0], builder.toString(), false);
        //
        // //aliases
        // if (c.names().length > 1) {
        // StringBuilder aliasBuilder = new StringBuilder();
        // for (int i = 1; i < c.names().length; i++) {
        // aliasBuilder.append(Globals.defaultPrefixCommand + c.names()[i] + ", ");
        // }
        // aliasBuilder.delete(aliasBuilder.length() - 2, aliasBuilder.length());
        // aliasBuilder.append(".\n");
        // infoEmbed.appendField("Aliases:", aliasBuilder.toString(), false);
        // }
        // Utility.sendEmbedMessage("", infoEmbed, command.channel.getToggles());
        // return "";
        // }
        // }
        // }
        // return "> Command with the name " + args + " not found.";
    }

    protected static final String[] NAMES = new Info().names;

    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Tells you information about DM commands.";
    }

    protected static final String USAGE = "[Command Name]";

    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.HELP;

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;

    }

    protected static final ChannelSetting CHANNEL_SETTING = null;
    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    protected static final boolean REQUIRES_ARGS = true;

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    public void init() {

    }
}
