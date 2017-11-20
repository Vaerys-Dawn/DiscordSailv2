package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.pogos.GuildConfig;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 09/04/2017.
 */
public interface ChannelSetting {

    String name();

    boolean isSetting();

    String desc(CommandObject command);

    default ArrayList<Long> getIDs(GuildConfig config) {
        for (ChannelSettingObject o : config.getChannelSettings()) {
            if (o.getType().equals(name())) {
                if (o.getChannelIDs().size() != 0) {
                    return o.getChannelIDs();
                } else {
                    return new ArrayList<>();
                }
            }
        }
        return new ArrayList<>();
    }

    default XEmbedBuilder getInfo(CommandObject object) {
        XEmbedBuilder builder = new XEmbedBuilder();
        builder.withColor(object.client.color);
        if (isSetting()) {
            builder.withTitle("Channel Setting - " + name());
        } else {
            builder.withTitle("Channel Type - " + name());
        }
        builder.withDesc(desc(object));
        List<IChannel> channels = object.guild.config.getChannelsByType(name(), object.guild);
        channels = object.user.getVisibleChannels(channels);
        String title;
        if (isSetting()){
            title = "Channels:";
        }else {
            title = "Channel:";
        }
        if (channels.size() != 0) {
            builder.appendField(title, Utility.listFormatter(channels.stream().map(channel -> channel.mention()).collect(Collectors.toList()), true), false);
        }
        List<Command> commands = object.guild.commands.stream().filter(command -> {
            if (command.channel() != null && Utility.testForPerms(object, command.perms())) {
                return command.channel().equalsIgnoreCase(name());
            }
            return false;
        }).collect(Collectors.toList());
        List<String> typeCommands = commands.stream().map(command -> command.getCommand(object)).collect(Collectors.toList());
        if (commands.size() != 0) {
            builder.appendField("Commands:", Utility.listFormatter(typeCommands, true), false);
        }
        return builder;
    }

    default String toggleSetting(GuildConfig config, long channelID) {
        ArrayList<ChannelSettingObject> objects = config.getChannelSettings();
        boolean isFound = false;
        for (ChannelSettingObject s : objects) {
            if (s.getType().equals(name())) {
                isFound = true;
            }
        }
        if (!isFound) {
            objects.add(new ChannelSettingObject(name()));
        }
        for (ChannelSettingObject object : objects) {
            if (object.getType().equals(name())) {
                if (!isSetting()) {
                    if (object.getChannelIDs().size() == 0 || !object.getChannelIDs().get(0).equals(channelID)) {
                        if (object.getChannelIDs().size() == 0) {
                            object.getChannelIDs().add(channelID);
                        } else {
                            object.getChannelIDs().set(0, channelID);
                        }
                        return "> " + Globals.client.getChannelByID(channelID).mention() + " is now the Server's **" + name() + "** channel.";
                    } else {
                        object.getChannelIDs().remove(0);
                        return "> " + Globals.client.getChannelByID(channelID).mention() + " is no longer the Server's **" + name() + "** channel.";
                    }
                } else {
                    if (object.getChannelIDs().size() == 0 || !object.getChannelIDs().contains(channelID)) {
                        object.getChannelIDs().add(channelID);
                        return "> " + Globals.client.getChannelByID(channelID).mention() + ". Channel setting: **" + name() + "** added.";
                    } else {
                        for (int i = 0; i < object.getChannelIDs().size(); i++) {
                            if (channelID == object.getChannelIDs().get(i)) {
                                object.getChannelIDs().remove(i);
                                return "> " + Globals.client.getChannelByID(channelID).mention() + ". Channel setting: **" + name() + "** removed.";
                            }
                        }
                    }
                }
            }
        }
        return "> Error toggling channel setting.";
    }

    default String validate() {
        StringBuilder response = new StringBuilder();
        boolean isErrored = false;
        response.append("\n>> Begin Error Report: " + this.getClass().getName() + " <<\n");
        if (name() == null || name().isEmpty()) {
            response.append("> TYPE IS EMPTY.\n");
            isErrored = true;
        }
        if (desc(new CommandObject()) == null || desc(new CommandObject()).isEmpty()) {
            response.append("> DESCRIPTION IS EMPTY.\n");
            isErrored = true;
        }
        response.append(">> End Error Report <<");
        if (isErrored) {
            return response.toString();
        } else {
            return null;
        }
    }
}
