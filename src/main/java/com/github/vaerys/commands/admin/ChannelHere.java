package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.help.HelpChannel;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;

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
            ChannelSetting setting = ChannelSetting.get(args);
            if (setting == null) {
                desc = "\\> Could not find messageChannel type \"" + args + "\"\n";
            } else {
                return setting.toggleSetting(command);
            }
        }
        XEmbedBuilder embedBuilder = new XEmbedBuilder(command);
        String title = "\\> Here are all of the messageChannel Types and Settings:";

        List<ChannelSetting> channelSettings = command.guild.channelSettings;
        List<String> types = channelSettings.stream().filter(channelSetting -> !channelSetting.isSetting()).map(ChannelSetting::toString).collect(Collectors.toList());
        List<String> settings = channelSettings.stream().filter(ChannelSetting::isSetting).map(ChannelSetting::toString).collect(Collectors.toList());
        Collections.sort(types);
        desc += "**Types**\n```\n" + SPACER + Utility.listFormatter(types, true) + "```\n" + SPACER + "**Settings**\n```\n" + Utility.listFormatter(settings, true) + "```\n";
        desc += "The Command **" + get(HelpChannel.class).getUsage(command) + "** Can give you extra information about each of the above.\n\n";
        desc += "See what is enabled on a messageChannel via **" +get(ChannelStats.class).getUsage(command) + "**\n\n";
        desc += missingArgs(command);
        embedBuilder.setDescription(desc);
        embedBuilder.setTitle(title);
        embedBuilder.queue(command.guildChannel);
        return null;
    }


    @Override
    public String description(CommandObject command) {
        return "Sets the current messageChannel as the messageChannel type you select.";
    }

    @Override
    public void init() {
        // does nothing
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
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
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
