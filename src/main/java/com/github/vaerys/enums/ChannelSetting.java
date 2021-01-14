package com.github.vaerys.enums;

import com.github.vaerys.commands.help.Report;
import com.github.vaerys.commands.help.SilentReport;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.adminlevel.ChannelSettingObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

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

    BOT_COMMANDS("BotCommands", true, "General Bot Command Channel."),

    CC_INFO("CustomCommands", true, "Channel for general use of the base CC commands."),

    CC_DENIED("CCDenied", true, "When this setting is enabled on a messageChannel it will deny the use of custom commands in it."),

    CHARACTER("Characters", true, "General Channel for Character related commands."),

    MANAGE_CC("ManageCC", true, "Channel for creating, editing and deleting custom commands."),

    DONT_LOG("DontLog", true, "When enabled on a messageChannel, no message logs will occur for the messageChannel."),

    GROUPS("Groups", true, "Channel for the group commands."),

    LEVEL_UP_DENIED("LevelUpDenied", true, "When this setting is on a messageChannel no level up messages will be sent to the messageChannel."),

    PIXELS("Pixels", true, "Channel for pixel commands."),

    SERVERS("Servers", true, "Channel for the server listing commands."),

    /**
     * When this setting is on a messageChannel all custom commands create in it will be, automatically tagged
     * shitpost. shitpost commands can only be run in shitpost channels.
     */
    SHITPOST("ShitPost", true, "When this setting is on a messageChannel all custom commands create in it will be, automatically tagged shitpost. shitpost commands can only be run in shitpost channels."),

    XP_DENIED("XpDenied", true, "When this setting is on a messageChannel no pixels will be gained in the  messageChannel."),

    PROFILES("Profiles", true, "Channel For Profile related Commands."),

    IGNORE_SPAM("IgnoreSpam", true, "When Enabled Spam Type messages will be ignored."),

    MUTE_APPEALS("MuteAppeals", true, "Stops muted people from using commands in the messageChannel."),

    // "Types"

    ADMIN("Admin", false, "Where messages related to moderation will be sent."),

    ADMIN_LOG("AdminLog", false, "Where all of the admin type logging will be sent."),

    ART("Art", false, "Where art is enabled to be pinned by users via sail."),

    INFO("Info", false, "Channel to post the contents of the Info.txt file."),

    GENERAL("General", false, "Where is where daily messages will be sent."),

    LEVEL_UP("LevelUp", false, "Where the LevelChannel profile setting will send level up messages."),

    SERVER_LOG("ServerLog", false, "Where all the general type logging will be sent."),

    JOIN_CHANNEL("JoinChannel", false, "Where Custom join messages will be sent."),

    // direct messages should not be accessible

    FROM_DM("DirectMessages", false, "The command can only be ran in DMs.");


    protected String name;

    protected boolean isSetting;

    protected String desc;

    /**
     * Creates a messageChannel setting class
     *
     * @param name
     * @param isNotUnique defines if is allowed to have multiples of this ChannelSetting per server.
     * @param desc
     *
     *  TODO: 2019/OCT/31 When converting to V3 convert isNotUnique to isUnique (flip the value from false to true vis versa) @c0bra5
     *  TODO: 2019/OCT/31 Add Index Val to constructor to prevent reordering issues.
     */
    ChannelSetting(String name, boolean isNotUnique, String desc) {
        this.name = name;
        this.isSetting = isNotUnique;
        this.desc = desc;
    }

    public static ChannelSetting get(String type) {
        if (type.equalsIgnoreCase(FROM_DM.toString())) return null;
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


    /**
     * checks if the setting is actually a messageChannel setting
     */
    public boolean isSetting() {
        return isSetting;
    }

    ;

    /**
     * returns the setting description using a command as a description argument
     *
     * @param command the command used in the documentation
     * @return the description of the messageChannel setting
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
            builder.setTitle("Channel Setting - " + name);
        } else {
            builder.setTitle("Channel Type - " + name);
        }
        builder.setDescription(getDesc(object));
        List<TextChannel> channels = object.guild.getChannelsByType(this);
        channels = object.user.getVisibleChannels(channels);
        String title;
        if (isSetting()) {
            title = "Channels:";
        } else {
            title = "Channel:";
        }
        if (channels.size() != 0) {
            builder.addField(title, Utility.listFormatter(channels.stream().map(channel -> channel.getAsMention()).collect(Collectors.toList()), true), false);
        }
        List<Command> commands = object.guild.commands.stream().filter(command -> {
            if (command.channel != null && GuildHandler.testForPerms(object, command.perms)) {
                return command.channel == this;
            }
            return false;
        }).collect(Collectors.toList());
        List<String> typeCommands = commands.stream().map(command -> command.getCommand(object)).collect(Collectors.toList());
        if (commands.size() != 0) {
            builder.addField("Commands:", Utility.listFormatter(typeCommands, true), false);
        }
        return builder;
    }

    public String toggleSetting(CommandObject command) {
        ChannelSettingObject settingObject = command.guild.channelData.getChannelSetting(this);
        if (settingObject == null) settingObject = command.guild.channelData.initSetting(this);
        String mention = command.guildChannel.mention;
        long channelID = command.guildChannel.longID;
        if (isSetting) {
            String mode = "removed";
            if (!settingObject.getChannelIDs().removeIf(l -> l == channelID)) {
                settingObject.getChannelIDs().add(channelID);
                mode = "added";
            }
            return String.format("> %s, Channel setting: **%s** %s.", mention, name, mode);
        } else {
            boolean isAdding = true;
            if (settingObject.getChannelIDs().isEmpty()) {
                settingObject.getChannelIDs().add(channelID);
            } else {
                if (settingObject.getChannelIDs().get(0) == channelID) {
                    settingObject.getChannelIDs().clear();
                    isAdding = false;
                } else {
                    settingObject.getChannelIDs().set(0, channelID);
                }
            }
            return String.format("> %s is %s the Server's **%s** messageChannel.", mention, isAdding ? "is now" : "is no longer", name);
        }
    }
}

