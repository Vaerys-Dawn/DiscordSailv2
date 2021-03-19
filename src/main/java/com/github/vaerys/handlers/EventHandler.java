package com.github.vaerys.handlers;

import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.*;
import com.github.vaerys.objects.utils.LogObject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateParentEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateParentEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.GenericRoleUpdateEvent;
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

public class EventHandler {

    final static Logger logger = LoggerFactory.getLogger(EventHandler.class);

    public static void updateVariables(Guild guild) {
        long guildID = guild.getIdLong();
        GuildObject guildObject = Globals.getGuildContent(guildID);
        guildObject.config.updateVariables(guild);
        guildObject.characters.updateVars(guild);
    }

    /**
     * Sets up the relevant files for each guild.
     */

    public static void handlePinnedMessages(MessageReceivedEvent event) {
        Message message = event.getMessage();
        if (message.getAuthor().getIdLong() == Client.getClientObject().bot.longID) return;
        message.delete().queue();
    }

    public static void onMessageReceived(MessageReceivedEvent event) {
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
            } else {
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

    public static void onReactionAddEvent(GuildMessageReactionAddEvent event) {
        Globals.reactionCount++;
        if (event.getUser().isBot()) {
            return;
        }
        MessageReaction.ReactionEmote x = Utility.getReaction("x");
        MessageReaction.ReactionEmote pin = Utility.getReaction(Constants.EMOJI_ADD_PIN);
        MessageReaction.ReactionEmote thumbsUp = Utility.getReaction(Constants.EMOJI_THUMBS_UP);
        MessageReaction.ReactionEmote thumbsDown = Utility.getReaction(Constants.EMOJI_THUMBS_DOWN);
        MessageReaction.ReactionEmote heart = Utility.getReaction(Constants.EMOJI_LIKE_PIN);
        MessageReaction.ReactionEmote remove = Utility.getReaction(Constants.EMOJI_REMOVE_PIN);
        MessageReaction.ReactionEmote gift = Utility.getReaction("gift");
        MessageReaction.ReactionEmote emoji = event.getReaction().getReactionEmote();

        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

        CommandObject object = new CommandObject(message, event.getGuild());
        UserObject pinner = new UserObject(event.getUser(), object.guild);
        UserObject owner = new UserObject(message.getAuthor(), object.guild);


        //do only on server channels
        if (emoji.isEmoji()) {
            //if is x and can bypass
            if (emoji.equals(remove)) ArtHandler.unPin(object);
            if (emoji.equals(x) && GuildHandler.testForPerms(event.getUser(), event.getGuild(), Permission.MESSAGE_MANAGE) &&
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
        } else if (event.getChannel() instanceof PrivateChannel && emoji.isEmoji()) {
            //if anyone uses x
            if (emoji.equals(x) && object.client.bot.longID == object.user.longID) {
                message.delete().queue();
            }
        }
    }

    public static void onMessageDeleteEvent(MessageDeleteEvent event) {
        if (event.getChannel() instanceof PrivateChannel) return;
        if (!Globals.isReady) return;
        if (event.getGuild().getUserByID(event.getAuthor().getIdLong()) == null) return;
        CommandObject command = new CommandObject(event.getMessage());
        if (!command.guild.config.moduleLogging) return;
        LoggingHandler.logDelete(command, event.getMessage());
    }

    public static void onUserJoinEvent(GuildMemberJoinEvent event) {
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
    }

    public static void onMessageUpdateEvent(MessageUpdateEvent event) {
        if (event.getOldMessage() == null || event.getNewMessage() == null) return;
        if (event.getNewMessage().getContent() == null || event.getOldMessage().getContent() == null) return;
        if (event.getNewMessage().getContent().isEmpty() || event.getOldMessage().getContent().isEmpty()) return;
        if (event.getNewMessage().getContent().equals(event.getOldMessage().getContent())) return;
        if (event.getChannel().isPrivate()) return;
        CommandObject command = new CommandObject(event.getMessage());

        LoggingHandler.doMessageEditLog(command, event.getOldMessage(), event.getNewMessage());
    }
}
