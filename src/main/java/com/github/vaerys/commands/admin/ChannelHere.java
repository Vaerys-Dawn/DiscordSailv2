package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 31/01/2017.
 */
public class ChannelHere extends Command {

    // using static as it will cause less memory to be used overall by orphaned data
    protected static final String[] NAMES = new String[] {"Channel", "ChannelHere", "ChannelSetting", "Channels"};
    protected static final String USAGE = "(Channel Type)";
    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[] {Permissions.MANAGE_CHANNELS};
    protected static final boolean REQUIRES_ARGS = false;
    protected static final boolean DO_ADMIN_LOGGING = true;


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

        List<ChannelSetting> channelSettings = Arrays.asList(command.guild.channelSettings);
        List<String> types = channelSettings.stream().filter(channelSetting -> !channelSetting.isSetting()).map(ChannelSetting::toString).collect(Collectors.toList());
        List<String> settings = channelSettings.stream().filter(channelSetting -> channelSetting.isSetting()).map(ChannelSetting::toString).collect(Collectors.toList());
        Collections.sort(types);
        desc += "**Types**\n```\n" + Utility.listFormatter(types, true) + "```\n" + "**Settings**\n```\n" + Utility.listFormatter(settings, true) + "```\n" + missingArgs(command);

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
        return NAMES;
    }


    @Override
    protected String usage() {
        return USAGE;
    }


    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }


    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }


    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }


    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }


    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

}
