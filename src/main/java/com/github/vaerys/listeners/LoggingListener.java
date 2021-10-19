package com.github.vaerys.listeners;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LoggingListener extends ListenerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(LoggingListener.class);


    private static final String JOIN_LEAVE_FORMAT = "> **@%s#%s** %s.\n**Current Users:** %s.";

    private static boolean isSailMessage(CommandObject command) {
        return command.user.longID == command.client.bot.longID;
    }

    private static void sendLog(String message, CommandObject command, boolean isAdmin, MessageEmbed... object) {
        sendLog(message, command.guild, isAdmin, object);
    }

    public static void sendLog(String message, GuildObject guild, boolean isAdmin, MessageEmbed... object) {
        TextChannel channel;
        if (isAdmin) channel = guild.getChannelByType(ChannelSetting.ADMIN_LOG);
        else channel = guild.getChannelByType(ChannelSetting.SERVER_LOG);
        if (channel == null) return;
        message = message.replaceAll("(?i)@(here|everyone)", "[REDACTED]");
        if (object.length == 0 || object[0] == null) channel.sendMessage(message).queue();
        else channel.sendMessage(message).embed(object[0]).queue();
    }

    private static boolean shouldLog(CommandObject command) {
        if (command.message == null) return false;
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
        if (dontLog.contains(command.guildChannel.get())) return false;
        if (command.guild.config.dontLogBot && command.user.get().isBot()) return false;
        if (chars200.equals("`Working...`") && isSailMessage(command)) return false;
        if (chars200.equals("`Loading...`") && isSailMessage(command)) return false;
        if (isSailMessage(command) && chars200.matches(pinRegex)) return false;
        if (isSailMessage(command) && command.message.get().getEmbeds().size() != 0) return false;
        if (serverLog != null && serverLog.equals(command.guildChannel.get()) && isSailMessage(command)) return false;
        if (adminLog != null && adminLog.equals(command.guildChannel.get()) && isSailMessage(command)) return false;
        if (info != null && info.equals(command.guildChannel.get()) && isSailMessage(command)) return false;
        return true;
    }

    private static boolean messageEmpty(Message message) {
        return (message.getContentRaw().isEmpty()) &&
                message.getEmbeds().isEmpty() &&
                message.getAttachments().isEmpty();
    }

    private static int getVarsLength(List<String> vars) {
        int length = 0;
        for (String s : vars) {
            length += s.length();
        }
        return length;
    }

    private static String getFormattedTimeStamp(CommandObject command, Message message) {
        return getFormattedTimeStamp(command, message.getTimeCreated().toInstant());
    }

    private static String getFormattedTimeStamp(CommandObject command, Instant timeStamp) {
        long difference = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - timeStamp.atZone(ZoneOffset.UTC).toEpochSecond();
        StringBuilder formatted = new StringBuilder();
        if (command.guild.config.useTimeStamps) {
            formatted.append("at `").append(Utility.formatTimestamp(timeStamp.atZone(ZoneOffset.UTC))).append(" - UTC`");
        } else {
            formatted.append("from ").append(Utility.formatTimeDifference(difference));
        }
        return formatted.toString();
    }

    public static void logDelete(CommandObject command, MessageDeleteEvent event) {
        if (!command.guild.config.deleteLogging) return;
        if (!shouldLog(command)) return;
        Instant time = TimeUtil.getTimeCreated(event.getMessageIdLong()).toInstant();
        String timestamp = getFormattedTimeStamp(command, time);
        String format = "> Message %s was **Deleted** in messageChannel: %s";
        //add all of the args
        List<String> vars = new ArrayList<>();
        vars.add(timestamp);
        vars.add(command.guildChannel.mention);
        sendLog(String.format(format, vars.toArray()), command, false);
    }


    //global updates:
    //messageChannel moved (Category changed), messageChannel name updated.
    private static final String channelUpdateFormat = "> %s's %s.\n%s";

