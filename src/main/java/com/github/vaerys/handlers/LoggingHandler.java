package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.internal.DiscordUtils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.audit.ActionType;
import sx.blah.discord.handle.audit.entry.AuditLogEntry;
import sx.blah.discord.handle.audit.entry.TargetedEntry;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.member.GuildMemberEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.obj.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LoggingHandler {

    private final static Logger logger = LoggerFactory.getLogger(LoggingHandler.class);


    private static boolean isSailMessage(CommandObject command) {
        return command.user.get().isBot();
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
        String chars200 = command.message.getContent();
        //pin regex handler
        String pinRegex = "> (\\*\\*.*?\\*\\*|I have pinned) Has pinned (\\*\\*.*'s\\*\\*|their) art(\\n|.)*";
        if (command.message.getContent().length() > 200) chars200 = command.message.getContent().substring(0, 200);
        if (command.guild == null) return false;
        if (!command.guild.config.moduleLogging) return false;
        IChannel info = command.guild.getChannelByType(ChannelSetting.INFO);
        IChannel serverLog = command.guild.getChannelByType(ChannelSetting.SERVER_LOG);
        IChannel adminLog = command.guild.getChannelByType(ChannelSetting.ADMIN_LOG);
        List<IChannel> dontLog = command.guild.getChannelsByType(ChannelSetting.DONT_LOG);
        if (dontLog.contains(command.channel.get())) return false;
        if (command.guild.config.dontLogBot && command.user.get().isBot()) return false;
        if (chars200.equals("`Working...`") && isSailMessage(command)) return false;
        if (chars200.equals("`Loading...`") && isSailMessage(command)) return false;
        if (isSailMessage(command) && chars200.matches(pinRegex)) return false;
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

    public static void doChannelUpdateLog(ChannelUpdateEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getLongID());
        if (event.getNewChannel() instanceof IVoiceChannel) {
            // TODO: 26/03/2018 Add support for voice Channels.
            return;
        }
        if (!content.config.moduleLogging) return;
        if (content.config.channelLogging) {
            if (!event.getOldChannel().getName().equalsIgnoreCase(event.getNewChannel().getName())) {
                String log = "> Channel " + event.getNewChannel().mention() + "'s name was changed.\nOld name : #" + event.getOldChannel().getName() + ".";
                Utility.sendLog(log, content, false);
            } else if ((event.getOldChannel().getTopic() == null || event.getOldChannel().getTopic().isEmpty()) && (event.getNewChannel().getTopic() == null || event.getNewChannel().getTopic().isEmpty())) {
                //do nothing
            } else if ((event.getOldChannel().getTopic() == null || event.getOldChannel().getTopic().isEmpty()) && (event.getNewChannel().getTopic() != null || !event.getNewChannel().getTopic().isEmpty())) {
                StringBuilder log = new StringBuilder("> Channel " + event.getNewChannel().mention() + "'s Channel topic was added.\n");
                log.append("**New Topic**: " + event.getNewChannel().getTopic());
                Utility.sendLog(log.toString(), content, false);
            } else if ((event.getOldChannel().getTopic() != null || !event.getOldChannel().getTopic().isEmpty()) && (event.getNewChannel().getTopic() == null || event.getNewChannel().getTopic().isEmpty())) {
                StringBuilder log = new StringBuilder("> Channel " + event.getNewChannel().mention() + "'s Channel topic was removed.\n");
                log.append("**Old Topic**: " + event.getOldChannel().getTopic());
                Utility.sendLog(log.toString(), content, false);
            } else if (!event.getOldChannel().getTopic().equalsIgnoreCase(event.getNewChannel().getTopic())) {
                StringBuilder log = new StringBuilder("> Channel " + event.getNewChannel().mention() + "'s Channel topic was changed.");
                log.append("\n**Old Topic**: " + event.getOldChannel().getTopic());
                log.append("\n**New Topic**: " + event.getNewChannel().getTopic());
                Utility.sendLog(log.toString(), content, false);
            }
        }
    }

    public static void doChannelDeleteLog(ChannelDeleteEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getLongID());
        if (!content.config.moduleLogging) return;
        if (content.config.channelLogging) {
            String log = "> Channel #" + event.getChannel().getName() + " was deleted.";
            Utility.sendLog(log, content, false);
        }
    }

    public static void doChannelCreateLog(ChannelCreateEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getLongID());
        if (!content.config.moduleLogging) return;
        if (content.config.channelLogging) {
            String log = "> Channel " + event.getChannel().mention() + " was created.";
            Utility.sendLog(log, content, false);
        }
    }

    public static void doJoinLeaveLog(GuildMemberEvent event, boolean joining) {
        IGuild guild = event.getGuild();
        GuildObject content = Globals.getGuildContent(guild.getLongID());
        if (!content.config.moduleLogging) return;

        String output = "> **@%s#%s** %s.\n**Current Users:** %s."; //name, descriminator, thinger, usercount.
        output = String.format(output, event.getUser().getName(), event.getUser().getDiscriminator(), "%s", event.getGuild().getUsers().size());
        //String builder = "> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** has **%s** the server.\n**Current Users:** " + event.getGuild().getUsers().size() + ".";

        if (content.config.joinLeaveLogging) {
            if (joining) {
                Utility.sendLog(String.format(output, "has **Joined** the server"), content, false);
            } else {
                if (content.config.kickLogging) { doKickLog(guild, event.getUser()); }
                Utility.sendLog(String.format(output, "has **Left** the server"), content, false);
            }
        }
    }

    public static void doMessageEditLog(MessageUpdateEvent event) {

        CommandObject command = new CommandObject(event.getMessage());
        IUser ourUser = command.client.bot.get();

        if (!shouldLog(command)) return;

        if (command.guild.config.editLogging) {
            //formats how long ago this was.
            String formatted;
            String oldContent;
            String newContent;
            StringBuilder response = new StringBuilder();
            ZonedDateTime oldMessageTime = event.getOldMessage().getTimestamp().atZone(ZoneOffset.UTC);
            ZonedDateTime newMessageTime = event.getNewMessage().getEditedTimestamp().get().atZone(ZoneOffset.UTC);
            int charLimit;
            long difference = newMessageTime.toEpochSecond() - oldMessageTime.toEpochSecond();
            if (command.guild.config.useTimeStamps) {
                formatted = "at `" + Utility.formatTimestamp(oldMessageTime) + " - UTC`";
            } else {
                formatted = "from " + Utility.formatTimeDifference(difference);
            }
            response.append("> **@" + command.user.username + "'s** Message " + formatted + " was **Edited** in channel: " + command.channel.get().mention() + ". ");
            //remove excess text that would cause a max char limit error.
            if (command.message.get().getContent().isEmpty()) {
                return;
            }
            if (command.guild.config.extendEditLog) {
                charLimit = 900;
            } else {
                charLimit = 1800;
            }
            if (event.getOldMessage().getContent().length() > charLimit) {
                oldContent = Utility.unFormatMentions(event.getOldMessage()).substring(0, charLimit) + "...";
            } else {
                oldContent = Utility.unFormatMentions(event.getOldMessage());
            }
            if (event.getNewMessage().getContent().length() > charLimit) {
                newContent = Utility.unFormatMentions(event.getNewMessage()).substring(0, charLimit) + "...";
            } else {
                newContent = Utility.unFormatMentions(event.getNewMessage());
            }
            response.append("**\nMessage's Old Contents:**\n" + oldContent);
            if (command.guild.config.extendEditLog) {
                response.append("\n**Message's New Contents:**\n" + newContent);
            }
            Utility.sendLog(response.toString(), command.guild, false);
        }
    }

    public static void doRoleUpdateLog(UserRoleUpdateEvent event) {
        IGuild guild = event.getGuild();
        GuildObject content = Globals.getGuildContent(guild.getLongID());
        if (!content.config.moduleLogging) return;
        if (content.config.userRoleLogging) {
            ArrayList<String> oldRoles = new ArrayList<>();
            ArrayList<String> newRoles = new ArrayList<>();
            oldRoles.addAll(event.getOldRoles().stream().filter(r -> !r.isEveryoneRole()).map(IRole::getName).collect(Collectors.toList()));
            newRoles.addAll(event.getNewRoles().stream().filter(r -> !r.isEveryoneRole()).map(IRole::getName).collect(Collectors.toList()));
            StringBuilder oldRoleList = new StringBuilder();
            StringBuilder newRoleList = new StringBuilder();
            for (String r : oldRoles) {
                oldRoleList.append(r + ", ");
            }
            for (String r : newRoles) {
                newRoleList.append(r + ", ");
            }
            String prefix = "> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "'s** Role have been Updated.";
            Utility.sendLog(prefix + "\nOld Roles: " + Utility.listFormatter(oldRoles, true) + "\nNew Roles: " + Utility.listFormatter(newRoles, true), content, false);
        }
    }

    /***
     * Handler for logging Kicks.
     *
     * @param guild the Guild the user left.
     * @param user  the User that left the server.
     */
    private static void doKickLog(IGuild guild, IUser user) {
        IUser botUser = Client.getClient().getOurUser();
        //test if the bot has auditLog perms
        if (!GuildHandler.testForPerms(botUser, guild, Permissions.VIEW_AUDIT_LOG)) return;

        //getTimestamp
        long timeStamp = Instant.now().atZone(ZoneOffset.UTC).toEpochSecond() * 1000;

        //build Message
        StringHandler kickLog = new StringHandler("**@%s#%s** has been **Kicked** by **@%s#%s**");

        // do some checks to make sure the user was in fact kicked
        List<TargetedEntry> kicksLog = guild.getAuditLog(ActionType.MEMBER_KICK).getEntriesByTarget(user.getLongID());
        if (kicksLog.size() == 0) return;

        //sort kickLog and get latest entry
        kicksLog.sort(Comparator.comparingLong(o -> DiscordUtils.getSnowflakeTimeFromID(o.getLongID()).toEpochMilli()));
        AuditLogEntry lastKick = kicksLog.get(kicksLog.size() - 1);

        //get the latest entry's timestamp
        long lastKickTime = DiscordUtils.getSnowflakeTimeFromID(lastKick.getLongID()).toEpochMilli();

        //get user responsible
        IUser responsible = lastKick.getResponsibleUser();

        // Check if timestamp is within fifteen seconds either way, lastKick is valid.
        long timeDiff = Math.abs(timeStamp - lastKickTime);
        //if (timeDiff > 15000) return;

        //format and send message
        kickLog.format(user.getName(), user.getDiscriminator(), responsible.getName(), responsible.getDiscriminator());
        if (lastKick.getReason().isPresent()) {
            kickLog.appendFormatted(" with reason `%s`", lastKick.getReason().get());
        }

        //send log
        GuildObject content = Globals.getGuildContent(guild.getLongID());
        Utility.sendLog(kickLog.toString(), content, true);
    }


    public static void doBanLog(UserBanEvent event) {
        IGuild guild = event.getGuild();
        GuildObject guildObject = Globals.getGuildContent(guild.getLongID());
        if (!guildObject.config.banLogging || !GuildHandler.testForPerms(Client.getClient().getOurUser(), guild, Permissions.VIEW_AUDIT_LOG))
            return;
        StringHandler output = new StringHandler("> **@%s#%s** was banned");
        output.setContent(String.format(output.toString(), event.getUser().getName(), event.getUser().getDiscriminator()));

        // get recent bans
        List<TargetedEntry> recentBans = event.getGuild().getAuditLog(ActionType.MEMBER_BAN_ADD).getEntriesByTarget(event.getUser().getLongID());
        if (recentBans.size() == 0) return;
        // and sort them. last entry is most recent.
        recentBans.sort(Comparator.comparingLong(o -> DiscordUtils.getSnowflakeTimeFromID(o.getLongID()).toEpochMilli()));

        AuditLogEntry lastBan = recentBans.get(recentBans.size() - 1);
        output.appendFormatted(" by **@%s#%s**", lastBan.getResponsibleUser().getName(), lastBan.getResponsibleUser().getDiscriminator());
        String reason = lastBan.getReason().isPresent() ? lastBan.getReason().get() : "No reason provided";
        output.appendFormatted(" with reason `%s`", reason);
        Utility.sendLog(output.toString(), guildObject, true);
    }
}
