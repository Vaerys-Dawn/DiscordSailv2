package com.github.vaerys.masterobjects;

import sx.blah.discord.handle.obj.IMessage;

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
}