//    public static void logChannelUpdate(GuildObject guild, GuildChannel oldChannel, GuildChannel newChannel) {
//        if (!guild.config.moduleLogging) return;
//        if (!guild.config.channelLogging) return;
//        boolean isVoice = newChannel.getType() == ChannelType.VOICE;
//        List<String> logs = new LinkedList<>();
//        String newChannelName;
//        if (newChannel instanceof TextChannel) {
//            newChannelName = ((TextChannel) newChannel).getAsMention();
//        } else {
//            newChannelName = newChannel.getName();
//        }
//        int oldPosition = oldChannel.getPosition();
//        int newPosition = newChannel.getPosition();
//        Category oldCategory = oldChannel.getParent();
//        Category newCategory = newChannel.getParent();
//        boolean categoryChanged = (!Objects.equals(oldCategory, newCategory));
//
//        if (!oldChannel.getName().equalsIgnoreCase(newChannel.getName())) {
//            logs.add(String.format(channelUpdateFormat, newChannelName, " name was **Updated**", String.format("**Old Name:** %s%s", (isVoice ? "" : "#"), oldChannel.getName())));
//        } else if (oldPosition != newPosition || categoryChanged) {
//            logs.add(logChannelMove(newChannelName, oldChannel, newChannel, guild));
//        }
//
//        if (isVoice) {
//            logs.add(logVoiceUpdate(newChannelName, (VoiceChannel) oldChannel, (VoiceChannel) newChannel));
//        } else {
//            if (newChannel instanceof TextChannel && oldChannel instanceof TextChannel) {
//                logs.add(logTextUpdate(newChannelName, (TextChannel) oldChannel, (TextChannel) newChannel));
//            }
//        }
//        logs = logs.stream().filter(l -> !l.isEmpty()).collect(Collectors.toList());
//        sendLog(String.join("\n", logs), guild, false);
//    }
//
//    //Text Only
//    //toggle NSFW, change topic, remove topic, add topic
//    private static String logTextUpdate(String channelName, TextChannel oldChannel, TextChannel newChannel) {
//        StringHandler action = new StringHandler();
//        StringHandler extraContent = new StringHandler();
//        String oldTopic = oldChannel.getTopic() == null ? "" : oldChannel.getTopic();
//        String newTopic = newChannel.getTopic() == null ? "" : newChannel.getTopic();
//        if (oldChannel.isNSFW() != newChannel.isNSFW()) {
//            action.appendFormatted("NSFW Tag was %s", (newChannel.isNSFW() ? "**Added**" : "**Removed**"));
//        }
//
//        if (!oldTopic.equalsIgnoreCase(newTopic)) {
//            if (!action.isEmpty()) action.append(" and the Channel's ");
//            if (oldTopic.isEmpty()) action.append("Topic was **Created**");
//            else if (newTopic.isEmpty()) action.append("Topic was **Removed**");
//            else action.append("Topic was **Updated**");
//
//            if (!oldTopic.isEmpty()) {
//                oldTopic = oldChannel.getTopic().length() < 300 ? oldChannel.getTopic() : oldChannel.getTopic().substring(0, 300).concat("...");
//                extraContent.appendFormatted("**Old Topic:** `%s`", oldTopic);
//            }
//            if (!newTopic.isEmpty()) {
//                if (!extraContent.isEmpty()) extraContent.append("\n");
//                newTopic = newChannel.getTopic().length() < 300 ? newChannel.getTopic() : newChannel.getTopic().substring(0, 300).concat("...");
//                extraContent.appendFormatted("**New Topic:** `%s`", newTopic);
//            }
//        }
//        if (!action.isEmpty()) {
//            return String.format(channelUpdateFormat, channelName, action, extraContent);
//        }
//        return "";
//    }
//
//    //Voice only
//    //Changed User Cap, Changed bitrate
//    private static String logVoiceUpdate(String channelName, VoiceChannel oldChannel, VoiceChannel newChannel) {
//        int oldUserCap = oldChannel.getUserLimit();
//        int newUserCap = newChannel.getUserLimit();
//        int oldBitrate = oldChannel.getBitrate() / 1000;
//        int newBitrate = newChannel.getBitrate() / 1000;
//        StringHandler action = new StringHandler();
//        StringHandler extraContent = new StringHandler();
//
//        if (oldUserCap != newUserCap) {
//            if (oldUserCap == 0) action.append("User Limit was **Added**");
//            else if (newUserCap == 0) action.append("User Limit was **Removed**");
//            else action.append("User Limit was **Updated**");
//            if (oldUserCap != 0) extraContent.appendFormatted("**Old User Limit:** %d", oldUserCap);
//            if (newUserCap != 0) {
//                if (!extraContent.isEmpty()) extraContent.append(", ");
//                extraContent.appendFormatted("**New User Limit:** %d", newUserCap);
//            }
//        }
//        if (oldBitrate != newBitrate) {
//            if (!action.isEmpty()) action.append(" and the Channel's ");
//            if (!extraContent.isEmpty()) extraContent.append("\n");
//            action.append("Bitrate was **Updated**");
//            extraContent.appendFormatted("**Old Bitrate:** %dkbps, **New Bitrate:** %dkbps", oldBitrate, newBitrate);
//        }
//        if (!action.isEmpty()) {
//            return String.format(channelUpdateFormat, channelName, action, extraContent);
//        }
//        return "";
//    }
//
//    private static String logChannelMove(String channelName, GuildChannel oldChannel, GuildChannel newChannel, GuildObject guild) {
//        int oldPosition = oldChannel.getPosition();
//        int newPosition = newChannel.getPosition();
//        Category oldCategory = oldChannel.getParent();
//        Category newCategory = newChannel.getParent();
//        String oldCategoryName = oldCategory == null ? "No Category" : oldCategory.getName();
//        String newCategoryName = newCategory == null ? "No Category" : newCategory.getName();
//        StringHandler action = new StringHandler();
//        StringHandler extraContent = new StringHandler();
//
//        if (!oldCategory.equals(newCategory)) {
//            if (!action.isEmpty()) action.append(" and it's ");
//            if (!extraContent.isEmpty()) extraContent.append("\n");
//            action.append("Category was **Changed**");
//            extraContent.appendFormatted("**Old Category:** %s, **New Category:** %s", oldCategoryName, newCategoryName);
////            if ((newChannel instanceof VoiceChannel ? guild.get().getVoiceChannels().size() : guild.get().getChannels().size()) > 1) {
////                extraContent.appendFormatted("\n**Old Position:** %d, **New Position:** %d", oldPosition, newPosition);
////            }
//        } else if (oldPosition != newPosition) {
//            action.append("Position was **Updated**");
//            extraContent.appendFormatted("**Old Position:** %d, **New Position:** %d", oldPosition, newPosition);
//        }
//
//        if (!action.isEmpty()) {
//            return String.format(channelUpdateFormat, channelName, action, extraContent);
//        }
//        return "";
//    }


    @Override
    public void onTextChannelDelete(@NotNull TextChannelDeleteEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getIdLong());
        if (!content.config.moduleLogging) return;
        if (content.config.channelLogging) {
            String log = "> Channel #" + event.getChannel().getName() + " was deleted.";
            LoggingListener.sendLog(log, content, false);
        }
        updateVariables(event.getChannel().getGuild());
    }

    @Override
    public void onVoiceChannelDelete(@NotNull VoiceChannelDeleteEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getIdLong());
        if (!content.config.moduleLogging) return;
        if (content.config.channelLogging) {
            String log = "> Channel " + event.getChannel().getName() + " was deleted.";
            LoggingListener.sendLog(log, content, false);
        }
        updateVariables(event.getChannel().getGuild());
    }

    public static void updateVariables(Guild guild) {
        long guildID = guild.getIdLong();
        GuildObject guildObject = Globals.getGuildContent(guildID);
        guildObject.config.updateVariables(guild);
        guildObject.characters.updateVars(guild);
    }

    @Override
    public void onTextChannelCreate(@NotNull TextChannelCreateEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getIdLong());
        if (!content.config.moduleLogging) return;
        if (content.config.channelLogging) {
            String log = "> Channel " + event.getChannel().getAsMention() + " was created.";
            sendLog(log, content, false);
        }
    }

    @Override
    public void onVoiceChannelCreate(@NotNull VoiceChannelCreateEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getIdLong());
        if (!content.config.moduleLogging) return;
        if (content.config.channelLogging) {
            String log = "> Channel " + event.getChannel().getName() + " was created.";
            sendLog(log, content, false);
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getIdLong());
        if (!content.config.moduleLogging) return;
        String output = String.format(JOIN_LEAVE_FORMAT, event.getUser().getName(), event.getUser().getDiscriminator(), "has **Joined** the server", event.getGuild().getMemberCount());
        if (content.config.joinLeaveLogging) {
            sendLog(output, content, false);
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getIdLong());
        if (!content.config.moduleLogging) return;
        String output = String.format(JOIN_LEAVE_FORMAT, event.getUser().getName(), event.getUser().getDiscriminator(), "has **Left** the server", event.getGuild().getMemberCount());
        if (content.config.joinLeaveLogging) {
            if (content.config.kickLogging) {
                doKickLog(event.getGuild(), event.getUser());
            }
            sendLog(output, content, false);
        }
    }


    public static void doMessageEditLog(CommandObject command) {
        if (!shouldLog(command)) return;

        if (!command.guild.config.editLogging) return;
        //formats how long ago this was.
        if (command.message.get().getTimeEdited() != null) {
            String response = String.format("> **@%s's** Message %s was **Edited** in messageChannel: %s.", command.user.username,
                    getFormattedTimeStamp(command, Objects.requireNonNull(command.message.get().getTimeEdited()).toInstant()), command.guildChannel.mention);
            sendLog(response, command.guild, false);
        }
    }

