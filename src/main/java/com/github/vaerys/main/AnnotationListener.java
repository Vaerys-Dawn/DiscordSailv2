package com.github.vaerys.main;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.ArtHandler;
import com.github.vaerys.handlers.MessageHandler;
import com.github.vaerys.handlers.QueueHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.SplitFirstObject;
import com.github.vaerys.pogos.GuildConfig;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.member.GuildMemberEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleUpdateEvent;
import sx.blah.discord.handle.obj.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 03/08/2016.
 */

@SuppressWarnings({"unused", "StringConcatenationInsideStringBufferAppend"})
public class AnnotationListener {

    final static Logger logger = LoggerFactory.getLogger(AnnotationListener.class);

    /**
     * Sets up the relevant files for each guild.
     */

    @EventSubscriber
    public void onGuildLeaveEvent(GuildLeaveEvent event) {
        Globals.unloadGuild(event.getGuild().getLongID());
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        Globals.isReady = true;
        event.getClient().changePlayingText(Globals.playing);
        Utility.updateUsername(Globals.botName);
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        try {
            if (event.getAuthor().isBot()) {
                return;
            }
            CommandObject command = new CommandObject(event.getMessage());
            //Set Console Response Channel.
            if (command.user.get().equals(command.client.creator)) {
                Globals.consoleMessageCID = command.channel.longID;
            }
            //message and command handling
            new MessageHandler(command.message.get().getContent(), command, event.getChannel().isPrivate());
        } catch (Exception e) {
            String errorPos = "";
            for (StackTraceElement s : e.getStackTrace()) {
                if (s.toString().contains("com.github.vaerys")) {
                    errorPos = s.toString();
                    break;
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append("> I caught an Error, Please send this Error message and the message that caused this error " +
                    "to my **Direct Messages** so my developer can look at it and try to solve the issue.\n```\n");
            builder.append(e.getClass().getName());
            builder.append(": " + e.getMessage());
            if (!errorPos.isEmpty()) {
                builder.append("\n" + Constants.PREFIX_INDENT + "at " + errorPos);
            }
            builder.append("```");
            Utility.sendMessage(builder.toString(), event.getChannel());
            Utility.sendStack(e);
            return;
        }
    }

    @EventSubscriber
    public void onMentionEvent(MentionEvent event) {
        IChannel channel = event.getMessage().getChannel();
        if (channel.isPrivate()) {
            return;
        }
        IGuild guild = event.getMessage().getGuild();
        IUser author = event.getMessage().getAuthor();
        long guildOwnerID = guild.getOwner().getLongID();
        String sailMention = event.getClient().getOurUser().mention(false);
        String sailMentionNick = event.getClient().getOurUser().mention(true);
        String prefix;
        String message = event.getMessage().toString();
        String[] splitMessage;

        if (event.getMessage().mentionsEveryone() || event.getMessage().mentionsHere()) {
            return;
        }
        if (author.getLongID() == Globals.getClient().getOurUser().getLongID()) {
            return;
        }

        /**This lets you set the guild's Prefix if you run "@Bot SetCommandPrefix [New Prefix]"*/
        if (author.getLongID() == guildOwnerID || author.getLongID() == Globals.creatorID) {
            SplitFirstObject mentionSplit = new SplitFirstObject(message);
            SplitFirstObject getArgs = new SplitFirstObject(mentionSplit.getRest());
            if (mentionSplit.getFirstWord() != null) {
                if (mentionSplit.getFirstWord().equals(sailMention) || mentionSplit.getFirstWord().equals(sailMentionNick)) {
                    long guildID = guild.getLongID();
                    GuildConfig guildConfig = Globals.getGuildContent(guildID).config;
                    if (getArgs.getRest() != null && !getArgs.getRest().contains(" ") && !getArgs.getRest().contains("\n")) {
                        prefix = getArgs.getRest();
                        if (getArgs.getFirstWord() != null && getArgs.getFirstWord().equalsIgnoreCase("setCommandPrefix")) {
                            Utility.sendMessage("> Updated Command Prefix to be : " + prefix, channel);
                            guildConfig.setPrefixCommand(prefix);
                        } else if (getArgs.getFirstWord() != null && getArgs.getFirstWord().equalsIgnoreCase("setCCPrefix")) {
                            Utility.sendMessage("> Updated CC Prefix to be : " + prefix, channel);
                            guildConfig.setPrefixCC(prefix);
                        }
                    } else {
                        Utility.sendMessage("> An error occurred trying to set Prefix\n" + Constants.PREFIX_INDENT + "Be sure that the prefix does not contain a newline or any spaces.", channel);
                    }
                }
            }
        }
    }

    @EventSubscriber
    public void onRoleUpdateEvent(RoleUpdateEvent event) {
        updateVariables(event.getGuild());
    }

    @EventSubscriber
    public void onRoleDeleteEvent(RoleDeleteEvent event) {
        updateVariables(event.getGuild());
    }

    @EventSubscriber
    public void onChannelUpdateEvent(ChannelUpdateEvent event) {
        if (event.getChannel().isPrivate()) {
            return;
        }
        updateVariables(event.getNewChannel().getGuild());

        GuildObject content = Globals.getGuildContent(event.getGuild().getLongID());
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

    @EventSubscriber
    public void onChannelDeleteEvent(ChannelDeleteEvent event) {
        if (event.getChannel().isPrivate()) {
            return;
        }
        GuildObject content = Globals.getGuildContent(event.getGuild().getLongID());
        if (content.config.channelLogging) {
            String log = "> Channel #" + event.getChannel().getName() + " was deleted.";
            Utility.sendLog(log, content, false);
        }
        updateVariables(event.getChannel().getGuild());
    }

    @EventSubscriber
    public void onChannelCreateEvent(ChannelCreateEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getLongID());
        if (content.config.channelLogging) {
            String log = "> Channel " + event.getChannel().mention() + " was created.";
            Utility.sendLog(log, content, false);
        }
    }

    public static void updateVariables(IGuild guild) {
        long guildID = guild.getLongID();
        GuildConfig guildConfig = Globals.getGuildContent(guildID).config;
        guildConfig.updateVariables(guild);
    }

    @EventSubscriber
    public void onReactionAddEvent(ReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        Emoji x = EmojiManager.getForAlias("x");
        Emoji pin = EmojiManager.getForAlias("pushpin");
        Emoji thumbsUp = EmojiManager.getForAlias("thumbsup");
        Emoji thumbsDown = EmojiManager.getForAlias("thumbsdown");
        IEmoji customEmoji = null;
        Emoji emoji = null;

        if (event.getReaction().isCustomEmoji()) {
            customEmoji = event.getReaction().getCustomEmoji();
        } else {
            emoji = event.getReaction().getUnicodeEmoji();
        }

        if (customEmoji == null && emoji == null) {
            return;
        }

        //do only on server channels
        if (!event.getChannel().isPrivate()) {
            CommandObject object = new CommandObject(event.getMessage());
            if (customEmoji != null) {
                return;
            } else {
                //if is pushpin
                if (object.guild.config.artPinning) {
                    if (event.getReaction().getUnicodeEmoji().equals(pin)) {
                        new ArtHandler(object.setAuthor(event.getUser()));
                    }
                }
                //if is x and can bypass
                if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, event.getUser(), event.getGuild())) {
                    if (event.getReaction().getUnicodeEmoji().equals(x)) {
                        if (event.getMessage().getAuthor().getLongID() == Globals.getClient().getOurUser().getLongID()) {
                            Utility.deleteMessage(event.getMessage());
                            return;
                        }
                    }
                }
                //if is thumbsup or thumbs down and is creator.
                if (event.getChannel().getLongID() == Globals.queueChannelID) {
                    if (!event.getReaction().isCustomEmoji()) {
                        if (event.getReaction().getUnicodeEmoji().equals(thumbsUp) || event.getReaction().getUnicodeEmoji().equals(thumbsDown)) {
                            IUser owner = Globals.getClient().getUserByID(Globals.creatorID);
                            if (event.getReaction().getUserReacted(owner)) {
                                QueueHandler.addedReaction(event.getMessage(), event.getReaction());
                            }
                        }
                    }
                }
            }
            //do only within Direct messages
        } else {
            if (customEmoji != null) {
                return;
            } else {
                //if anyone uses x
                if (event.getReaction().getUnicodeEmoji().equals(x)) {
                    if (event.getMessage().getAuthor().getLongID() == Globals.getClient().getOurUser().getLongID()) {
                        Utility.deleteMessage(event.getMessage());
                        return;
                    }
                }
            }
        }
    }

