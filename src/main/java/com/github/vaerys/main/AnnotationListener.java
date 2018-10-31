package com.github.vaerys.main;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.handlers.*;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.LogObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleUpdateEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 03/08/2016.
 */

@SuppressWarnings({"unused", "StringConcatenationInsideStringBufferAppend"})
public class AnnotationListener {

    final static Logger logger = LoggerFactory.getLogger(AnnotationListener.class);

    public static void updateVariables(IGuild guild) {
        long guildID = guild.getLongID();
        GuildObject guildObject = Globals.getGuildContent(guildID);
        guildObject.config.updateVariables(guild);
        guildObject.characters.updateVars(guild);
    }

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
        if (!Globals.isReady) return;
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
            if (event.getGuild() != null) {
                Globals.addToLog(new LogObject("ERROR", "MESSAGE_HANDLER", event.getMessage().getContent(),
                        event.getChannel().getLongID(), event.getAuthor().getLongID(), event.getMessageID(), event.getGuild().getLongID()));
            }
            Utility.sendStack(e);
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
        LoggingHandler.doChannelUpdateLog(event);
    }

    @EventSubscriber
    public void onChannelDeleteEvent(ChannelDeleteEvent event) {
        if (event.getChannel().isPrivate()) {
            return;
        }
        LoggingHandler.doChannelDeleteLog(event);
        updateVariables(event.getChannel().getGuild());
    }

    @EventSubscriber
    public void onChannelCreateEvent(ChannelCreateEvent event) {
        LoggingHandler.doChannelCreateLog(event);
    }

    @EventSubscriber
    public void onReactionAddEvent(ReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        ReactionEmoji x = Utility.getReaction("x");
        ReactionEmoji pin = Utility.getReaction(Constants.EMOJI_ADD_PIN);
        ReactionEmoji thumbsUp = Utility.getReaction(Constants.EMOJI_THUMBS_UP);
        ReactionEmoji thumbsDown = Utility.getReaction(Constants.EMOJI_THUMBS_DOWN);
        ReactionEmoji heart = Utility.getReaction(Constants.EMOJI_LIKE_PIN);
        ReactionEmoji remove = Utility.getReaction(Constants.EMOJI_REMOVE_PIN);
        ReactionEmoji emoji = event.getReaction().getEmoji();

        if (emoji == null) return;

        IMessage message = event.getMessage();
        if (message == null) message = event.getChannel().getMessageByID(event.getMessageID());

        CommandObject object = new CommandObject(message);
        UserObject pinner = new UserObject(event.getUser(), object.guild);
        UserObject owner = new UserObject(event.getAuthor(), object.guild);


        //do only on server channels
        if (!event.getChannel().isPrivate() && emoji.isUnicode()) {
            //if is x and can bypass
            if (emoji.equals(remove)) ArtHandler.unPin(object);
            if (emoji.equals(x) && GuildHandler.testForPerms(object, Permissions.MANAGE_MESSAGES)
                    && object.client.bot.longID == object.user.longID)
                RequestHandler.deleteMessage(object.message);
            //if is pushpin
            if (emoji.equals(pin)) ArtHandler.pinMessage(object, pinner, owner);

            //if is thumbsup or thumbs down and is creator.
            if (emoji.equals(thumbsUp) || emoji.equals(thumbsDown))
                QueueHandler.reactionAdded(object, event.getReaction());
            //if is hear and is pinned then give xp
            if (emoji.equals(heart))
                ArtHandler.pinLiked(object, pinner, owner);
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
        if (event.getChannel().isPrivate()) return;
        if (!Globals.isReady) return;
        if (event.getMessage() == null) return;
        if (event.getGuild().getUserByID(event.getAuthor().getLongID()) == null) return;
        CommandObject command = new CommandObject(event.getMessage());
        if (!command.guild.config.moduleLogging) return;
        LoggingHandler.logDelete(command, event.getMessage());
    }

    @EventSubscriber
    public void onUserJoinEvent(UserJoinEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getLongID());
        UserObject user = new UserObject(event.getUser().getLongID(), content);
        if (content.config.welcomeMessages && !user.get().isBot()) {
            JoinHandler.sendWelcomeMessage(content, event, user);
        }
        if (content.config.checkNewUsers) {
            JoinHandler.checkNewUsers(content, event, user);
        }
        if (content.config.moduleJoinMessages && content.config.sendJoinMessages) {
            JoinHandler.customJoinMessages(content, event.getUser());
        }
        JoinHandler.autoReMute(event, content, user);
        if (!content.config.moduleLogging) return;
        LoggingHandler.doJoinLeaveLog(event, true);
    }

    @EventSubscriber
    public void onUserLeaveEvent(UserLeaveEvent event) {
        LoggingHandler.doJoinLeaveLog(event, false);
    }

    @EventSubscriber
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        if (event.getOldMessage() == null || event.getNewMessage() == null) return;
        if (event.getNewMessage().getContent() == null || event.getOldMessage().getContent() == null) return;
        if (event.getNewMessage().getContent().isEmpty() || event.getOldMessage().getContent().isEmpty()) return;
        if (event.getNewMessage().getContent().equals(event.getOldMessage().getContent())) return;
        if (event.getChannel().isPrivate()) return;

        LoggingHandler.doMessageEditLog(event);
    }

    @EventSubscriber
    public void onUserRoleUpdateEvent(UserRoleUpdateEvent event) {
        LoggingHandler.doRoleUpdateLog(event);
    }

    @EventSubscriber
    public void onUserBanEvent(UserBanEvent event) {
        LoggingHandler.doBanLog(event);
    }
}
