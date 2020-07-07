package com.github.vaerys.v3core.v3masterobjects;

import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Utility;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public class MessageObject {

    public ClientObject client;
    public long longID;
    public UserObject author;
    private IMessage object;

    public MessageObject(IMessage message, GuildObject guild) {
        if (message == null) return;
        client = new ClientObject(guild);
        this.object = message;
        this.longID = message.getLongID();
        this.author = new UserObject(message.getAuthor(), guild);
    }

    public IMessage get() {
        return object;
    }

    public List<IMessage.Attachment> getAttachments() {
        return object.getAttachments();
    }

    public int length() {
        return object.getContent().length();
    }

    public String getContent() {
        return object.getContent();
    }

    public void delete() {
        RequestHandler.deleteMessage(object);
    }

    public ZonedDateTime getTimestampZone() {
        return ZonedDateTime.ofInstant(object.getTimestamp(), ZoneOffset.UTC);
    }

    public List<IReaction> getReactions() {
        return object.getReactions();
    }

    public IReaction getReationByName(String s) {
        return object.getReactionByEmoji(Utility.getReaction(s));
    }

    public List<IUser> getMentions() {
        return object.getMentions();
    }

    public Instant getTimestamp() {
        return object.getTimestamp();
    }
}
