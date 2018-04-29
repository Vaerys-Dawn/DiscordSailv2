package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
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
                if (s.toString().equalsIgnoreCase(args)) {
                    List<IChannel> channels = command.guild.getChannelsByType(s);
                    List<String> channelMentions = Utility.getChannelMentions(channels);
                    if (channels.size() != 0) {
                        builder.appendField(s.toString(), Utility.listFormatter(channelMentions, true), false);
                        RequestHandler.sendEmbedMessage("", builder, command.channel.get());
                        return null;
                    } else {
                        if (s.isSetting()) {
                            return "> Could not find any channels with the **" + s.toString() + "** setting enabled.";
                        } else {
                            return "> Could not find a channel with the **" + s.toString() + "** type enabled.";
                        }
                    }
                }
            }
            return "> Could not any channel settings with that name.";
        }

        for (ChannelSettingObject c : command.guild.channelData.getChannelSettings()) {
            if (c.getChannelIDs().contains(command.channel.longID)) {
                for (ChannelSetting setting : Globals.getChannelSettings()) {
                    if (c.getType() == setting) {
                        if (setting.isSetting()) {
                            channelSettings.add(c.getType().toString());
                        } else {
                            channelTypes.add(c.getType().toString());
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
    public String description(CommandObject command) {
        return "Gives information about the current channel";
    }

    @Override
    public void init() {

    }

    @Override
    protected String[] names() {
        return new String[]{"ChannelStats"};
    }

    @Override
    protected String usage() {
        return "(Channel setting/type)";
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
        return false;
    }
}
