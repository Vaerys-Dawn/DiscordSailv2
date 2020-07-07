package com.github.vaerys.v3core.v3masterobjects;

import com.github.vaerys.enums.ChannelSetting;
import discord4j.core.object.entity.*;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;

public class ChannelObject {
    public ClientObject client;
    public List<ChannelSetting> settings = new LinkedList<>();
    private Mono<MessageChannel> channel;

    public ChannelObject(Mono<MessageChannel> channel, GuildObject guild) {
        if (channel == null) return;
        this.client = new ClientObject(guild);
        this.channel = channel;
    }

    public Mono<MessageChannel> get() {
        return channel;
    }

    public Channel getChannel(){
        return channel.block();
    }

    public Mono<String> getName(){
        return channel.map(c -> c.getMention());
    }

    public Mono<Mono<Message>> getMonoMessageByID(long messageID) {
        return channel.map(c -> c.getMessageById(Snowflake.of(messageID)));
    }

    public Message getMessageByID(long next) {
        return getMonoMessageByID(next).block();
    }

    public Mono<Long> getPinCount() {
        return channel.getPinnedMessages().count();
    }

    public long getPinCountBlock(){
        return channel.getPinnedMessages().count().block();
    }
}