    @EventSubscriber
    public void onMessageDeleteEvent(MessageDeleteEvent event) {
        if (event.getChannel().isPrivate()) {
            return;
        }
        CommandObject command = new CommandObject(event.getMessage());
        String content;
        IUser ourUser = command.client.bot;

        if (!shouldLog(command)) {
            return;
        }

        if (command.guild.config.deleteLogging) {
            if (command.message.get().getContent() == null) {
                return;
            }
            if (command.message.get().getContent().isEmpty()) {
                return;
            }
            int charLimit = 1800;
            if (command.message.get().getContent().length() > charLimit) {
                content = Utility.unFormatMentions(command.message.get()).substring(0, 1800) + "...";
            } else {
                content = Utility.unFormatMentions(command.message.get());
            }
            if ((content.equals("`Loading...`") || content.equals("`Working...`")) && command.user.longID == command.client.longID) {
                return;
            }
            long difference = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - event.getMessage().getTimestamp().atZone(ZoneOffset.UTC).toEpochSecond();
            String formatted;
            if (command.guild.config.useTimeStamps) {
                formatted = "at `" + Utility.formatTimestamp(event.getMessage().getTimestamp().atZone(ZoneOffset.UTC)) + " - UTC`";
            } else {
                formatted = "from " + Utility.formatTimeDifference(difference);
            }
            Utility.sendLog("> **@" + command.user.username + "'s** Message " + formatted + " was **Deleted** in channel: " + command.channel.get().mention() + " with contents:\n" + content, command.guild, false);
        }
    }

