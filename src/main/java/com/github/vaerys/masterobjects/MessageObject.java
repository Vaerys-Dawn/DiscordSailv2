package com.github.vaerys.masterobjects;

import sx.blah.discord.handle.obj.IMessage;

public class MessageObject {

    public ClientObject client;
    private IMessage object;
    public long longID;
    public String stringID;

    public MessageObject(IMessage message, GuildObject guild) {
        client = new ClientObject(message.getClient(), guild);
        this.object = message;
        this.longID = message.getLongID();
        this.stringID = message.getStringID();
    }

    public IMessage get() {
        return object;
    }
}
