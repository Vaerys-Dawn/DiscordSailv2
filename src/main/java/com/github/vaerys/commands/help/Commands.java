package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 29/01/2017.
 */


public class Commands extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder helpEmbed = new XEmbedBuilder(command);
        List<SAILType> types = new ArrayList<>();
        List<SAILType> filteredTypes = new ArrayList<>();

        //get commands
        List<Command> commands = new ArrayList<>(command.guild.getAllCommands(command));

        //if creator get creator commands
        if (command.user.checkIsCreator()) {
            commands.addAll(Globals.getALLCreatorCommands());
        }

        //add dm commands.
        types.add(SAILType.DM);
        commands.addAll(Globals.getCommands(true));

        //get types
        commands.forEach(command1 -> {
            if (!types.contains(command1.type)) types.add(command1.type);
        });

        //remove types that the user does not have permission to see.
        Collections.sort(types);
        filteredTypes.addAll(types.stream()
                .filter(t -> Utility.getCommandsByType(commands, command, t, true).size() != 0)
                .collect(Collectors.toList()));


        //print out list of types.
        if (args == null || args.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append(codeBlock + "\n");
            builder.append(Utility.listEnumFormatter(filteredTypes, false));
            builder.append(codeBlock + "\n");
            helpEmbed.withTitle("Here are the Command Types I have available for use:");
            builder.append(Utility.getCommandInfo(this, command) + "\n");
            helpEmbed.withDescription(builder.toString());
            RequestHandler.sendEmbedMessage("", helpEmbed, command.channel.get());
            return null;
        }

        //make sure that the type exits
        SAILType type = SAILType.get(args);
        if (type == null || !types.contains(type)) {
            return "> There are no commands with the type: **" + args + "**.\n\n" + Utility.getCommandInfo(this, command);
        }

        //make sure that the user is allowed to view the type.
        if (!filteredTypes.contains(type)) {
            return "> You do not have permission to see the **" + type.toString() + "** page.";
        }


        StringHandler builder = new StringHandler();
        String suffix = Utility.getCommandInfo(new Help(), command);
        helpEmbed.withTitle("> Here are all of the " + type.toString() + " Commands I have available.");

        //special footers
        switch (type) {
            case DM:
                suffix = "**These commands can only be performed in DMs.**\n" +
                        "> If you send a non command message to my DMs it will send it to my creator.\n\n" + suffix;
                break;
            case CREATOR:
                suffix = "**Only the creator of this bot can run these commands.**\n\n" + suffix;
                break;
        }

        //build command list
        TreeMap<String, Boolean> commandNames = new TreeMap<>();
        for (Command c : commands) {
            //filter out subCommands that aren't allowed to be seen due to missing perms or inactive module.
            List<SubCommandObject> subCommands = c.subCommands.stream()
                    .filter(s -> types.contains(s.getType()))
                    .filter(s -> GuildHandler.testForPerms(command, s.getPermissions()))
                    .collect(Collectors.toList());
            //add command if command type == type specified.
            if (c.type == type || (c.type != type && c.channel == ChannelSetting.FROM_DM)) {
                commandNames.put(c.getCommand(command), subCommands.size() != 0);
            }
            //add any valid subCommands.
            for (SubCommandObject s : subCommands) {
                if (s.getType() == type) {
                    if (c.type == type && !c.showIndividualSubs) break;
                    String subName = s.getCommand(command);
                    if (!commandNames.containsKey(subName)) {
                        commandNames.put(s.getCommand(command), true);
                    }
                    if (!showIndividualSubs) break;
                }
            }
        }
        //formats list
        List<String> list = new LinkedList<>();
        if (commandNames.containsValue(true)) {
            List<String> finalList = list;
            commandNames.forEach((s, hasSub) -> {
                if (hasSub) {
                    finalList.add("* " + s);
                } else {
                    finalList.add("  " + s);
                }
            });
        } else {
            list = commandNames.keySet().stream().collect(Collectors.toList());
        }
        //build embed.
        builder.append(codeBlock + "\n");
        builder.append(Utility.listFormatter(list, false));
        builder.append(codeBlock + "\n");
        builder.append(suffix);
        helpEmbed.withDescription(builder.toString());
        RequestHandler.sendEmbedMessage("", helpEmbed, command.channel.get());
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"Commands"};
    }

    @Override
    public String description(CommandObject command) {
        return "Lists the commands that users can run.";
    }

    @Override
    protected String usage() {
        return "(Command Type)";
    }

    @Override
    protected SAILType type() {
        return SAILType.HELP;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
