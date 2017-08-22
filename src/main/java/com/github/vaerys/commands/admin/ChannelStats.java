package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.ChannelSetting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class ChannelStats implements Command {
    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder();
        builder.withTitle("Channel Stats");
        builder.withColor(command.client.color);
        ArrayList<String> channelTypes = new ArrayList<>();
        ArrayList<String> channelSettings = new ArrayList<>();

        ArrayList<String> channelNames = new ArrayList<>();
        if (args != null && !args.isEmpty()) {
            for (ChannelSetting s : command.guild.channelSettings) {
                if (s.type().equalsIgnoreCase(args)) {
                    ArrayList<String> channelIDs = command.guild.config.getChannelIDsByType(s.type());
                    if (channelIDs != null) {
                        for (String cID : channelIDs) {
                            IChannel channel = command.guild.get().getChannelByID(cID);
                            if (channel != null) {
                                channelNames.add(channel.mention());
                            }
                        }
                        builder.appendField(s.type(), Utility.listFormatter(channelNames, true), false);
                        Utility.sendEmbedMessage("", builder, command.channel.get());
                        return null;
                    } else {
                        if (s.isSetting()) {
                            return "> Could not find any channels with the **" + s.type() + "** setting enabled.";
                        } else {
                            return "> Could not find a channel with the **" + s.type() + "** type enabled.";
                        }
                    }
                }
            }
            return "> Could not any channel settings with that name.";
        }

        for (ChannelSettingObject c : command.guild.config.getChannelSettings()) {
            if (c.getChannelIDs().contains(command.channel.stringID)) {
                for (ChannelSetting setting : Globals.getChannelSettings()) {
                    if (c.getType().equalsIgnoreCase(setting.type())) {
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
        Utility.sendEmbedMessage("", builder, command.channel.get());
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"ChannelStats"};
    }

    @Override
    public String description() {
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
