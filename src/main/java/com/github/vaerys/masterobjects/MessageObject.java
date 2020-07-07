package com.github.vaerys.masterobjects;

import com.github.vaerys.handlers.RequestHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class MessageObject {

    public ClientObject client;
    public long longID;
    public UserObject author;
    private Message object;

    public MessageObject(Message message, GuildObject guild) {
        if (message == null) return;
        client = new ClientObject(guild);
        this.object = message;
        this.longID = message.getIdLong();
        this.author = new UserObject(message.getAuthor(), guild);
    }

    public Message get() {
        return object;
    }

    public List<Message.Attachment> getAttachments() {
        return object.getAttachments();
    }

    public int length() {
        return object.getContentRaw().length();
    }

    public String getContent() {
        return object.getContentRaw();
    }

    public void delete() {
        RequestHandler.deleteMessage(object);
    }

    public ZonedDateTime getTimestampZone() {
        return object.getTimeCreated().atZoneSameInstant(ZoneOffset.UTC);
    }

    public List<MessageReaction> getReactions() {
        return object.getReactions();
    }

    public MessageReaction getReactionByName(String s) {
        Optional<MessageReaction> temp = object.getReactions().stream().filter(r -> r.getReactionEmote().getName() == s).findFirst();
        if (temp.isPresent()) return temp.get();
        return null;
    }

    public List<User> getMentions() {
        return object.getMentionedUsers();
    }

    public Instant getTimestamp() {
        return object.getTimeCreated().toInstant();
    }
}
