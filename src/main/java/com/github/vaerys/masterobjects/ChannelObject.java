package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Client;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.AttachmentOption;

import java.io.File;
import java.io.InputStream;

public class ChannelObject {
    public ClientObject client;
    public long longID;
    public String name;
    private MessageChannel messageChannel;

    public ChannelObject(MessageChannel channel) {
        if (channel == null) return;
        this.client = Client.getClientObject();
        this.messageChannel = channel;
        this.longID = channel.getIdLong();
        this.name = channel.getName();
    }

    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    public Message getMessageByID(long next) {
        return messageChannel.retrieveMessageById(next).complete();
    }

    public int getPinCount() {
        return messageChannel.retrievePinnedMessages().complete().size();
    }

    public void queueMessage(String s) {
        messageChannel.sendMessage(s).queue();
    }

    public void queueMessage(MessageEmbed embed) {
        messageChannel.sendMessage(embed).queue();
    }

    public void queueMessage(String s, MessageEmbed embed) {
        messageChannel.sendMessage(s).embed(embed).queue();
    }

    public void queueFile(byte[] bytes, String fileName, AttachmentOption... options) {
        messageChannel.sendFile(bytes,fileName,options).queue();
    }

    public void queueFile(String s, byte[] bytes, String fileName, AttachmentOption... options) {
        messageChannel.sendMessage(s).addFile(bytes, fileName,options).queue();
    }

    public void queueFile(InputStream stream, String fileName, AttachmentOption... options) {
        messageChannel.sendFile(stream, fileName, options).queue();
    }

    public void queueFile(String s, InputStream stream, String fileName, AttachmentOption... options) {
        messageChannel.sendMessage(s).addFile(stream, fileName,options).queue();
    }

    public void queueFile(File file, String fileName, AttachmentOption... options) {
        messageChannel.sendFile(file, fileName, options).queue();
    }

    public void queueFile(String s, File file, String fileName, AttachmentOption... options) {
        messageChannel.sendMessage(s).addFile(file, fileName, options).queue();
    }

    public void queueFile(String s, File file, AttachmentOption... options) {
        messageChannel.sendMessage(s).addFile(file, options).queue();
    }

    public Message sendMessage(String s) {
        return messageChannel.sendMessage(s).complete();
    }

    public Message sendMessage(MessageEmbed embed) {
        return messageChannel.sendMessage(embed).complete();
    }

    public Message sendMessage(String s, MessageEmbed embed) {
        return messageChannel.sendMessage(s).embed(embed).complete();
    }

    public Message sendFile(byte[] bytes, String fileName, AttachmentOption... options) {
        return messageChannel.sendFile(bytes,fileName,options).complete();
    }

    public Message sendFile(String s, byte[] bytes, String fileName, AttachmentOption... options) {
        return messageChannel.sendMessage(s).addFile(bytes, fileName,options).complete();
    }

    public Message sendFile(InputStream stream, String fileName, AttachmentOption... options) {
        return messageChannel.sendFile(stream, fileName, options).complete();
    }

    public Message sendFile(String s, InputStream stream, String fileName, AttachmentOption... options) {
        return messageChannel.sendMessage(s).addFile(stream, fileName,options).complete();
    }

    public Message sendFile(File file, String fileName, AttachmentOption... options) {
        return messageChannel.sendFile(file, fileName, options).complete();
    }

    public Message sendFile(String s, File file, String fileName, AttachmentOption... options) {
        return messageChannel.sendMessage(s).addFile(file, fileName, options).complete();
    }

    public Message sendFile(String s, File file, AttachmentOption... options) {
        return messageChannel.sendMessage(s).addFile(file, options).complete();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChannelObject) {
            return longID == ((ChannelObject) obj).longID;
        } else {
            return super.equals(obj);
        }
    }

}
