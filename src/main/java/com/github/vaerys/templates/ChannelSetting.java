package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.Report;
import com.github.vaerys.commands.help.SilentReport;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.objects.XEmbedBuilder;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 09/04/2017. Edited by C0bra5 on 2018-feb-18
 */

/**
 * This enum contains all the states in which a server can be set to.
 *
 * @author C0bra5
 */
public enum ChannelSetting {

    // "Settings"

    /**
     * General Bot Command Channel.
     */
    BOT_COMMANDS(Command.CHANNEL_BOT_COMMANDS, true, "General Bot Command Channel."),

    /**
     * Channel for general use of the base CC commands.
     */
    CHANNEL_CC(Command.CHANNEL_CC_INFO, true, "Channel for general use of the base CC commands."),

    /**
     * When this setting is enabled on a channel it will deny the use of custom commands in it.
     */
    CC_DENIED(Command.CHANNEL_CC_DENIED, true, "When this setting is enabled on a channel it will deny the use of custom commands in it."),

    /**
     * General Channel for Character related commands.
     */
    CHANNEL_CHARACTERS(Command.CHANNEL_CHAR, true, "General Channel for Character related commands."),

    /**
     * Channel for creating, editing and deleting custom commands.
     */
    CREATE_CC(Command.CHANNEL_EDIT_CC, true, "Channel for creating, editing and deleting custom commands."),

    /**
     * When enabled on a channel, no message logs will occur for the channel.
     */
    DONT_LOG(Command.CHANNEL_DONT_LOG, true, "When enabled on a channel, no message logs will occur for the channel."),

    /**
     * Channel for the group commands.
     */
    GROUPS(Command.CHANNEL_GROUPS, true, "Channel for the group commands."),

    /**
     * When this setting is on a channel no level up messages will be sent to the channel.
     */
    LEVEL_UP_DENIED(Command.CHANNEL_LEVEL_UP_DENIED, true, "When this setting is on a channel no level up messages will be sent to the channel."),

    /**
     * Channel for pixel commands.
     */
    PIXELS(Command.CHANNEL_PIXELS, true, "Channel for pixel commands."),

    /**
     * Channel for the server listing commands.
     */
    SERVERS(Command.CHANNEL_SERVERS, true, "Channel for the server listing commands."),

    /**
     * When this setting is on a channel all custom commands create in it will be, automatically tagged
     * shitpost. shitpost commands can only be run in shitpost channels.
     */
    SHITPOST(Command.CHANNEL_SHITPOST, true, "When this setting is on a channel all custom commands create in it will be, automatically tagged shitpost. shitpost commands can only be run in shitpost channels."),

    /**
     * When this setting is on a channel no pixels will be gained in the channel.
     */
    XP_DENIED(Command.CHANNEL_XP_DENIED, true, "When this setting is on a channel no pixels will be gained in the  channel."),


    // "Types"

    /**
     * Where messages created by the $1%s and $2%s commands are sent.
     */
    ADMIN(Command.CHANNEL_ADMIN, false, "Where messages created by the $1%s and $2%s commands are sent."),

    /**
     * Where all of the admin type logging will be sent.
     */
    ADMIN_LOG(Command.CHANNEL_ADMIN_LOG, false, "Where all of the admin type logging will be sent."),

    /**
     * Where art is enabled to be pinned by users via sail.
     */
    ART(Command.CHANNEL_ART, false, "Where art is enabled to be pinned by users via sail."),

    /**
     * Command.CHANNEL_INFO, false, "Channel to post the contents of the Info.txt file."
     */
    CHANNEL_INFO(Command.CHANNEL_INFO, false, "Channel to post the contents of the Info.txt file."),

    /**
     * Where is where daily messages will be sent.
     */
    GENERAL(Command.CHANNEL_GENERAL, false, "Where is where daily messages will be sent."),

    /**
     * Where the LevelChannel profile setting will send level up messages.
     */
    LEVEL_UP(Command.CHANNEL_LEVEL_UP, false, "Where the LevelChannel profile setting will send level up messages."),

    /**
     * Where all the general type logging will be sent.
     */
    SERVER_LOG(Command.CHANNEL_SERVER_LOG, false, "Where all the general type logging will be sent.");

    protected String name;

    protected boolean isSetting;

    protected String desc;

    /**
     * the human readable value of the enum
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * checks if the setting is actually a channel setting
     */
    public boolean isSetting() {
        return isSetting;
    }

