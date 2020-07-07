package com.github.vaerys.v3core.v3masterobjects;


import com.github.vaerys.main.Globals;
import com.github.vaerys.v3core.Sailv3;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class ClientObject {
    public UserObject bot;
    public UserObject creator;
    private DiscordClient object;

    public ClientObject(GuildObject guild) {
        this.object = Sailv3.getClient();
        if (!object.isConnected()) return;
        bot = new UserObject(object.getSelf().block(), guild, this);
        creator = new UserObject(object.getUserById(Snowflake.of(Globals.creatorID)).block(), guild, this);
    }

    public Mono<User> getThisUser(){
        return bot.get();
    }

    public User getThisUserBlock(){
        return bot.get().block();
    }

    public UserObject getUserObject() {
        return bot;
    }

    public DiscordClient get() {
        return object;
    }

    public Mono<User> getMonoUserByID(long userID) {
        return object.getUserById(Snowflake.of(userID));
    }

    public User getUserByID(long userID) {
        return object.getUserById(Snowflake.of(userID)).block();
    }

    public Flux<Guild> getGuilds() {
        return object.getGuilds();
    }

    public List<Guild> getGuildsBlock() {
        return object.getGuilds().collectList().block();
    }

    public Flux<GuildEmoji> getEmojiList() {
        return getGuilds().flatMap(Guild::getEmojis);
    }

    public List<GuildEmoji> getEmojiListBlock(){
        return getEmojiList().collectList().block();
    }

    public Mono<GuildEmoji> getEmojiByID(long emojiID) {
        return getEmojiList().filter(guildEmoji -> guildEmoji.getId().equals(Snowflake.of(emojiID))).next();
    }

    public GuildEmoji getEmojiByIDBlock(long emojiID) {
        return getEmojiByID(emojiID).block();
    }
}
