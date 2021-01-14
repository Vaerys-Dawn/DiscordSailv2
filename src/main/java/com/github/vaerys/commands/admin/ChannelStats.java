package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.ChannelSettingObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 01/07/2017.
 */
public class ChannelStats extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.setTitle("Channel Stats");
        ArrayList<String> channelTypes = new ArrayList<>();
        ArrayList<String> channelSettings = new ArrayList<>();

        if (args != null && !args.isEmpty()) {
            for (ChannelSetting s : command.guild.channelSettings) {
                if (s.toString().equalsIgnoreCase(args)) {
                    List<TextChannel> channels = command.guild.getChannelsByType(s);
                    List<String> channelMentions = Utility.getChannelMentions(channels);
                    if (channels.size() != 0) {
                        builder.addField(s.toString(), Utility.listFormatter(channelMentions, true), false);
                        builder.queue(command.guildChannel);
                        return null;
                    } else {
                        if (s.isSetting()) {
                            return "\\> Could not find any channels with the **" + s.toString() + "** setting enabled.";
                        } else {
                            return "\\> Could not find a messageChannel with the **" + s.toString() + "** type enabled.";
                        }
                    }
                }
            }
            return "\\> Could not any messageChannel settings with that name.";
        }

        for (ChannelSettingObject c : command.guild.channelData.getChannelSettings()) {
            if (c.getChannelIDs().contains(command.guildChannel.longID)) {
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
            return "\\> I found nothing of value.";
        }
        if (channelTypes.size() != 0) {
            builder.addField("Types:", Utility.listFormatter(channelTypes, true), false);
        }
        if (channelSettings.size() != 0) {
            builder.addField("Settings:", Utility.listFormatter(channelSettings, true), false);
        }
        builder.queue(command.guildChannel);
        return null;
    }


    @Override
    public String description(CommandObject command) {
        return "Gives information about the current messageChannel";
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
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
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
