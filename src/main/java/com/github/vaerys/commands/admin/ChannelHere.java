package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.ChannelSetting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ChannelHere implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        String desc = "";
        if (!args.isEmpty()) {
            for (ChannelSetting s : command.guild.channelSettings) {
                if (args.equalsIgnoreCase(s.type())) {
                    return s.toggleSetting(command.guild.config, command.channel.longID);
                }

            }
            desc = "> Could not find channel type \"" + args + "\"\n";
        }
        XEmbedBuilder embedBuilder = new XEmbedBuilder();
        String title = "> Here is a list of available Channel Types:\n";

        List<ChannelSetting> channelSettings = command.guild.channelSettings;
        List<String> types = channelSettings.stream().map(ChannelSetting::type).collect(Collectors.toList());
        Collections.sort(types);
        embedBuilder.withDesc(desc);
        Utility.listFormatterEmbed(title, embedBuilder, (ArrayList<String>) types, true);
        embedBuilder.appendField(spacer, Utility.getCommandInfo(this, command), false);
        embedBuilder.withColor(command.client.color);
        Utility.sendEmbedMessage("", embedBuilder, command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Channel", "ChannelHere", "ChannelSetting"};
    }

    @Override
    public String description() {
        return "Sets the current channel as the channel type you select.";
    }

    @Override
    public String usage() {
        return "(Channel Type)";
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
        return new Permissions[]{Permissions.MANAGE_CHANNELS};
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