//    public static void doRoleUpdateLog() {
//        Guild guild = event.getGuild();
//        UserObject user = new UserObject(event.getUser(), Globals.getGuildContent(event.getGuild().getIdLong()));
//        GuildObject content = Globals.getGuildContent(guild.getIdLong());
//        if (!content.config.moduleLogging) return;
//        if (content.config.userRoleLogging) {
//            ArrayList<String> oldRoles = new ArrayList<>();
//            ArrayList<String> newRoles = new ArrayList<>();
//            oldRoles.addAll(event.getOldRoles().stream().filter(r -> !r.isEveryoneRole()).map(Role::getName).collect(Collectors.toList()));
//            newRoles.addAll(event.getNewRoles().stream().filter(r -> !r.isEveryoneRole()).map(Role::getName).collect(Collectors.toList()));
//            StringBuilder oldRoleList = new StringBuilder();
//            StringBuilder newRoleList = new StringBuilder();
//            for (String r : oldRoles) {
//                oldRoleList.append(r + ", ");
//            }
//            for (String r : newRoles) {
//                newRoleList.append(r + ", ");
//            }
//            String prefix = "> **@" + user.username + "'s** Role have been Updated.";
//            sendLog(prefix + "\nOld Roles: " + Utility.listFormatter(oldRoles, true) + "\nNew Roles: " + Utility.listFormatter(newRoles, true), content, false);
//        }
//    }

    /***
     * Handler for logging Kicks.
     *
     * @param guild the Guild the globalUser left.
     * @param user  the User that left the server.
     */
    private void doKickLog(Guild guild, User user) {
        Member botUser = guild.getMember(Client.getClient().getSelfUser());
        //test if the bot has auditLog perms
        if (!GuildHandler.testForPerms(botUser, guild, Permission.VIEW_AUDIT_LOGS)) return;

        //getTimestampZone
        long timeStamp = Instant.now().atZone(ZoneOffset.UTC).toEpochSecond() * 1000;

        //build Message

        StringHandler kickLog = new StringHandler("**@%s#%s** has been **Kicked** by **@%s#%s**");

        // do some checks to make sure the globalUser was in fact kicked
        List<AuditLogEntry> kicksLog = guild.retrieveAuditLogs().stream().filter(auditLogEntry -> {
            return auditLogEntry.getType() == ActionType.KICK && auditLogEntry.getTargetIdLong() == user.getIdLong();
        }).collect(Collectors.toList());
        if (kicksLog.isEmpty()) return;

        //sort kickLog and get latest entry
        kicksLog.sort(Comparator.comparingLong(o -> TimeUtil.getTimeCreated(o.getIdLong()).toInstant().toEpochMilli()));
        AuditLogEntry lastKick = kicksLog.get(kicksLog.size() - 1);

        //get the latest entry's timestamp
        long lastKickTime = TimeUtil.getTimeCreated(lastKick.getIdLong()).toInstant().toEpochMilli();

        //get globalUser responsible
        User responsible = lastKick.getUser();
        if (responsible == null) return;

        // Check if timestamp is within fifteen seconds either way, lastKick is valid.
        long timeDiff = Math.abs(timeStamp - lastKickTime);
        if (timeDiff > 15000) return;

        //format and send message
        kickLog.format(user.getName(), user.getDiscriminator(), responsible.getAsTag());
        if (lastKick.getReason() != null) {
            kickLog.appendFormatted(" with reason `%s`", lastKick.getReason());
        }

        //send log
        GuildObject content = Globals.getGuildContent(guild.getIdLong());
        sendLog(kickLog.toString(), content, true);
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        Guild guild = event.getGuild();
        GuildObject guildObject = Globals.getGuildContent(guild.getIdLong());
        if (!guildObject.config.moduleLogging || !guildObject.config.banLogging) return;
        if (!GuildHandler.testForPerms(guild.getMember(Client.getClient().getSelfUser()), guild, Permission.VIEW_AUDIT_LOGS))
            return;
        StringHandler output = new StringHandler("> **@%s#%s** was banned");
        output.setContent(String.format(output.toString(), event.getUser().getName(), event.getUser().getDiscriminator()));

        // get recent bans
        List<AuditLogEntry> recentBans = event.getGuild().retrieveAuditLogs().complete().stream()
                .filter(auditLogEntry -> auditLogEntry.getType() == ActionType.BAN && auditLogEntry.getTargetIdLong() == event.getUser().getIdLong())
                .collect(Collectors.toList());
        if (recentBans.isEmpty()) return;
        // and sort them. last entry is most recent.
        recentBans.sort(Comparator.comparingLong(o -> TimeUtil.getTimeCreated(o.getIdLong()).toInstant().toEpochMilli()));

        AuditLogEntry lastBan = recentBans.get(recentBans.size() - 1);
        String user = "Unknown_User#0000";
        if (lastBan.getUser() != null) user = lastBan.getUser().getAsTag();
        output.appendFormatted(" by **@%s**", user);
        String reason = lastBan.getReason() != null ? lastBan.getReason() : "No reason provided";
        output.appendFormatted(" with reason `%s`", reason);
        sendLog(output.toString(), guildObject, true);
    }

}
