package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.HelpModules;
import com.github.vaerys.commands.help.HelpSettings;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildToggle;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Toggle extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        return getContent(args, command, false, this);
    }

    public String getContent(String args, CommandObject command, boolean isModule, Command classObject) {
        StringBuilder builder = new StringBuilder();
        if (!args.isEmpty()) {
            for (GuildToggle t : command.guild.toggles) {
                if (t.isModule() == isModule) {
                    if (args.equalsIgnoreCase(t.name())) {
                        t.toggle(command.guild.config);
                        command.guild.loadCommandData();

                        String mode = t.get(command.guild.config) ? "enabled" : "disabled";
                        String type = t.isModule() ? "module" : "setting";
                        String helpCommand = t.isModule() ? new HelpModules().getUsage(command) : new HelpSettings().getUsage(command);
                        return "> **" + t.name() + "** is now **" + mode + "**.\n\n" +
                                "To see more info about what this " + type + " " + mode + " you can run **" + helpCommand + "**.";
                    }
                }
            }
            if (isModule) {
                builder.append("> Could not find Module \"" + args + "\".\n");
            } else {
                builder.append("> Could not find Setting \"" + args + "\".\n");
            }

        }
        XEmbedBuilder embedBuilder = new XEmbedBuilder(command);
        String title;
        if (isModule) {
            title = "> Here are all of the available Modules:\n";
        } else {
            title = "> Here are all of the available Settings:\n";
        }
        List<String> typesActive = new LinkedList<>();
        List<String> typesDeactivated = new LinkedList<>();
        for (GuildToggle t : command.guild.toggles) {
            if (t.isModule() == isModule) {
                if (t.get(command.guild.config)) typesActive.add(t.name());
                else typesDeactivated.add(t.name());
            }
        }
        Collections.sort(typesActive);
        Collections.sort(typesDeactivated);
        embedBuilder.withTitle(title);
        embedBuilder.withDescription("**Activated**\n```\n" + Utility.listFormatter(typesActive, true) + "```\n" +
                "**Deactivated**\n```\n" + Utility.listFormatter(typesDeactivated, true) + "```\n" +
                missingArgs(command));
        RequestHandler.sendEmbedMessage("", embedBuilder, command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Setting", "Toggle", "Settings", "Toggles"};
    }

    @Override
    public String description(CommandObject command) {
        return "Toggles the specified setting of the Guild Config.";
    }

    @Override
    public String usage() {
        return "(Setting)";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return true;
    }

    @Override
    public void init() {

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
