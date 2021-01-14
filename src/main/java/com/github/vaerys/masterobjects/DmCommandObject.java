package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Client;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class DmCommandObject {

    public MessageObject message;
    public ChannelObject messageChannel;
    public GlobalUserObject globalUser;
    public ClientObject client;

    public DmCommandObject(Message message, MessageChannel privateChannel, User author) {
        this.message = new MessageObject(message);
        this.messageChannel = new ChannelObject(privateChannel);
        this.globalUser = new GlobalUserObject(author);
        this.client = Client.getClientObject();
    }

    public DmCommandObject(){
        this.message = null;
        this.messageChannel = null;
        this.globalUser = null;
        this.client = Client.getClientObject();
    }
}
