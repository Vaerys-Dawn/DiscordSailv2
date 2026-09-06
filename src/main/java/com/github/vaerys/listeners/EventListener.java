package com.github.vaerys.listeners;

import com.github.vaerys.handlers.*;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Vaerys on 03/08/2016.
 */

public class EventListener extends ListenerAdapter {

    final static Logger logger = LoggerFactory.getLogger(EventListener.class);

    @Override
    public void onGenericRole(@NotNull GenericRoleEvent event) {
        updateVariables(event.getGuild());
    }

    @Override
    public void onRoleDelete(@NotNull RoleDeleteEvent event) {
        updateVariables(event.getGuild());
    }

    public void updateVariables(Guild guild) {
        long guildID = guild.getIdLong();
        GuildObject guildObject = Globals.getGuildContent(guildID);
        guildObject.config.updateVariables(guild);
        guildObject.characters.updateVars(guild);
    }

    public static void handlePinnedMessages(MessageReceivedEvent event) {
        Message message = event.getMessage();
        if (message.getAuthor().getIdLong() == Client.getClientObject().bot.longID) return;
        message.delete().queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getType() == MessageType.CHANNEL_PINNED_ADD) {
            handlePinnedMessages(event);
        }
        if (!Globals.isReady) return;
        try {
            if (event.getAuthor().isBot()) return;
            MessageChannel channel = event.getChannel();

            //message and command handling
            String args = event.getMessage().getContentRaw().isEmpty() ? "" : event.getMessage().getContentRaw();

            if (channel.getType() == ChannelType.PRIVATE) {
                DmCommandObject command = new DmCommandObject(event.getMessage(), event.getChannel().asPrivateChannel(), event.getAuthor());
                MessageHandler.handleDmMessage(args, command);
            } else {
                CommandObject command = new CommandObject(event.getMessage(), event.getGuild());
                MessageHandler.handleMessage(args, command);
            }
        } catch (StackOverflowError e) {
            logger.error("caught");
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
            builder.append(": ").append(e.getMessage());
            if (!errorPos.isEmpty()) {
                builder.append("\n").append(Constants.PREFIX_INDENT).append("at ").append(errorPos);
            }
            builder.append("```");
            event.getChannel().sendMessage(builder.toString()).queue();
            Utility.sendStack(e, event.getAuthor(), event.getMessage());
        }
    }


    @Override
    public void onGenericMessageReaction(GenericMessageReactionEvent event) {
        Globals.reactionCount++;
        if (event.getUser().isBot()) {
            return;
        }
        UnicodeEmoji x = Utility.getReaction("x");
        UnicodeEmoji pin = Utility.getReactionUnicode(Constants.EMOJI_ADD_PIN_UNICODE);
        UnicodeEmoji thumbsUp = Utility.getReaction(Constants.EMOJI_THUMBS_UP);
        UnicodeEmoji thumbsDown = Utility.getReaction(Constants.EMOJI_THUMBS_DOWN);
        UnicodeEmoji heart = Utility.getReaction(Constants.EMOJI_LIKE_PIN);
        UnicodeEmoji remove = Utility.getReaction(Constants.EMOJI_REMOVE_PIN);
        UnicodeEmoji gift = Utility.getReaction("gift");
        EmojiUnion emojiUnion = event.getReaction().getEmoji();

        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();

        CommandObject object = new CommandObject(message, event.getGuild());
        UserObject pinner = new UserObject(event.getUser(), object.guild);
        UserObject owner = new UserObject(message.getAuthor(), object.guild);


        //do only on server channels
        if (emojiUnion.getType() == Emoji.Type.UNICODE) {
            UnicodeEmoji emoji = emojiUnion.asUnicode();
            //if is x and can bypass
            if (emoji.equals(remove)) ArtHandler.unPin(object);
            if (emoji.equals(x) && GuildHandler.testForPerms(event.getMember(), event.getGuild(), Permission.MESSAGE_MANAGE) &&
                    object.client.bot.longID == object.user.longID) {
                object.message.delete();
            }
            //if is pushpin
            if (emoji.equals(pin)) ArtHandler.pinMessage(object, pinner, owner);

            //if is thumbsup or thumbs down and is creator.
            if (emoji.equals(thumbsUp) || emoji.equals(thumbsDown))
                QueueHandler.reactionAdded(object, event.getReaction());
            //if is heart and is pinned then give xp
            if (emoji.equals(heart))
                ArtHandler.pinLiked(object, pinner, owner);
            //give a gift
            if (emoji.equals(gift))
                Globals.getGlobalData().giveGift(message.getIdLong(), pinner, object.guild);
            //do only within Direct messages
        }
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        if (event.getChannel().getType() == ChannelType.PRIVATE) return;
        if (!Globals.isReady) return;
        if (event.getChannel().getType() == ChannelType.TEXT) {
            GuildMessageChannel channel = event.getGuildChannel();
            CommandObject command = new CommandObject(Globals.getGuildContent(event.getGuild().getIdLong()), channel);
            if (!command.guild.config.moduleLogging) return;
            LoggingListener.logDelete(command, event);
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        GuildObject content = Globals.getGuildContent(event.getGuild().getIdLong());
        UserObject user = new UserObject(event.getUser(), content);
        if (content.config.welcomeMessages && !user.get().isBot()) {
            JoinHandler.sendWelcomeMessage(content, event, user);
        }
        if (content.config.checkNewUsers) {
            JoinHandler.checkNewUsers(content, event, user);
        }
        if (content.config.moduleJoinMessages && content.config.sendJoinMessages) {
            JoinHandler.customJoinMessages(content, event.getMember());
        }
        GuildHandler.checkUsersRoles(user.longID, content);
        JoinHandler.autoReMute(event, content, user);
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if (event.getChannel() instanceof PrivateChannel) return;
        CommandObject command = new CommandObject(event.getMessage(), event.getGuild());

        LoggingListener.doMessageEditLog(command);
    }

    /***
     * Delete on X emoji added
     * @param event
     */
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        Emoji x = Utility.getReaction("x");
        Emoji emoji = event.getReaction().getEmoji();
        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        if (event.getUser() == null) return;
        if (event.getChannel().getType() == ChannelType.PRIVATE) {
            //if anyone uses x
            if (emoji.equals(x) && Client.getClientObject().bot.longID == event.getUser().getIdLong()) {
                message.delete().queue();
            }
        }
    }
}
