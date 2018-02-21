package com.github.vaerys.main;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.ArtHandler;
import com.github.vaerys.handlers.LoggingHandler;
import com.github.vaerys.handlers.MessageHandler;
import com.github.vaerys.handlers.QueueHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.UserCountDown;
import com.github.vaerys.enums.ChannelSetting;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageSendEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.member.GuildMemberEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleUpdateEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

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
        RequestHandler.changePresence(Globals.playing);
        RequestHandler.updateUsername(Globals.botName);
    }

    @EventSubscriber
    public void onSystemMessageReceivedEvent(MessageSendEvent event) {
        IMessage message = event.getMessage();
        if (message.getType() != IMessage.Type.CHANEL_PINNED_MESSAGE) return;
        if (!message.getAuthor().equals(event.getClient().getOurUser())) return;
        RequestHandler.deleteMessage(message);
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        try {
            if (event.getAuthor().isBot()) return;

            CommandObject command = new CommandObject(event.getMessage());
            //Set Console Response Channel.
            if (command.user.get().equals(command.client.creator)) {
                Globals.consoleMessageCID = command.channel.longID;
            }
            command.guild.handleWelcome(command);

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
            RequestHandler.sendMessage(builder.toString(), event.getChannel());
            Utility.sendStack(e);
            return;
        }
    }

    @EventSubscriber
    public void onMentionEvent(MentionEvent event) {
        // nothing interesting happens.
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

    @EventSubscriber
    public void onChannelDeleteEvent(ChannelDeleteEvent event) {
        if (event.getChannel().isPrivate()) {
            return;
        }
        GuildObject content = Globals.getGuildContent(event.getGuild().getLongID());
        if (!content.config.moduleLogging) return;
        if (content.config.channelLogging) {
            String log = "> Channel #" + event.getChannel().getName() + " was deleted.";
            Utility.sendLog(log, content, false);
        }
        updateVariables(event.getChannel().getGuild());
    }

    @EventSubscriber
    public void onChannelCreateEvent(ChannelCreateEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getLongID());
        if (!content.config.moduleLogging) return;
        if (content.config.channelLogging) {
            String log = "> Channel " + event.getChannel().mention() + " was created.";
            Utility.sendLog(log, content, false);
        }
    }

    public static void updateVariables(IGuild guild) {
        long guildID = guild.getLongID();
        GuildObject guildObject = Globals.getGuildContent(guildID);
        guildObject.config.updateVariables(guild);
        guildObject.characters.updateVars(guild);
    }

    @EventSubscriber
    public void onReactionAddEvent(ReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        ReactionEmoji x = Utility.getReaction("x");
        ReactionEmoji pin = Utility.getReaction(Constants.EMOJI_ADD_PIN);
        ReactionEmoji thumbsUp = Utility.getReaction(Constants.EMOJI_APPROVE);
        ReactionEmoji thumbsDown = Utility.getReaction(Constants.EMOJI_DISAPPROVE);
        ReactionEmoji heart = Utility.getReaction(Constants.EMOJI_LIKE_PIN);
        ReactionEmoji remove = Utility.getReaction(Constants.EMOJI_REMOVE_PIN);
        ReactionEmoji emoji = event.getReaction().getEmoji();

        if (emoji == null) return;

        IMessage message = event.getMessage();
        if (message == null) message = event.getChannel().getMessageByID(event.getMessageID());

        CommandObject object = new CommandObject(message);

        //do only on server channels
        if (!event.getChannel().isPrivate() && emoji.isUnicode()) {
            //if is x and can bypass
            if (emoji.equals(remove)) ArtHandler.unPin(object);
            if (emoji.equals(x) && Utility.testForPerms(object, Permissions.MANAGE_MESSAGES)
                    && object.client.bot.longID == object.user.longID)
                RequestHandler.deleteMessage(object.message);
            //if is pushpin
            if (emoji.equals(pin)) ArtHandler.pinMessage(object.setAuthor(event.getUser()));


            //if is thumbsup or thumbs down and is creator.
            if (emoji.equals(thumbsUp) || emoji.equals(thumbsDown))
                QueueHandler.reactionAdded(object, event.getReaction());
            //if is hear and is pinned then give xp
            if (emoji.equals(heart))
                ArtHandler.pinLiked(object.setAuthor(event.getUser()));
            //do only within Direct messages
        } else if (event.getChannel().isPrivate() && emoji.isUnicode()) {
            //if anyone uses x
            if (emoji.equals(x) && object.client.bot.longID == object.user.longID) {
                RequestHandler.deleteMessage(message);
            }
        }
    }

    @EventSubscriber
    public void onMessageDeleteEvent(MessageDeleteEvent event) {
        if (!Globals.isReady) return;
        if (event.getMessage() == null) return;
        if (event.getGuild().getUserByID(event.getAuthor().getLongID()) == null) return;
        CommandObject command = new CommandObject(event.getMessage());
        if (!command.guild.config.moduleLogging) return;
        LoggingHandler.logDelete(command, event.getMessage());
    }

    private boolean shouldLog(CommandObject command) {
        if (command.guild == null) {
            return false;
        }
        if (!command.guild.config.moduleLogging) return false;
        List<IChannel> infoID = command.guild.getChannelsByType(ChannelSetting.INFO);
        List<IChannel> serverLogID = command.guild.getChannelsByType(ChannelSetting.SERVER_LOG);
        List<IChannel> adminLogID = command.guild.getChannelsByType(ChannelSetting.ADMIN_LOG);
        List<IChannel> dontLog = command.guild.getChannelsByType(ChannelSetting.DONT_LOG);
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
        if (serverLogID.size() != 0 && serverLogID.get(0).equals(command.channel.get()) && command.client.bot.longID == command.user.longID) {
            return false;
        }
        if (infoID.size() != 0 && infoID.get(0).equals(command.channel.get()) && command.client.bot.longID == command.user.longID) {
            return false;
        }
        if (adminLogID.size() != 0 && adminLogID.get(0).equals(command.channel.get()) && command.client.bot.longID == command.user.longID) {
            return false;
        }
        return true;
    }

    @EventSubscriber
    public void onUserJoinEvent(UserJoinEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getLongID());
        if (!content.config.moduleLogging) return;
        UserObject user = new UserObject(event.getUser(), Globals.getGuildContent(event.getGuild().getLongID()));
        if (content.config.joinsServerMessages && !user.get().isBot()){
            String message = content.config.getJoinMessage();
            message = message.replace("<server>", event.getGuild().getName());
            message = message.replace("<user>", event.getUser().getName());
            user.sendDm(message);
        }
        for (UserCountDown u : content.users.mutedUsers) {
            if (u.getID() == event.getUser().getLongID()) {
                RequestHandler.roleManagement(user, content, content.config.getMutedRoleID(), true);
            }
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
        if (!content.config.moduleLogging) return;
        String builder = "> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** has **%s** the server.\n**Current Users:** " + event.getGuild().getUsers().size() + ".";
        if (content.config.joinLeaveLogging) {
            if (joining) {
                Utility.sendLog(String.format(builder, "Joined"), content, false);
            } else {
                Utility.sendLog(String.format(builder, "Left"), content, false);
            }
        }
    }

    @EventSubscriber
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        if (event.getOldMessage() == null || event.getNewMessage() == null) return;
        if (event.getNewMessage().getContent() == null || event.getOldMessage().getContent() == null) return;
        if (event.getNewMessage().getContent().isEmpty() || event.getOldMessage().getContent().isEmpty()) return;
        if (event.getNewMessage().getContent().equals(event.getOldMessage().getContent())) return;
        if (event.getChannel().isPrivate()) return;

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

    @EventSubscriber
    public void onUserRoleUpdateEvent(UserRoleUpdateEvent event) {
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
}
