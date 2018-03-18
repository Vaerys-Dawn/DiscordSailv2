package com.github.vaerys.commands.help;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 29/01/2017.
 */


public class Commands extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        List<SAILType> types = new LinkedList<>();
        Map<SAILType, String> pages = new TreeMap<>();

        //get dm commands
        List<Command> dmCommands = Globals.getCommands(true);
        //is creator
        if (command.user.checkIsCreator()) {
            dmCommands.addAll(Globals.getCreatorCommands(true));
            List<Command> creatorCommands = Globals.getCreatorCommands(false);
            //add creator type and page
            pages.put(SAILType.CREATOR, buildPage(creatorCommands, command, SAILType.CREATOR));
            types.add(SAILType.CREATOR);
        }
        //add dm type and page
        pages.put(SAILType.DM, buildPage(dmCommands, command, SAILType.DM));
        types.add(SAILType.DM);

        //check visible commands;
        List<Command> visibleCommands = command.guild.commands.stream().filter(c -> GuildHandler.testForPerms(command, c.perms)).collect(Collectors.toList());

        //add all extra types
        types.addAll(visibleCommands.stream().map(c -> c.type).collect(Collectors.toList()));
        //remove duplicates
        types = types.stream().distinct().collect(Collectors.toList());

        //build pages
        for (SAILType s : types) {
            if (s == SAILType.CREATOR || s == SAILType.DM) continue;
            List<Command> typeCommands = visibleCommands.stream().filter(c -> c.type == s).collect(Collectors.toList());
            for (Command c : visibleCommands) {
                for (SubCommandObject sub : c.subCommands) {
                    if (sub.getType() == s && GuildHandler.testForPerms(command, sub.getPermissions())) {
                        typeCommands.add(c);
                    }
                }
            }
            pages.put(s, buildPage(typeCommands, command, s));
        }

        //post type list
        SAILType type = SAILType.get(args);
        boolean typeNull = type == null || !types.contains(type);
        boolean argsNull = args == null || args.isEmpty();
        if (typeNull || argsNull) {
            //get prefix
            String prefix = typeNull && !argsNull ? "> There are no commands with the type: **" + args + "**." : "";
            //title
            builder.withTitle("Here are the Command Types I have available for use:");
            //desc
            builder.withDesc("```\n" +
                    Utility.listFormatter(types.stream().map(t -> t.toString()).collect(Collectors.toList()), false) +
                    "```\n" + missingArgs(command));
            builder.send(prefix, command);
            return null;
        }

        //send page
        builder.withTitle("> Here are all of the " + type.toString() + " Commands I have available.");
        builder.withDesc(pages.get(type) + missingArgs(command));
        builder.send(command);
        return null;
    }

    private String buildPage(List<Command> commands, CommandObject command, SAILType type) {
        Map<String, Boolean> commandNames = new TreeMap<>();

        //build command name list
        for (Command c : commands) {
            //put command in the map
            boolean isDm = (type == SAILType.DM && c.channel == ChannelSetting.FROM_DM);
            if (c.type == type || isDm) {
                commandNames.put(c.getCommand(command), c.subCommands.size() != 0);
            }
            //add any valid subCommands.
            for (SubCommandObject s : c.subCommands) {
                if (s.getType() == type) {
                    if (c.type == type && !c.showIndividualSubs) break;
                    commandNames.put(s.getCommand(command), true);
                    if (!showIndividualSubs) break;
                }
            }
        }

        //format command names
        List<String> list = new LinkedList<>();
        if (commandNames.containsValue(true)) {
            List<String> finalList = list;
            commandNames.forEach((s, hasSub) -> finalList.add((hasSub ? "* " : "  ") + s));
        } else {
            list = commandNames.keySet().stream().collect(Collectors.toList());
        }

        //add suffixes to special pages
        String suffix = "";
        if (type == SAILType.DM) {
            suffix = "**These commands can only be performed in DMs.**\n" +
                    "> If you send a non command message to my DMs it will send it to my creator.\n\n";
        } else if (type == SAILType.CREATOR) {
            suffix = "**Only the creator of this bot can run these commands.**\n\n";
        }

        //finalise page
        return "```\n" + Utility.listFormatter(list, false) + "```\n" + suffix;
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
