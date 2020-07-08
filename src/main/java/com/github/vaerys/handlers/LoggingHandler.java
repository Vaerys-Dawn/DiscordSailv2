package com.github.vaerys.handlers;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.internal.DiscordUtils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.audit.ActionType;
import sx.blah.discord.handle.audit.entry.AuditLogEntry;
import sx.blah.discord.handle.audit.entry.TargetedEntry;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.member.GuildMemberEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.impl.obj.VoiceChannel;
import sx.blah.discord.handle.obj.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LoggingHandler {

    private final static Logger logger = LoggerFactory.getLogger(LoggingHandler.class);


    private static boolean isSailMessage(CommandObject command) {
        return command.user.longID == command.client.bot.longID;
    }

    private static void sendLog(String message, CommandObject command, boolean isAdmin, EmbedObject... object) {
        sendLog(message, command.guild, isAdmin, object);
    }

    public static void sendLog(String message, GuildObject guild, boolean isAdmin, EmbedObject... object) {
        TextChannel channel;
        if (isAdmin) channel = guild.getChannelByType(ChannelSetting.ADMIN_LOG);
        else channel = guild.getChannelByType(ChannelSetting.SERVER_LOG);
        if (channel == null) return;
        message = message.replaceAll("(?i)@(here|everyone)", "[REDACTED]");
        if (object.length == 0 || object[0] == null) RequestHandler.sendMessage(message, channel);
        else RequestHandler.sendEmbed(message, object[0], channel);
    }

    private static boolean shouldLog(CommandObject command) {
        String chars200 = command.message.getContent();
        //pin regex handler
        String pinRegex = "> (\\*\\*.*?\\*\\*|I have pinned) Has pinned (\\*\\*.*'s\\*\\*|their) art(\\n|.)*";
        if (command.message.getContent().length() > 200) chars200 = command.message.getContent().substring(0, 200);
        if (command.guild == null) return false;
        if (!command.guild.config.moduleLogging) return false;
        TextChannel info = command.guild.getChannelByType(ChannelSetting.INFO);
        TextChannel serverLog = command.guild.getChannelByType(ChannelSetting.SERVER_LOG);
        TextChannel adminLog = command.guild.getChannelByType(ChannelSetting.ADMIN_LOG);
        List<TextChannel> dontLog = command.guild.getChannelsByType(ChannelSetting.DONT_LOG);
        if (dontLog.contains(command.channel.get())) return false;
        if (command.guild.config.dontLogBot && command.user.get().isBot()) return false;
        if (chars200.equals("`Working...`") && isSailMessage(command)) return false;
        if (chars200.equals("`Loading...`") && isSailMessage(command)) return false;
        if (isSailMessage(command) && chars200.matches(pinRegex)) return false;
        if (isSailMessage(command) && command.message.get().getEmbeds().size() != 0) return false;
        if (serverLog != null && serverLog.equals(command.channel.get()) && isSailMessage(command)) return false;
        if (adminLog != null && adminLog.equals(command.channel.get()) && isSailMessage(command)) return false;
        if (info != null && info.equals(command.channel.get()) && isSailMessage(command)) return false;
        return true;
    }

    private static boolean messageEmpty(Message message) {
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

    private static String getFormattedTimeStamp(CommandObject command, Message message) {
        long difference = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - message.getTimestamp().atZone(ZoneOffset.UTC).toEpochSecond();
        StringBuffer formatted = new StringBuffer();
        if (command.guild.config.useTimeStamps) {
            formatted.append("at `").append(Utility.formatTimestamp(message.getTimestamp().atZone(ZoneOffset.UTC))).append(" - UTC`");
        } else {
            formatted.append("from ").append(Utility.formatTimeDifference(difference));
        }
        return formatted.toString();
    }

    public static void logDelete(CommandObject command, Message deletedMessage) {
        if (!command.guild.config.deleteLogging) return;
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
        for (Message.Attachment atc : deletedMessage.getAttachments()) {
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
        sendLog(content.toString(), command, false, embed);
    }


    //global updates:
    //channel moved (Category changed), channel name updated.
    private static final String channelUpdateFormat = "> %s's %s.\n%s";

    public static void logChannelUpdate(GuildObject guild, TextChannel oldChannel, TextChannel newChannel) {
        if (!guild.config.moduleLogging) return;
        if (!guild.config.channelLogging) return;
        boolean isVoice = newChannel instanceof VoiceChannel;
        List<String> logs = new LinkedList<>();
        String newChannelName = isVoice ? String.format("**%s**", newChannel.getName()) : newChannel.mention();
        int oldPosition = oldChannel.getPosition();
        int newPosition = newChannel.getPosition();
        ICategory oldCategory = oldChannel.getCategory();
        ICategory newCategory = newChannel.getCategory();
        boolean categoryChanged = (oldCategory == null ? !newCategory.equals(oldCategory) : !oldCategory.equals(newCategory));

        if (!oldChannel.getName().equalsIgnoreCase(newChannel.getName())) {
            logs.add(String.format(channelUpdateFormat, newChannelName, " name was **Updated**", String.format("**Old Name:** %s%s", (isVoice ? "" : "#"), oldChannel.getName())));
        } else if (oldPosition != newPosition || categoryChanged) {
            logs.add(logChannelMove(newChannelName, oldChannel, newChannel, guild));
        }

        if (isVoice) {
            logs.add(logVoiceUpdate(newChannelName, (VoiceChannel) oldChannel, (VoiceChannel) newChannel));
        } else {
            logs.add(logTextUpdate(newChannelName, oldChannel, newChannel));
        }
        logs = logs.stream().filter(l -> !l.isEmpty()).collect(Collectors.toList());
        sendLog(String.join("\n", logs), guild, false);
    }

    //Text Only
    //toggle NSFW, change topic, remove topic, add topic
    private static String logTextUpdate(String channelName, TextChannel oldChannel, TextChannel newChannel) {
        StringHandler action = new StringHandler();
        StringHandler extraContent = new StringHandler();
        String oldTopic = oldChannel.getTopic() == null ? "" : oldChannel.getTopic();
        String newTopic = newChannel.getTopic() == null ? "" : newChannel.getTopic();
        if (oldChannel.isNSFW() != newChannel.isNSFW()) {
            action.appendFormatted("NSFW Tag was %s", (newChannel.isNSFW() ? "**Added**" : "**Removed**"));
        }

        if (!oldTopic.equalsIgnoreCase(newTopic)) {
            if (!action.isEmpty()) action.append(" and the Channel's ");
            if (oldTopic.isEmpty()) action.append("Topic was **Created**");
            else if (newTopic.isEmpty()) action.append("Topic was **Removed**");
            else action.append("Topic was **Updated**");

            if (!oldTopic.isEmpty()) {
                oldTopic = oldChannel.getTopic().length() < 300 ? oldChannel.getTopic() : oldChannel.getTopic().substring(0, 300).concat("...");
                extraContent.appendFormatted("**Old Topic:** `%s`", oldTopic);
            }
            if (!newTopic.isEmpty()) {
                if (!extraContent.isEmpty()) extraContent.append("\n");
                newTopic = newChannel.getTopic().length() < 300 ? newChannel.getTopic() : newChannel.getTopic().substring(0, 300).concat("...");
                extraContent.appendFormatted("**New Topic:** `%s`", newTopic);
            }
        }
        if (!action.isEmpty()) {
            return String.format(channelUpdateFormat, channelName, action, extraContent);
        }
        return "";
    }

    //Voice only
    //Changed User Cap, Changed bitrate
    private static String logVoiceUpdate(String channelName, VoiceChannel oldChannel, VoiceChannel newChannel) {
        int oldUserCap = oldChannel.getUserLimit();
        int newUserCap = newChannel.getUserLimit();
        int oldBitrate = oldChannel.getBitrate() / 1000;
        int newBitrate = newChannel.getBitrate() / 1000;
        StringHandler action = new StringHandler();
        StringHandler extraContent = new StringHandler();

        if (oldUserCap != newUserCap) {
            if (oldUserCap == 0) action.append("User Limit was **Added**");
            else if (newUserCap == 0) action.append("User Limit was **Removed**");
            else action.append("User Limit was **Updated**");
            if (oldUserCap != 0) extraContent.appendFormatted("**Old User Limit:** %d", oldUserCap);
            if (newUserCap != 0) {
                if (!extraContent.isEmpty()) extraContent.append(", ");
                extraContent.appendFormatted("**New User Limit:** %d", newUserCap);
            }
        }
        if (oldBitrate != newBitrate) {
            if (!action.isEmpty()) action.append(" and the Channel's ");
            if (!extraContent.isEmpty()) extraContent.append("\n");
            action.append("Bitrate was **Updated**");
            extraContent.appendFormatted("**Old Bitrate:** %dkbps, **New Bitrate:** %dkbps", oldBitrate, newBitrate);
        }
        if (!action.isEmpty()) {
            return String.format(channelUpdateFormat, channelName, action, extraContent);
        }
        return "";
    }

    private static String logChannelMove(String channelName, TextChannel oldChannel, TextChannel newChannel, GuildObject guild) {
        int oldPosition = oldChannel.getPosition();
        int newPosition = newChannel.getPosition();
        ICategory oldCategory = oldChannel.getCategory();
        ICategory newCategory = newChannel.getCategory();
        String oldCategoryName = oldCategory == null ? "No Category" : oldCategory.getName();
        String newCategoryName = newCategory == null ? "No Category" : newCategory.getName();
        StringHandler action = new StringHandler();
        StringHandler extraContent = new StringHandler();

        if (!oldCategory.equals(newCategory)) {
            if (!action.isEmpty()) action.append(" and it's ");
            if (!extraContent.isEmpty()) extraContent.append("\n");
            action.append("Category was **Changed**");
            extraContent.appendFormatted("**Old Category:** %s, **New Category:** %s", oldCategoryName, newCategoryName);
//            if ((newChannel instanceof VoiceChannel ? guild.get().getVoiceChannels().size() : guild.get().getChannels().size()) > 1) {
//                extraContent.appendFormatted("\n**Old Position:** %d, **New Position:** %d", oldPosition, newPosition);
//            }
        } else if (oldPosition != newPosition) {
            action.append("Position was **Updated**");
            extraContent.appendFormatted("**Old Position:** %d, **New Position:** %d", oldPosition, newPosition);
        }

        if (!action.isEmpty()) {
            return String.format(channelUpdateFormat, channelName, action, extraContent);
        }
        return "";
    }

    public static void doChannelDeleteLog(ChannelDeleteEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getIdLong());
        if (!content.config.moduleLogging) return;
        if (content.config.channelLogging) {
            String log = "> Channel #" + event.getChannel().getName() + " was deleted.";
            sendLog(log, content, false);
        }
    }

    public static void doChannelCreateLog(ChannelCreateEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getIdLong());
        if (!content.config.moduleLogging) return;
        if (content.config.channelLogging) {
            String log = "> Channel " + event.getChannel().mention() + " was created.";
            sendLog(log, content, false);
        }
    }

    public static void doJoinLeaveLog(GuildMemberEvent event, boolean joining) {
        Guild guild = event.getGuild();
        GuildObject content = Globals.getGuildContent(guild.getIdLong());
        if (!content.config.moduleLogging) return;

        String output = "> **@%s#%s** %s.\n**Current Users:** %s."; //name, descriminator, thinger, usercount.
        output = String.format(output, event.getUser().getName(), event.getUser().getDiscriminator(), "%s", event.getGuild().getUsers().size());
        //String builder = "> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** has **%s** the server.\n**Current Users:** " + event.getGuild().getUsers().size() + ".";

        if (content.config.joinLeaveLogging) {
            if (joining) {
                sendLog(String.format(output, "has **Joined** the server"), content, false);
            } else {
                if (content.config.kickLogging) {
                    doKickLog(guild, event.getUser());
                }
                sendLog(String.format(output, "has **Left** the server"), content, false);
            }
        }
    }

    public static void doMessageEditLog(CommandObject command, Message oldMessage, Message newMessage) {
        if (!shouldLog(command)) return;

        if (!command.guild.config.editLogging) return;
        //formats how long ago this was.
        int charLimit;
        if (command.guild.config.extendEditLog) {
            charLimit = 900;
        } else {
            charLimit = 1800;
        }

        String oldContent = oldMessage.getContent() == null ? "" : Utility.unFormatMentions(oldMessage);
        String newContent = newMessage.getContent() == null ? "" : Utility.unFormatMentions(newMessage);
        oldContent = oldContent.length() > charLimit ? oldContent.substring(0, charLimit).concat("...") : oldContent;
        newContent = newContent.length() > charLimit ? newContent.substring(0, charLimit).concat("...") : newContent;
        StringBuilder extraContent = new StringBuilder();

        if (command.message.get().getContent().isEmpty()) return;

        extraContent.append("**\nMessage's Old Contents:**\n" + oldContent);
        if (command.guild.config.extendEditLog) {
            extraContent.append("\n**Message's New Contents:**\n" + newContent);
        }

        String response = String.format("> **@%s's** Message %s was **Edited** in channel: %s.\n%s", command.user.username,
                getFormattedTimeStamp(command, oldMessage), command.channel.mention, extraContent);
        sendLog(response, command.guild, false);
    }

    public static void doRoleUpdateLog(UserRoleUpdateEvent event) {
        Guild guild = event.getGuild();
        UserObject user = new UserObject(event.getUser(), Globals.getGuildContent(event.getGuild().getIdLong()));
        GuildObject content = Globals.getGuildContent(guild.getIdLong());
        if (!content.config.moduleLogging) return;
        if (content.config.userRoleLogging) {
            ArrayList<String> oldRoles = new ArrayList<>();
            ArrayList<String> newRoles = new ArrayList<>();
            oldRoles.addAll(event.getOldRoles().stream().filter(r -> !r.isEveryoneRole()).map(Role::getName).collect(Collectors.toList()));
            newRoles.addAll(event.getNewRoles().stream().filter(r -> !r.isEveryoneRole()).map(Role::getName).collect(Collectors.toList()));
            StringBuilder oldRoleList = new StringBuilder();
            StringBuilder newRoleList = new StringBuilder();
            for (String r : oldRoles) {
                oldRoleList.append(r + ", ");
            }
            for (String r : newRoles) {
                newRoleList.append(r + ", ");
            }
            String prefix = "> **@" + user.username + "'s** Role have been Updated.";
            sendLog(prefix + "\nOld Roles: " + Utility.listFormatter(oldRoles, true) + "\nNew Roles: " + Utility.listFormatter(newRoles, true), content, false);
        }
    }

    /***
     * Handler for logging Kicks.
     *
     * @param guild the Guild the user left.
     * @param user  the User that left the server.
     */
    private static void doKickLog(Guild guild, IUser user) {
        IUser botUser = Client.getClient().getOurUser();
        //test if the bot has auditLog perms
        if (!GuildHandler.testForPerms(botUser, guild, Permissions.VIEW_AUDIT_LOG)) return;

        //getTimestampZone
        long timeStamp = Instant.now().atZone(ZoneOffset.UTC).toEpochSecond() * 1000;

        //build Message

        StringHandler kickLog = new StringHandler("**@%s#%s** has been **Kicked** by **@%s#%s**");

        // do some checks to make sure the user was in fact kicked
        List<TargetedEntry> kicksLog = guild.getAuditLog(ActionType.MEMBER_KICK).getEntriesByTarget(user.getIdLong());
        if (kicksLog.size() == 0) return;

        //sort kickLog and get latest entry
        kicksLog.sort(Comparator.comparingLong(o -> DiscordUtils.getSnowflakeTimeFromID(o.getIdLong()).toEpochMilli()));
        AuditLogEntry lastKick = kicksLog.get(kicksLog.size() - 1);

        //get the latest entry's timestamp
        long lastKickTime = DiscordUtils.getSnowflakeTimeFromID(lastKick.getIdLong()).toEpochMilli();

        //get user responsible
        IUser responsible = lastKick.getResponsibleUser();

        // Check if timestamp is within fifteen seconds either way, lastKick is valid.
        long timeDiff = Math.abs(timeStamp - lastKickTime);
        if (timeDiff > 15000) return;

        //format and send message
        kickLog.format(user.getName(), user.getDiscriminator(), responsible.getName(), responsible.getDiscriminator());
        if (lastKick.getReason().isPresent()) {
            kickLog.appendFormatted(" with reason `%s`", lastKick.getReason().get());
        }

        //send log
        GuildObject content = Globals.getGuildContent(guild.getIdLong());
        sendLog(kickLog.toString(), content, true);
    }


    public static void doBanLog(UserBanEvent event) {
        Guild guild = event.getGuild();
        GuildObject guildObject = Globals.getGuildContent(guild.getIdLong());
        if (!guildObject.config.moduleLogging || !guildObject.config.banLogging) return;
        if (!GuildHandler.testForPerms(Client.getClient().getOurUser(), guild, Permissions.VIEW_AUDIT_LOG)) return;
        StringHandler output = new StringHandler("> **@%s#%s** was banned");
        output.setContent(String.format(output.toString(), event.getUser().getName(), event.getUser().getDiscriminator()));

        // get recent bans
        List<TargetedEntry> recentBans = event.getGuild().getAuditLog(ActionType.MEMBER_BAN_ADD).getEntriesByTarget(event.getUser().getIdLong());
        if (recentBans.size() == 0) return;
        // and sort them. last entry is most recent.
        recentBans.sort(Comparator.comparingLong(o -> DiscordUtils.getSnowflakeTimeFromID(o.getIdLong()).toEpochMilli()));

        AuditLogEntry lastBan = recentBans.get(recentBans.size() - 1);
        output.appendFormatted(" by **@%s#%s**", lastBan.getResponsibleUser().getName(), lastBan.getResponsibleUser().getDiscriminator());
        String reason = lastBan.getReason().isPresent() ? lastBan.getReason().get() : "No reason provided";
        output.appendFormatted(" with reason `%s`", reason);
        sendLog(output.toString(), guildObject, true);
    }
}
