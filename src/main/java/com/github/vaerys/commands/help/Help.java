package com.github.vaerys.commands.help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 29/01/2017.
 */


public class Help extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder helpEmbed = new XEmbedBuilder(command);
        List<SAILType> types = new ArrayList<>();
        List<Command> commands = new ArrayList<>(command.guild.getAllCommands(command));
        if (command.user.longID == command.client.creator.longID) {
            commands.addAll(Globals.getCreatorCommands(false));
        }
        commands.forEach(command1 -> {
            if (!types.contains(command1.type))
                types.add(command1.type);
        });
        String error = "> There are no commands with the type: **" + args + "**.\n\n" + Utility.getCommandInfo(this, command);
        ListIterator iterator = types.listIterator();
        while (iterator.hasNext()) {
            SAILType t = (SAILType) iterator.next();
            if (Utility.getCommandsByType(commands, command, t, true).size() == 0) {
                iterator.remove();
            }
        }
        Collections.sort(types);
        List<String> typeNames = new ArrayList<>(types.size());
        for (SAILType c : types) {
            typeNames.add(types.toString());
        }
        
        if (args == null || args.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append(codeBlock + "\n");
            builder.append(Utility.listFormatter(typeNames, false));
            builder.append(codeBlock + "\n");
            helpEmbed.withTitle("Here are the Command Types I have available for use:");
            builder.append(Utility.getCommandInfo(this, command) + "\n");
            helpEmbed.withDescription(builder.toString());
            RequestHandler.sendEmbedMessage("", helpEmbed, command.channel.get());
            return null;
        } else {
            for (String s : typeNames) {
                if (s.equalsIgnoreCase(args)) {
                    StringBuilder builder = new StringBuilder();
                    String suffix = Utility.getCommandInfo(new Info(), command);
                    helpEmbed.withTitle("> Here are all of the " + s + " Commands I have available.");
                    if (args.equalsIgnoreCase(SAILType.DM.toString())) {
                        suffix = "**These commands can only be performed in DMs.**\n" +
                                "> If you send a non command message to my DMs it will send it to my creator.\n\n" + suffix;
                    } else if (args.equalsIgnoreCase(SAILType.CREATOR.toString())) {
                        suffix = "**Only the creator of this bot can run these commands.**\n\n" + suffix;
                    }
                    builder.append(codeBlock + "\n");
                    List<String> commandNames = new ArrayList<>();
                    for (Command c : Utility.getCommandsByType(commands, command, SAILType.get(s), true)) {
                        StringBuilder commandCall = new StringBuilder(c.getCommand(command));
                          //commented out at dawn's request
//                        if (c.dualType() != null && Utility.testForPerms(command, c.dualPerms())) {
//                            commandCall.append(indent + "*");
//                        }
                        commandNames.add(commandCall.toString());
                    }
                    Collections.sort(commandNames);
                    builder.append(Utility.listFormatter(commandNames, false));
                    builder.append(codeBlock + "\n");
                    builder.append(suffix);
                    helpEmbed.withDescription(builder.toString());
                    RequestHandler.sendEmbedMessage("", helpEmbed, command.channel.get());
                    return null;
                }
            }
            return error;
        }
    }

    protected static final String[] NAMES = new String[]{"Commands"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Lists the commands that users can run.";
    }

    protected static final String USAGE = "(Command Type)";
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

    protected static final Permissions[] PERMISSIONS = new Permissions[0];
    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    protected static final boolean DO_ADMIN_LOGGING = false;
    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

    @Override
    public void init() {

    }
}