    ;

    /**
     * returns the setting description using a command as a description argument
     *
     * @param command the command used in the documentation
     * @return the description of the channel setting
     */
    public String getDesc(CommandObject command) {
        if (desc == null) {
            return desc;
        } else {
            return String.format(desc, new Report().getCommand(command), new SilentReport().getCommand(command));
        }

    }

    ;

    /**
     * Returns the setting description
     *
     * @return the setting description
     */
    public String getDesc() {
        return desc;
    }

    ;

    /**
     * Creates a channel setting class
     *
     * @param name
     * @param isSetting
     * @param desc
     */
    private ChannelSetting(String name, boolean isSetting, String desc) {
        this.name = name;
        this.isSetting = isSetting;
        this.desc = desc;
    }


    public ArrayList<Long> getIDs(GuildObject guild) {
        for (ChannelSettingObject o : guild.channelData.getChannelSettings()) {
            if (o.getType().name().equals(name())) {
                if (!o.getChannelIDs().isEmpty()) {
                    return o.getChannelIDs();
                } else {
                    return new ArrayList<>();
                }
            }
        }
        return new ArrayList<>();
    }

    public XEmbedBuilder getInfo(CommandObject object) {
        XEmbedBuilder builder = new XEmbedBuilder(object);
        if (isSetting()) {
            builder.withTitle("Channel Setting - " + name);
        } else {
            builder.withTitle("Channel Type - " + name);
        }
        builder.withDesc(getDesc(object));
        List<IChannel> channels = object.guild.getChannelsByType(name);
        channels = object.user.getVisibleChannels(channels);
        String title;
        if (isSetting()) {
            title = "Channels:";
        } else {
            title = "Channel:";
        }
        if (channels.size() != 0) {
            builder.appendField(title, Utility.listFormatter(channels.stream().map(channel -> channel.mention()).collect(Collectors.toList()), true), false);
        }
        List<Command> commands = object.guild.commands.stream().filter(command -> {
            if (command.channel() != null && Utility.testForPerms(object, command.perms())) {
                return command.channel().equalsIgnoreCase(name);
            }
            return false;
        }).collect(Collectors.toList());
        List<String> typeCommands = commands.stream().map(command -> command.getCommand(object)).collect(Collectors.toList());
        if (commands.size() != 0) {
            builder.appendField("Commands:", Utility.listFormatter(typeCommands, true), false);
        }
        return builder;
    }

    public String toggleSetting(GuildObject guild, long channelID) {
        List<ChannelSettingObject> objects = guild.channelData.getChannelSettings();
        boolean isFound = false;
        for (ChannelSettingObject s : objects) {
            if (s.getType().equals(name)) {
                isFound = true;
            }
        }
        if (!isFound) {
            objects.add(new ChannelSettingObject(name));
        }
        for (ChannelSettingObject object : objects) {
            if (object.getType().equals(name)) {
                if (!isSetting()) {
                    if (object.getChannelIDs().isEmpty() || !object.getChannelIDs().get(0).equals(channelID)) {
                        if (object.getChannelIDs().isEmpty()) {
                            object.getChannelIDs().add(channelID);
                        } else {
                            object.getChannelIDs().set(0, channelID);
                        }
                        return "> " + Globals.client.getChannelByID(channelID).mention() + " is now the Server's **" + name + "** channel.";
                    } else {
                        object.getChannelIDs().remove(0);
                        return "> " + Globals.client.getChannelByID(channelID).mention() + " is no longer the Server's **" + name + "** channel.";
                    }
                } else {
                    if (object.getChannelIDs().isEmpty() || !object.getChannelIDs().contains(channelID)) {
                        object.getChannelIDs().add(channelID);
                        return "> " + Globals.client.getChannelByID(channelID).mention() + ". Channel setting: **" + name + "** added.";
                    } else {
                        for (int i = 0; i < object.getChannelIDs().size(); i++) {
                            if (channelID == object.getChannelIDs().get(i)) {
                                object.getChannelIDs().remove(i);
                                return "> " + Globals.client.getChannelByID(channelID).mention() + ". Channel setting: **" + name + "** removed.";
                            }
                        }
                    }
                }
            }
        }
        return "> Error toggling channel setting.";
    }

    public static ChannelSetting get(String type) {
        for (ChannelSetting c : values()) {
            if (c.toString().equalsIgnoreCase(type)) {
                return c;
            }
        }
        return null;
    }
}