    private boolean shouldLog(CommandObject command) {
        List<IChannel> infoID = command.guild.config.getChannelsByType(Command.CHANNEL_INFO, command.guild);
        List<IChannel> serverLogID = command.guild.config.getChannelsByType(Command.CHANNEL_SERVER_LOG, command.guild);
        List<IChannel> adminLogID = command.guild.config.getChannelsByType(Command.CHANNEL_ADMIN_LOG, command.guild);
        List<IChannel> dontLog = command.guild.config.getChannelsByType(Command.CHANNEL_DONT_LOG, command.guild);
        if (dontLog.size() != 0) {
            if (dontLog.contains(command.channel.get())) {
                return false;
            }
        }
        if (command.guild.config.dontLogBot && command.user.get().isBot()) {
            return false;
        }
        if (command.message.get() == null) {
            return false;
        }
        if (serverLogID.size() != 0 && serverLogID.get(0).equals(command.channel.get()) && command.client.longID == command.user.longID) {
            return false;
        }
        if (infoID.size() != 0 && infoID.get(0).equals(command.channel.get()) && command.client.longID == command.user.longID) {
            return false;
        }
        if (adminLogID.size() != 0 && adminLogID.get(0).equals(command.channel.get()) && command.client.longID == command.user.longID) {
            return false;
        }
        return true;
    }

    @EventSubscriber
    public void onUserJoinEvent(UserJoinEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getLongID());
        if (content.config.joinsServerMessages) {
            String message = content.config.getJoinMessage();
            message = message.replace("<server>", event.getGuild().getName());
            message = message.replace("<user>", event.getUser().getName());
            Utility.sendDM(message, event.getUser().getLongID());
        }
        joinLeaveLogging(event, true);
    }

    @EventSubscriber
    public void onUserLeaveEvent(UserLeaveEvent event) {
        joinLeaveLogging(event, false);
    }

    public void joinLeaveLogging(GuildMemberEvent event, boolean joining) {
        IGuild guild = event.getGuild();
        GuildObject content = Globals.getGuildContent(guild.getLongID());
        if (content.config.joinLeaveLogging) {
            if (joining) {
                Utility.sendLog("> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** has **Joined** the server.", content, false);
            } else {
                Utility.sendLog("> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** has **Left** the server.", content, false);
            }
        }
    }

    @EventSubscriber
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        if (event.getNewMessage().getContent() == null || event.getOldMessage().getContent() == null) {
            return;
        }
        if (event.getNewMessage().getContent().isEmpty() || event.getOldMessage().getContent().isEmpty()) {
            return;
        }
        if (event.getNewMessage().getContent().equals(event.getOldMessage().getContent())) {
            return;
        }
        if (event.getChannel().isPrivate()) {
            return;
        }
        CommandObject command = new CommandObject(event.getMessage());
        IUser ourUser = command.client.bot;
        if (!shouldLog(command)) {
            return;
        }

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

    @EventSubscriber
    public void onUserRoleUpdateEvent(UserRoleUpdateEvent event) {
        IGuild guild = event.getGuild();
        GuildObject content = Globals.getGuildContent(guild.getLongID());
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
            logger.debug("Old Roles:");
            logger.debug(oldRoleList.toString());
            logger.debug("New Roles:");
            logger.debug(newRoleList.toString());
            String prefix = "> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "'s** Role have been Updated.";
            Utility.sendLog(prefix + "\nOld Roles: " + Utility.listFormatter(oldRoles, true) + "\nNew Roles: " + Utility.listFormatter(newRoles, true), content, false);
        }
    }
}
