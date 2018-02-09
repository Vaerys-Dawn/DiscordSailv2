package com.github.vaerys.templates;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.objects.SubCommandObject;
import com.github.vaerys.objects.XEmbedBuilder;
import org.apache.commons.lang3.ArrayUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Vaerys on 29/01/2017.
 */
public abstract class Command {
    //Type Constants
    public static final String TYPE_GENERAL = "General";
    public static final String TYPE_ADMIN = "Admin";
    public static final String TYPE_ROLE_SELECT = "Role";
    public static final String TYPE_CHARACTER = "Chars";
    public static final String TYPE_SERVERS = "Servers";
    public static final String TYPE_CC = "CC";
    public static final String TYPE_HELP = "Help";
    public static final String TYPE_COMPETITION = "Comp";
    public static final String TYPE_DM = "DM";
    public static final String TYPE_CREATOR = "Creator";
    public static final String TYPE_PIXEL = "Pixels";
    public static final String TYPE_GROUPS = "Groups";
    public static final String TYPE_SLASH = "Slash";
    public static final String TYPE_MENTION = "Mention";

    //Channel Constants
    public static final String CHANNEL_GENERAL = "General";
    public static final String CHANNEL_SERVERS = "Servers";
    public static final String CHANNEL_BOT_COMMANDS = "BotCommands";
    public static final String CHANNEL_SERVER_LOG = "ServerLog";
    public static final String CHANNEL_ADMIN_LOG = "AdminLog";
    public static final String CHANNEL_ADMIN = "Admin";
    public static final String CHANNEL_INFO = "Info";
    public static final String CHANNEL_SHITPOST = "ShitPost";
    public static final String CHANNEL_DONT_LOG = "DontLog";
    public static final String CHANNEL_ART = "Art";
    public static final String CHANNEL_GROUPS = "Groups";
    public static final String CHANNEL_XP_DENIED = "XpDenied";
    public static final String CHANNEL_PIXELS = "Pixels";
    public static final String CHANNEL_LEVEL_UP = "LevelUp";
    public static final String CHANNEL_LEVEL_UP_DENIED = "LevelUpDenied";
    public static final String CHANNEL_DM = "DirectMessages";

    public static final String spacer = "\u200B";
    public static final String indent = "    ";
    public static final String codeBlock = "```";
    public static final String ownerOnly = ">> ONLY THE BOT'S OWNER CAN RUN THIS <<";


    public List<SubCommandObject> subCommands = new LinkedList<>();

    public abstract String execute(String args, CommandObject command);

    //descriptors
    public abstract String[] names();

    public abstract String description(CommandObject command);

    public abstract String usage();

    public abstract String type();

    public abstract String channel();

    public abstract Permissions[] perms();

    public abstract boolean requiresArgs();

    public abstract boolean doAdminLogging();

    public abstract void init();

    public abstract String dualDescription();

    public abstract String dualUsage();

    public abstract String dualType();

    public abstract Permissions[] dualPerms();

    public String getCommand(CommandObject command) {
        return command.guild.config.getPrefixCommand() + names()[0];
    }

    public String getUsage(CommandObject command) {
        if (usage() == null || usage().isEmpty()) {
            return getCommand(command);
        } else {
            return getCommand(command) + " " + usage();
        }
    }

    public String getDualUsage(CommandObject command) {
        if (dualUsage() == null || dualUsage().isEmpty()) {
            return getUsage(command);
        } else {
            return getCommand(command) + " " + dualUsage();
        }
    }

    public String missingArgs(CommandObject command) {
        return ">> **" + getUsage(command) + "** <<";
    }

    public boolean isCall(String args, CommandObject command) {
        SplitFirstObject call = new SplitFirstObject(args);
        for (String s : names()) {
            if ((command.guild.config.getPrefixCommand() + s).equalsIgnoreCase(call.getFirstWord())) {
                return true;
            }
        }
        return false;
    }

    public String getArgs(String args, CommandObject command) {
        SplitFirstObject call = new SplitFirstObject(args);
        if (call.getRest() == null) {
            return "";
        }
        return call.getRest();
    }

    public XEmbedBuilder getCommandInfo(CommandObject command) {
        XEmbedBuilder infoEmbed = new XEmbedBuilder(command);

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
        infoEmbed.appendField("> Help - " + names()[0], builder.toString(), false);

        //Handle channels
        List<IChannel> channels = command.guild.getChannelsByType(channel());
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

    public String validate() {
        StringBuilder response = new StringBuilder();
        boolean isError = false;
        response.append(Utility.formatError(this));
        if (names().length == 0 || names()[0].isEmpty()) {
            response.append("   > Command name is empty.\n");
            isError = true;
        }
        if (description(new CommandObject()) == null || description(new CommandObject()).isEmpty()) {
            response.append("   > Command description is empty.\n");
            isError = true;
        }
        if (type() == null || type().isEmpty()) {
            response.append("   > Command type is empty.\n");
            isError = true;
        }
        if (requiresArgs() && (usage() == null || usage().isEmpty())) {
            response.append("   > Command usage is null when requiresArgs is true.\n");
            isError = true;
        }
        if (dualDescription() != null || dualType() != null) {
            if (dualType() == null || dualType().isEmpty()) {
                response.append("   > Command dual type is empty.\n");
                isError = true;
            }
            if (dualType().equalsIgnoreCase(type())) {
                response.append("   > Command dual type is equal to type.\n");
                isError = true;
            }
            if (dualDescription() == null || dualDescription().isEmpty()) {
                response.append("   > Command dual description is empty.\n");
                isError = true;
            }
            if (dualDescription().equalsIgnoreCase(description(new CommandObject()))) {
                response.append("   > Command dual description is equal to description.\n");
                isError = true;
            }
            if (dualUsage() == null || dualUsage().isEmpty()) {
                response.append("   > Command dual usage is empty.\n");
                isError = true;
            }
            if (usage() != null && usage().equalsIgnoreCase(dualUsage())) {
                response.append("   > Command dual usage is equal to usage.\n");
                isError = true;
            }
        }
        if (isError) {
            return response.toString();
        } else {
            return null;
        }
    }

    public boolean isSubtype(CommandObject command, String subType) {
        return command.message.get().getContent().toLowerCase().startsWith(command.guild.config.getPrefixCommand() + subType.toLowerCase());
    }
}
