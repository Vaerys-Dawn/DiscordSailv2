package com.github.vaerys.enums;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.Report;
import com.github.vaerys.commands.help.SilentReport;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This enum contains all the states in which a server can be set to.
 * <p>
 * <p>
 * Created by Vaerys on 09/04/2017.</br>
 * Edited by C0bra5 on 2018-feb-18
 * </p>
 *
 * @author C0bra5
 */
public enum ChannelSetting {

    // "Settings"

    /**
     * General Bot Command Channel.
     */
    BOT_COMMANDS("BotCommands", true, "General Bot Command Channel."),

    /**
     * Channel for general use of the base CC commands.
     */
    CC_INFO("CustomCommands", true, "Channel for general use of the base CC commands."),

    /**
     * When this setting is enabled on a channel it will deny the use of custom commands in it.
     */
    CC_DENIED("CCDenied", true, "When this setting is enabled on a channel it will deny the use of custom commands in it."),

    /**
     * General Channel for Character related commands.
     */
    CHARACTER("Characters", true, "General Channel for Character related commands."),

    /**
     * Channel for creating, editing and deleting custom commands.
     */
    MANAGE_CC("ManageCC", true, "Channel for creating, editing and deleting custom commands."),

    /**
     * When enabled on a channel, no message logs will occur for the channel.
     */
    DONT_LOG("DontLog", true, "When enabled on a channel, no message logs will occur for the channel."),

    /**
     * Channel for the group commands.
     */
    GROUPS("Groups", true, "Channel for the group commands."),

    /**
     * When this setting is on a channel no level up messages will be sent to the channel.
     */
    LEVEL_UP_DENIED("LevelUpDenied", true, "When this setting is on a channel no level up messages will be sent to the channel."),

    /**
     * Channel for pixel commands.
     */
    PIXELS("Pixels", true, "Channel for pixel commands."),

    /**
     * Channel for the server listing commands.
     */
    SERVERS("Servers", true, "Channel for the server listing commands."),

    /**
     * When this setting is on a channel all custom commands create in it will be, automatically tagged
     * shitpost. shitpost commands can only be run in shitpost channels.
     */
    SHITPOST("ShitPost", true, "When this setting is on a channel all custom commands create in it will be, automatically tagged shitpost. shitpost commands can only be run in shitpost channels."),

    /**
     * When this setting is on a channel no pixels will be gained in the channel.
     */
    XP_DENIED("XpDenied", true, "When this setting is on a channel no pixels will be gained in the  channel."),


    // "Types"

    /**
     * Where messages related to moderation will be sent.
     */
    ADMIN("Admin", false, "Where messages related to moderation will be sent."),

    /**
     * Where all of the admin type logging will be sent.
     */
    ADMIN_LOG("AdminLog", false, "Where all of the admin type logging will be sent."),

    /**
     * Where art is enabled to be pinned by users via sail.
     */
    ART("Art", false, "Where art is enabled to be pinned by users via sail."),

    /**
     * Command.CHANNEL_INFO, false, "Channel to post the contents of the Info.txt file."
     */
    INFO("Info", false, "Channel to post the contents of the Info.txt file."),

    /**
     * Where is where daily messages will be sent.
     */
    GENERAL("General", false, "Where is where daily messages will be sent."),

    /**
     * Where the LevelChannel profile setting will send level up messages.
     */
    LEVEL_UP("LevelUp", false, "Where the LevelChannel profile setting will send level up messages."),

    /**
     * Where all the general type logging will be sent.
     */
    SERVER_LOG("ServerLog", false, "Where all the general type logging will be sent."),

    /**
     * Where all the general type logging will be sent.
     */
    FROM_DM("DirectMessages", false, "The command can only be ran in DMs."),

    /**
     * When Enabled Spam Type messages will be ignored.
     */
    IGNORE_SPAM("IgnoreSpam", true, "When Enabled Spam Type messages will be ignored."),

    /**
     * Where Custom join messages will be sent.
     */
    JOIN_CHANNEL("JoinChannel", true, "Where Custom join messages will be sent.");


    protected String name;

    protected boolean isSetting;

    protected String desc;

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

    public static ChannelSetting get(String type) {
        for (ChannelSetting c : values()) {
            if (c.toString().equalsIgnoreCase(type)) {
                return c;
            }
        }
        return null;
    }

    ;

    /**
     * the human readable value of the enum
     */
    @Override
    public String toString() {
        return name;
    }

    ;

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
        if (command == null) {
            return desc;
        } else {
            return String.format(desc, new Report().getCommand(command), new SilentReport().getCommand(command));
        }

    }

    /**
     * Returns the setting description
     *
     * @return the setting description
     */
    public String getDesc() {
        return desc;
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
        List<IChannel> channels = object.guild.getChannelsByType(this);
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
            if (command.channel != null && GuildHandler.testForPerms(object, command.perms)) {
                return command.channel == this;
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

        String modifier = isSetting ? "Setting" : "Type";

        ChannelSettingObject channel = null;


        for (ChannelSettingObject s : objects) {
            if (s.getType() == this) {
                isFound = true;
                channel = s;
            }
        }
        if (!isFound) {
            channel = new ChannelSettingObject(name);
            objects.add(channel);
        }

        String error = "> An error occurred Trying to toggle the Channel " + modifier + ".";

        //this should never run.
        if (channel == null) return error;

        if (isSetting) {
            if (channel.getChannelIDs().isEmpty() || !channel.getChannelIDs().contains(channelID)) {
                channel.getChannelIDs().add(channelID);
                return "> " + guild.getChannelByID(channelID).mention() + ". Channel setting: **" + name + "** added.";
            } else {
                for (int i = 0; i < channel.getChannelIDs().size(); i++) {
                    if (channelID == channel.getChannelIDs().get(i)) {
                        channel.getChannelIDs().remove(i);
                        return "> " + guild.getChannelByID(channelID).mention() + ". Channel setting: **" + name + "** removed.";
                    }
                }
            }
        } else {
            if (channel.getChannelIDs().isEmpty() || !channel.getChannelIDs().get(0).equals(channelID)) {
                if (channel.getChannelIDs().isEmpty()) {
                    channel.getChannelIDs().add(channelID);
                } else {
                    channel.getChannelIDs().set(0, channelID);
                }
                return "> " + guild.getChannelByID(channelID).mention() + " is now the Server's **" + name + "** channel.";
            } else {
                channel.getChannelIDs().remove(0);
                return "> " + guild.getChannelByID(channelID).mention() + " is no longer the Server's **" + name + "** channel.";
            }
        }
        return error;
    }
}

