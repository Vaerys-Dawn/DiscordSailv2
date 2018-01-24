package com.github.vaerys.masterobjects;

import com.github.vaerys.handlers.RequestHandler;
import sx.blah.discord.handle.obj.IMessage;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public class MessageObject {

    public ClientObject client;
    private IMessage object;
    public long longID;

    public MessageObject(IMessage message, GuildObject guild) {
        if (message == null) return;
        client = new ClientObject(message.getClient(), guild);
        this.object = message;
        this.longID = message.getLongID();
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

    public ZonedDateTime getTimestamp() {
        return ZonedDateTime.ofInstant(object.getTimestamp(),ZoneOffset.UTC);
    }
}
