package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class ChannelStats extends Command {
    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withTitle("Channel Stats");
        ArrayList<String> channelTypes = new ArrayList<>();
        ArrayList<String> channelSettings = new ArrayList<>();

        if (args != null && !args.isEmpty()) {
            for (ChannelSetting s : command.guild.channelSettings) {
                if (s.name().equalsIgnoreCase(args)) {
                    List<IChannel> channels = command.guild.getChannelsByType(s.name());
                    List<String> channelMentions = Utility.getChannelMentions(channels);
                    if (channels.size() != 0) {
                        builder.appendField(s.name(), Utility.listFormatter(channelMentions, true), false);
                        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
                        return null;
                    } else {
                        if (s.isSetting()) {
                            return "> Could not find any channels with the **" + s.name() + "** setting enabled.";
                        } else {
                            return "> Could not find a channel with the **" + s.name() + "** type enabled.";
                        }
                    }
                }
            }
            return "> Could not any channel settings with that name.";
        }

        for (ChannelSettingObject c : command.guild.config.getChannelSettings()) {
            if (c.getChannelIDs().contains(command.channel.longID)) {
                for (ChannelSetting setting : Globals.getChannelSettings()) {
                    if (c.getType().equalsIgnoreCase(setting.name())) {
                        if (setting.isSetting()) {
                            channelSettings.add(c.getType());
                        } else {
                            channelTypes.add(c.getType());
                        }
                    }
                }
            }
        }

        if (channelSettings.size() == 0 && channelTypes.size() == 0) {
            return "> I found nothing of value.";
        }
        if (channelTypes.size() != 0) {
            builder.appendField("Types:", Utility.listFormatter(channelTypes, true), false);
        }
        if (channelSettings.size() != 0) {
            builder.appendField("Settings:", Utility.listFormatter(channelSettings, true), false);
        }
        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"ChannelStats"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives information about the current channel";
    }

    @Override
    public String usage() {
        return "(Channel setting/type)";
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
        return false;
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
