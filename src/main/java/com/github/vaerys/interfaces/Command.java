package com.github.vaerys.interfaces;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.XEmbedBuilder;
import org.apache.commons.lang3.ArrayUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vaerys on 29/01/2017.
 */
public interface Command {
    //Type Constants
    String TYPE_GENERAL = "General";
    String TYPE_ADMIN = "Admin";
    String TYPE_ROLE_SELECT = "Role";
    String TYPE_CHARACTER = "Characters";
    String TYPE_SERVERS = "Servers";
    String TYPE_CC = "CC";
    String TYPE_HELP = "Help";
    String TYPE_COMPETITION = "Competition";
    String TYPE_DM = "DM";
    String TYPE_CREATOR = "Creator";
    String TYPE_PIXEL = "Pixels";
    String TYPE_GROUPS = "Groups";
    String TYPE_SLASH = "Slash";
    String TYPE_MENTION = "Mention";

    //Channel Constants
    String CHANNEL_GENERAL = "General";
    String CHANNEL_SERVERS = "Servers";
    String CHANNEL_BOT_COMMANDS = "BotCommands";
    String CHANNEL_SERVER_LOG = "ServerLog";
    String CHANNEL_ADMIN_LOG = "AdminLog";
    String CHANNEL_ADMIN = "Admin";
    String CHANNEL_INFO = "Info";
    String CHANNEL_SHITPOST = "ShitPost";
    String CHANNEL_DONT_LOG = "DontLog";
    String CHANNEL_ART = "Art";
    String CHANNEL_GROUPS = "Groups";
    String CHANNEL_XP_DENIED = "XpDenied";
    String CHANNEL_PIXELS = "Pixels";
    String CHANNEL_LEVEL_UP = "LevelUp";
    String CHANNEL_LEVEL_UP_DENIED = "LevelUpDenied";
    String CHANNEL_DM = "DirectMessages";

    String spacer = "\u200B";
    String indent = "    ";
    String codeBlock = "```";
    String ownerOnly = ">> ONLY THE BOT'S OWNER CAN RUN THIS <<";


    String execute(String args, CommandObject command);

    //descriptors
    String[] names();

    String description(CommandObject command);

    String usage();

    String type();

    String channel();

    Permissions[] perms();

    boolean requiresArgs();

    boolean doAdminLogging();

    String dualDescription();

    String dualUsage();

    String dualType();

    Permissions[] dualPerms();

    default String getCommand(CommandObject command) {
        return command.guild.config.getPrefixCommand() + names()[0];
    }

    default String getUsage(CommandObject command) {
        if (usage() == null || usage().isEmpty()) {
            return getCommand(command);
        } else {
            return getCommand(command) + " " + usage();
        }
    }

    default String getDualUsage(CommandObject command) {
        if (dualUsage() == null || dualUsage().isEmpty()) {
            return getUsage(command);
        } else {
            return getCommand(command) + " " + dualUsage();
        }
    }

    default String missingArgs(CommandObject command) {
        return ">> **" + getUsage(command) + "** <<";
    }

    default boolean isCall(String args, CommandObject command) {
        SplitFirstObject call = new SplitFirstObject(args);
        for (String s : names()) {
            if ((command.guild.config.getPrefixCommand() + s).equalsIgnoreCase(call.getFirstWord())) {
                return true;
            }
        }
        return false;
    }

    default String getArgs(String args, CommandObject command) {
        SplitFirstObject call = new SplitFirstObject(args);
        if (call.getRest() == null) {
            return "";
        }
        return call.getRest();
    }

