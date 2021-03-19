package com.github.vaerys.handlers;

import com.github.vaerys.main.Globals;
import com.github.vaerys.main.InitEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.GenericRoleUpdateEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EventManager implements IEventManager {

    @Override
    public void register(@NotNull Object listener) {
        // do nothing
    }

    @Override
    public void unregister(@NotNull Object listener) {
        // do nothing
    }

    @Override
    public void handle(@NotNull GenericEvent event) {
        if (event instanceof GenericRoleUpdateEvent) EventHandler.updateVariables(((GenericRoleUpdateEvent<?>) event).getGuild());
        if (event instanceof RoleDeleteEvent) EventHandler.updateVariables(((RoleDeleteEvent) event).getGuild());
        if (event instanceof TextChannelDeleteEvent) LoggingHandler.doTextChannelDeleteLog((TextChannelDeleteEvent) event);
        if (event instanceof VoiceChannelDeleteEvent) LoggingHandler.doVoiceChannelDeleteLog((VoiceChannelDeleteEvent) event);
        if (event instanceof TextChannelUpdatePositionEvent) LoggingHandler.logTextChannelMoveUpdate(((TextChannelUpdatePositionEvent) event));
        if (event instanceof MessageReceivedEvent) {
            CreatorHandler.restart((MessageReceivedEvent) event);
            CreatorHandler.creatorCommands((MessageReceivedEvent) event);
            EventHandler.onMessageReceived((MessageReceivedEvent) event);
        }
        if (event instanceof GuildMessageReactionAddEvent) EventHandler.onReactionAddEvent((GuildMessageReactionAddEvent) event);
        if (event instanceof MessageDeleteEvent) EventHandler.onMessageDeleteEvent((MessageDeleteEvent) event);
        if (event instanceof ReadyEvent) {
            InitEvent.initBot();
        }
        if (event instanceof GuildLeaveEvent) Globals.unloadGuild(((GuildLeaveEvent) event).getGuild().getIdLong());
        if (event instanceof GuildReadyEvent) GuildCreateListener.onGuildCreateEvent((GuildReadyEvent) event);
        if (event instanceof MessageUpdateEvent) EventHandler.onMessageUpdateEvent((MessageUpdateEvent) event);
        if (event instanceof GuildMemberLeaveEvent) LoggingHandler.doLeaveLogging((GuildMemberLeaveEvent) event);
        if (event instanceof GuildMemberJoinEvent) {
            EventHandler.onUserJoinEvent((GuildMemberJoinEvent) event);
            LoggingHandler.doJoinLogging((GuildMemberJoinEvent) event);
        }
        if (event instanceof GuildMemberRoleRemoveEvent) LoggingHandler.doUserRoleRemoveEvent((GuildMemberRoleRemoveEvent) event);
        if (event instanceof GuildMemberRoleAddEvent) LoggingHandler.doUserRoleAddLogging((GuildMemberRoleAddEvent) event);
        if (event instanceof GuildBanEvent) LoggingHandler.doBanLog((GuildBanEvent) event);
        if (event instanceof TextChannelCreateEvent) LoggingHandler.doTextChannelCreateLog((TextChannelCreateEvent) event);
        if (event instanceof VoiceChannelCreateEvent) LoggingHandler.doVoiceChannelCreateLog((VoiceChannelCreateEvent) event);
    }

    @NotNull
    @Override
    public List<Object> getRegisteredListeners() {
        return new ArrayList<>();
    }
}
