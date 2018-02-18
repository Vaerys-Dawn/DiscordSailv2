package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ChannelHere extends Command {

    public ChannelHere() {
    }

    @Override
    public String execute(String args, CommandObject command) {
        String desc = "";
        if (!args.isEmpty()) {
            for (ChannelSetting s : command.guild.channelSettings) {
                if (args.equalsIgnoreCase(s.name())) {
                    return s.toggleSetting(command.guild.config, command.channel.longID);
                }

            }
            desc = "> Could not find channel type \"" + args + "\"\n";
        }
        XEmbedBuilder embedBuilder = new XEmbedBuilder(command);
        String title = "> Here are all of the channel Types and Settings:";

        List<ChannelSetting> channelSettings = command.guild.channelSettings;
        List<String> types = channelSettings.stream().filter(channelSetting -> !channelSetting.isSetting()).map(ChannelSetting::name).collect(Collectors.toList());
        List<String> settings = channelSettings.stream().filter(channelSetting -> channelSetting.isSetting()).map(ChannelSetting::name).collect(Collectors.toList());
        Collections.sort(types);
        desc += "**Types**\n```\n" + Utility.listFormatter(types, true) + "```\n" +
                "**Settings**\n```\n" + Utility.listFormatter(settings, true) + "```\n" + missingArgs(command);

        embedBuilder.withDesc(desc);
        embedBuilder.withTitle(title);
        RequestHandler.sendEmbedMessage("", embedBuilder, command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"Channel", "ChannelHere", "ChannelSetting", "Channels"};
    }

    @Override
    public String description(CommandObject command) {
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
