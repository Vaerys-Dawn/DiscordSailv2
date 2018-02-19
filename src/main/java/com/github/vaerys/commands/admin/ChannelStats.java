package com.github.vaerys.commands.admin;

import java.util.ArrayList;
import java.util.List;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class ChannelStats extends Command {
    
    // using static as it will cause less memory to be used overall by orphaned data
    protected static final String[] NAMES = new String[]{"ChannelStats"};
    protected static final String USAGE = "(Channel setting/type)";
    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_CHANNELS};
    protected static final boolean REQUIRES_ARGS = false;
    protected static final boolean DO_ADMIN_LOGGING = false;
    
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
