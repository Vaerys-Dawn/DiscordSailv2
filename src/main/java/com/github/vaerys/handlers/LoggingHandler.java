package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.main.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoggingHandler {

    private final static Logger logger = LoggerFactory.getLogger(LoggingHandler.class);


    private static boolean isSailMessage(CommandObject command) {
        return command.client.bot.longID == command.user.longID;
    }

    private static void sendLog(CommandObject command, String message, boolean isAdmin, EmbedObject... object) {
        IChannel channel;
        if (isAdmin) channel = command.guild.getChannelByType(ChannelSetting.ADMIN_LOG);
        else channel = command.guild.getChannelByType(ChannelSetting.SERVER_LOG);
        if (channel == null) return;
        if (object.length == 0 || object[0] == null) RequestHandler.sendMessage(message, channel);
        else RequestHandler.sendEmbed(message, object[0], channel);
    }

    private static boolean shouldLog(CommandObject command) {
        if (command.guild == null) return false;
        if (!command.guild.config.moduleLogging) return false;
        IChannel info = command.guild.getChannelByType(ChannelSetting.INFO);
        IChannel serverLog = command.guild.getChannelByType(ChannelSetting.SERVER_LOG);
        IChannel adminLog = command.guild.getChannelByType(ChannelSetting.ADMIN_LOG);
        List<IChannel> dontLog = command.guild.getChannelsByType(ChannelSetting.DONT_LOG);
        if (dontLog.contains(command.channel.get())) return false;
        if (command.guild.config.dontLogBot && command.user.get().isBot()) return false;
        if (command.message.getContent().equals("`Working...`") && isSailMessage(command)) return false;
        if (command.message.getContent().equals("`Loading...`") && isSailMessage(command)) return false;
        if (serverLog != null && serverLog.equals(command.channel.get()) && isSailMessage(command)) return false;
        if (adminLog != null && adminLog.equals(command.channel.get()) && isSailMessage(command)) return false;
        if (info != null && info.equals(command.channel.get()) && isSailMessage(command)) return false;
        return true;
    }

    private static boolean messageEmpty(IMessage message) {
        return (message.getContent() == null || message.getContent().isEmpty()) &&
                message.getEmbeds().size() == 0 &&
                message.getAttachments().size() == 0;
    }

    private static int getVarsLength(List<String> vars) {
        int length = 0;
        for (String s : vars) {
            length += s.length();
        }
        return length;
    }

    private static String getFormattedTimeStamp(CommandObject command, IMessage message) {
        long difference = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - message.getTimestamp().atZone(ZoneOffset.UTC).toEpochSecond();
        StringBuffer formatted = new StringBuffer();
        if (command.guild.config.useTimeStamps) {
            formatted.append("at `").append(Utility.formatTimestamp(message.getTimestamp().atZone(ZoneOffset.UTC))).append(" - UTC`");
        } else {
            formatted.append("from ").append(Utility.formatTimeDifference(difference));
        }
        return formatted.toString();
    }

    public static void logDelete(CommandObject command, IMessage deletedMessage) {
        if (!shouldLog(command)) return;
        if (messageEmpty(deletedMessage)) return;
        StringBuffer content;
        StringBuffer extraContent = new StringBuffer();
        EmbedObject embed = null;
        int charLimit = 2000;
        String timestamp = getFormattedTimeStamp(command, deletedMessage);
        String format = "> **@%s's** Message %s was **Deleted** in channel: %s with contents:\n%s\n%s";
        //check embed
        if (deletedMessage.getEmbeds().size() != 0) embed = new EmbedObject(deletedMessage.getEmbeds().get(0));
        //add and attachments
        for (IMessage.Attachment atc : deletedMessage.getAttachments()) {
            if (extraContent.length() != 0) extraContent.append("\n");
            extraContent.append("<").append(atc.getUrl()).append(">");
        }
        //add all of the args
        List<String> vars = new ArrayList<String>() {{
            add(command.user.username);
            add(timestamp);
            add(command.channel.mention);
            add(extraContent.toString());
        }};
        //calculate the limit of content
        charLimit -= getVarsLength(vars);
        charLimit -= format.length() + Utility.embedToString(embed).length();
        //make sure that the length doesn't go over.
        content = new StringBuffer(Utility.unFormatMentions(command.message.get()));
        if (charLimit < 0) {
            logger.error("Message caused Charlimit to go under 0.\n" +
                    "Content:" + content +
                    "\nExtra Content:" + extraContent);
            return;
        }
        if (content.length() > charLimit) {
            content.setLength(charLimit);
            content.append("...");
        }
        vars.add(3, content.toString());
        content = new StringBuffer(String.format(format, vars.toArray()));
        sendLog(command, content.toString(), false, embed);
    }

    public static void logEdit(CommandObject command, IMessage editedMessage, IMessage oldMessage) {

    }

    public static void logJoinLeave(CommandObject command, IUser user) {

    }

    public static void logChannelCreate(CommandObject command, IChannel createdChannel) {

    }

    public static void logChannelDelete(CommandObject command, IChannel deletedChannel) {

    }

    public static void logChannelEdit(CommandObject command, IChannel editedChannel) {

    }

    public static void logUserRolesUpdate(CommandObject command, IUser user) {

    }

}
