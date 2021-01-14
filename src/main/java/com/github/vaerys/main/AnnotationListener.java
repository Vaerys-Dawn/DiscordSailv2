package com.github.vaerys.main;

import com.github.vaerys.handlers.*;
import com.github.vaerys.masterobjects.*;
import com.github.vaerys.objects.utils.LogObject;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
import sx.blah.discord.handle.impl.events.guild.voice.VoiceChannelUpdateEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.Guild;
import sx.blah.discord.handle.obj.Message;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 03/08/2016.
 */

@SuppressWarnings({"unused", "StringConcatenationInsideStringBufferAppend"})
public class AnnotationListener extends ListenerAdapter {

    final static Logger logger = LoggerFactory.getLogger(AnnotationListener.class);

    public static void updateVariables(Guild guild) {
        long guildID = guild.getIdLong();
        GuildObject guildObject = Globals.getGuildContent(guildID);
        guildObject.config.updateVariables(guild);
        guildObject.characters.updateVars(guild);
    }

    /**
     * Sets up the relevant files for each guild.
     */

    @EventSubscriber
    public void onGuildLeaveEvent(GuildLeaveEvent event) {
        Globals.unloadGuild(event.getGuild().getIdLong());
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        Globals.isReady = true;
        RequestHandler.changePresence(Globals.playing);
        RequestHandler.updateUsername(Globals.botName);
    }


    public void handlePinnedMessages(MessageReceivedEvent event) {
        Message message = event.getMessage();
        if (message.getAuthor().getIdLong() == Client.getClientObject().bot.longID) return;
        message.delete().queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getType() == MessageType.CHANNEL_PINNED_ADD) {
            handlePinnedMessages(event);
        }
        if (!Globals.isReady) return;
        try {
            if (event.getAuthor().isBot()) return;

            GlobalUserObject user = new GlobalUserObject(event.getAuthor());
            MessageChannel channel = event.getChannel();

            //message and command handling
            String args = event.getMessage().getContentRaw().isEmpty() ? "" : event.getMessage().getContentRaw();

            if (channel.getType() == ChannelType.PRIVATE) {
                DmCommandObject command = new DmCommandObject(event.getMessage(), event.getPrivateChannel(), event.getAuthor());
                MessageHandler.handleDmMessage(args, command);
            }else {
                CommandObject command = new CommandObject(event.getMessage(), event.getGuild());
                MessageHandler.handleMessage(args, command);
            }
        } catch (StackOverflowError e) {
            System.out.println("caught");
        } catch (Exception e) {
            String errorPos = "";
            for (StackTraceElement s : e.getStackTrace()) {
                if (s.toString().contains("com.github.vaerys")) {
                    errorPos = s.toString();
                    break;
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append("\\> I caught an Error, Please send this Error message and the message that caused this error " +
                    "to my **Direct Messages** so my developer can look at it and try to solve the issue.\n```\n");
            builder.append(e.getClass().getName());
            builder.append(": " + e.getMessage());
            if (!errorPos.isEmpty()) {
                builder.append("\n" + Constants.PREFIX_INDENT + "at " + errorPos);
            }
            builder.append("```");
            event.getChannel().sendMessage(builder.toString()).queue();
            if (event.getGuild() != null) {
                Globals.addToLog(new LogObject("ERROR", "MESSAGE_HANDLER", event.getMessage().getContentRaw(),
                        event.getChannel().getIdLong(), event.getAuthor().getIdLong(), event.getMessageIdLong(), event.getGuild().getIdLong()));
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
    public void onVoiceChannelUpdateEvent(VoiceChannelUpdateEvent event) {
        LoggingHandler.logChannelUpdate(Globals.getGuildContent(event.getGuild().getIdLong()), event.getOldVoiceChannel(), event.getNewVoiceChannel());
    }

    @EventSubscriber
    public void onChannelUpdateEvent(ChannelUpdateEvent event) {
        if (event.getChannel().isPrivate()) {
            return;
        }
        updateVariables(event.getNewChannel().getGuild());
        LoggingHandler.logChannelUpdate(Globals.getGuildContent(event.getGuild().getIdLong()), event.getOldChannel(), event.getNewChannel());
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
        Globals.reactionCount++;
        if (event.getUser().isBot()) {
            return;
        }
        ReactionEmoji x = Utility.getReaction("x");
        ReactionEmoji pin = Utility.getReaction(Constants.EMOJI_ADD_PIN);
        ReactionEmoji thumbsUp = Utility.getReaction(Constants.EMOJI_THUMBS_UP);
        ReactionEmoji thumbsDown = Utility.getReaction(Constants.EMOJI_THUMBS_DOWN);
        ReactionEmoji heart = Utility.getReaction(Constants.EMOJI_LIKE_PIN);
        ReactionEmoji remove = Utility.getReaction(Constants.EMOJI_REMOVE_PIN);
        ReactionEmoji gift = Utility.getReaction("gift");
        ReactionEmoji emoji = event.getReaction().getEmoji();

        if (emoji == null) return;

        Message message = event.getMessage();
        if (message == null) message = event.getChannel().getMessageByID(event.getMessageID());

        CommandObject object = new CommandObject(message);
        UserObject pinner = new UserObject(event.getUser(), object.guild);
        UserObject owner = new UserObject(event.getAuthor(), object.guild);


        //do only on server channels
        if (!event.getChannel().isPrivate() && emoji.isUnicode()) {
            //if is x and can bypass
            if (emoji.equals(remove)) ArtHandler.unPin(object);
            if (emoji.equals(x) && GuildHandler.testForPerms(event.getUser(), event.getGuild(), Permissions.MANAGE_MESSAGES) &&
                    object.client.bot.longID == object.user.longID) {
                RequestHandler.deleteMessage(object.message);
            }
            //if is pushpin
            if (emoji.equals(pin)) ArtHandler.pinMessage(object, pinner, owner);

            //if is thumbsup or thumbs down and is creator.
            if (emoji.equals(thumbsUp) || emoji.equals(thumbsDown))
                QueueHandler.reactionAdded(object, event.getReaction());
            //if is hear and is pinned then give xp
            if (emoji.equals(heart))
                ArtHandler.pinLiked(object, pinner, owner);
            //give a gift
            if (emoji.equals(gift))
                Globals.getGlobalData().giveGift(message.getIdLong(), pinner, object.guild);
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
        if (event.getGuild().getUserByID(event.getAuthor().getIdLong()) == null) return;
        CommandObject command = new CommandObject(event.getMessage());
        if (!command.guild.config.moduleLogging) return;
        LoggingHandler.logDelete(command, event.getMessage());
    }

    @EventSubscriber
    public void onUserJoinEvent(UserJoinEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getIdLong());
        UserObject user = new UserObject(event.getUser(), content);
        if (content.config.welcomeMessages && !user.get().isBot()) {
            JoinHandler.sendWelcomeMessage(content, event, user);
        }
        if (content.config.checkNewUsers) {
            JoinHandler.checkNewUsers(content, event, user);
        }
        if (content.config.moduleJoinMessages && content.config.sendJoinMessages) {
            JoinHandler.customJoinMessages(content, event.getUser());
        }
        GuildHandler.checkUsersRoles(user.longID, content);
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
        CommandObject command = new CommandObject(event.getMessage());

        LoggingHandler.doMessageEditLog(command, event.getOldMessage(), event.getNewMessage());
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