    default XEmbedBuilder getCommandInfo(CommandObject command) {
        XEmbedBuilder infoEmbed = new XEmbedBuilder();
        Color color = command.client.color;
        if (color != null) {
            infoEmbed.withColor(color);
        }

        //command info
        StringBuilder builder = new StringBuilder();
        builder.append("**" + getUsage(command) + "**\n");
        builder.append("**Desc: **" + description(command) + "\n");
        builder.append("**Type: **" + type() + "\n");
        if (perms().length != 0) {
            builder.append("**Perms: **");
            ArrayList<String> permList = new ArrayList<>();
            for (Permissions p : perms()) {
                permList.add(p.toString());
            }
            builder.append(Utility.listFormatter(permList, true));
        }
        //dual command info
        if (dualType() != null && Utility.testForPerms(command, dualPerms())) {
            builder.append("\n**" + getDualUsage(command) + "**\n");
            builder.append("**Desc: **" + dualDescription() + "\n");
            builder.append("**Type: **" + dualType() + "\n");
            if (perms().length != 0 || dualPerms().length != 0) {
                Permissions[] perms;
                perms = ArrayUtils.addAll(dualPerms(), perms());
                builder.append("**Perms: **");
                ArrayList<String> permList = new ArrayList<>();
                for (Permissions p : perms) {
                    permList.add(p.toString());
                }
                builder.append(Utility.listFormatter(permList, true));
            }
        }
        infoEmbed.appendField("> Info - " + names()[0], builder.toString(), false);

        //Handle channels
        List<IChannel> channels = command.guild.config.getChannelsByType(channel(), command.guild);
        List<String> channelMentions = Utility.getChannelMentions(channels);

        //channel
        if (channelMentions.size() > 0) {
            if (channelMentions.size() == 1) {
                infoEmbed.appendField("Channel ", Utility.listFormatter(channelMentions, true), false);
            } else {
                infoEmbed.appendField("Channels ", Utility.listFormatter(channelMentions, true), false);
            }
        }

        //aliases
        if (names().length > 1) {
            StringBuilder aliasBuilder = new StringBuilder();
            for (int i = 1; i < names().length; i++) {
                aliasBuilder.append(command.guild.config.getPrefixCommand() + names()[i] + ", ");
            }
            aliasBuilder.delete(aliasBuilder.length() - 2, aliasBuilder.length());
            aliasBuilder.append(".\n");
            infoEmbed.appendField("Aliases", aliasBuilder.toString(), false);
        }
        return infoEmbed;
    }

    default String validate() {
        StringBuilder response = new StringBuilder();
        boolean isErrored = false;
        response.append("\n>> Begin Error Report: " + this.getClass().getName() + " <<\n");
        if (names().length == 0 || names()[0].isEmpty()) {
            response.append("> NAME IS EMPTY.\n");
            isErrored = true;
        }
        if (description(new CommandObject()) == null || description(new CommandObject()).isEmpty()) {
            response.append("> DESCRIPTION IS EMPTY.\n");
            isErrored = true;
        }
        if (type() == null || type().isEmpty()) {
            response.append("> TYPE IS EMPTY.\n");
            isErrored = true;
        }
        if (requiresArgs() && (usage() == null || usage().isEmpty())) {
            response.append("> USAGE IS NULL WHEN REQUIRES_ARGS IS TRUE.\n");
            isErrored = true;
        }
        if (dualDescription() != null || dualType() != null) {
            if (dualType() == null || dualType().isEmpty()) {
                response.append("> DUAL TYPE IS EMPTY.\n");
                isErrored = true;
            }
            if (dualType().equalsIgnoreCase(type())) {
                response.append("> DUAL TYPE IS EQUAL TO TYPE.\n");
                isErrored = true;
            }
            if (dualDescription() == null || dualDescription().isEmpty()) {
                response.append("> DUAL DESCRIPTION IS EMPTY.\n");
                isErrored = true;
            }
            if (dualDescription().equalsIgnoreCase(description(new CommandObject()))) {
                response.append("> DUAL DESCRIPTION IS EQUAL TO DESCRIPTION.\n");
                isErrored = true;
            }
            if (dualUsage() == null || dualUsage().isEmpty()) {
                response.append("> DUAL USAGE IS EMPTY.\n");
                isErrored = true;
            }
            if (usage() != null && usage().equalsIgnoreCase(dualUsage())) {
                response.append("> DUAL USAGE IS EQUAL TO USAGE.\n");
                isErrored = true;
            }
        }
        response.append(">> End Error Report <<");
        if (isErrored) {
            return response.toString();
        } else {
            return null;
        }
    }

    default boolean isSubtype(CommandObject command, String subType) {
        return command.message.get().getContent().toLowerCase().startsWith(command.guild.config.getPrefixCommand() + subType.toLowerCase());
    }
}
