package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.help.HelpChannel;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ChannelHere extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        String desc = "";
        if (!args.isEmpty()) {
            for (ChannelSetting s : command.guild.channelSettings) {
                if (args.equalsIgnoreCase(s.toString())) {
                    return s.toggleSetting(command.guild, command.channel.longID);
                }

            }
            desc = "> Could not find channel type \"" + args + "\"\n";
        }
        XEmbedBuilder embedBuilder = new XEmbedBuilder(command);
        String title = "> Here are all of the channel Types and Settings:";

        List<ChannelSetting> channelSettings = command.guild.channelSettings;
        List<String> types = channelSettings.stream().filter(channelSetting -> !channelSetting.isSetting()).map(ChannelSetting::toString).collect(Collectors.toList());
        List<String> settings = channelSettings.stream().filter(channelSetting -> channelSetting.isSetting()).map(ChannelSetting::toString).collect(Collectors.toList());
        Collections.sort(types);
        desc += "**Types**\n```\n" + spacer + Utility.listFormatter(types, true) + "```\n" + spacer + "**Settings**\n```\n" + Utility.listFormatter(settings, true) + "```\n";
        desc += "The Command **" + get(HelpChannel.class).getUsage(command) + "** Can give you extra information about each of the above.\n\n";
        desc += missingArgs(command);
        embedBuilder.withDesc(desc);
        embedBuilder.withTitle(title);
        RequestHandler.sendEmbedMessage("", embedBuilder, command.channel.get());
        return null;
    }


    @Override
    public String description(CommandObject command) {
        return "Sets the current channel as the channel type you select.";
    }

    @Override
    public void init() {

    }

    @Override
    protected String[] names() {
        return new String[]{"Channel", "ChannelHere", "ChannelSetting", "Channels"};
    }


    @Override
    protected String usage() {
        return "(Channel Type)";
    }


    @Override
    protected SAILType type() {
        return SAILType.ADMIN;
    }


    @Override
    protected ChannelSetting channel() {
        return null;
    }


    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_CHANNELS};
    }


    @Override
    protected boolean requiresArgs() {
        return false;
    }


    @Override
    protected boolean doAdminLogging() {
        return true;
    }

}
