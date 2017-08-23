package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class Toggle implements Command {
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
                        return "> **" + t.name() + "** is now **" + t.get(command.guild.config) + "**.";
                    }
                }
            }
            if (isModule) {
                builder.append("> Could not find Module \"" + args + "\".\n");
            } else {
                builder.append("> Could not find Toggle \"" + args + "\".\n");
            }

        }
        XEmbedBuilder embedBuilder = new XEmbedBuilder();
        String title;
        if (isModule) {
            title = "> Here is a list of available Guild Modules:\n";
        } else {
            title = "> Here is a list of available Guild Toggles:\n";
        }
        ArrayList<String> types = new ArrayList<>();
        for (GuildToggle t : command.guild.toggles) {
            if (t.isModule() == isModule) {
                types.add(t.name());
            }
        }
        Collections.sort(types);
        embedBuilder.withDesc(builder.toString());
        Utility.listFormatterEmbed(title, embedBuilder, types, true);
        embedBuilder.appendField(spacer, Utility.getCommandInfo(classObject, command), false);
        embedBuilder.withColor(command.client.color);
        Utility.sendEmbedMessage("", embedBuilder, command.channel.get());
//        command.guild.loadCommandData();
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Toggle"};
    }

    @Override
    public String description() {
        return "Toggles Certain Parts of the Guild Config.";
    }

    @Override
    public String usage() {
        return "(Toggle Type)";
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
