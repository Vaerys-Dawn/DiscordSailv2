package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Client;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.internal.entities.DataMessage;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;

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
        if (s == null || s.isEmpty()) return;
        messageChannel.sendMessage(s).queue();
    }

    public void queueMessage(MessageEmbed embed) {
        messageChannel.sendMessageEmbeds(embed).queue();
    }

    public void queueMessage(String s, MessageEmbed embed) {
        if (s == null || s.isEmpty()) {
            queueMessage(embed);
        } else {
            messageChannel.sendMessage(s).setEmbeds(embed).queue();
        }
    }

    public void queueFile(byte[] bytes, String fileName, AttachmentOption... options) {
        messageChannel.sendFile(bytes, fileName, options).queue();
    }

    public void queueFile(String s, byte[] bytes, String fileName, AttachmentOption... options) {
        if (s == null || s.isEmpty()) {
            queueFile(bytes, fileName, options);
        } else {
            messageChannel.sendMessage(s).addFile(bytes, fileName, options).queue();
        }
    }

    public void queueFile(InputStream stream, String fileName, AttachmentOption... options) {
        messageChannel.sendFile(stream, fileName, options).queue();
    }

    public void queueFile(String s, InputStream stream, String fileName, AttachmentOption... options) {
        if (s == null || s.isEmpty()) {
            queueFile(stream, fileName, options);
        } else {
            messageChannel.sendMessage(s).addFile(stream, fileName, options).queue();
        }
    }

    public void queueFile(File file, String fileName, AttachmentOption... options) {
        messageChannel.sendFile(file, fileName, options).queue();
    }

    public void queueFile(File file, AttachmentOption... options) {
        messageChannel.sendFile(file, options).queue();
    }

    public void queueFile(String s, File file, String fileName, AttachmentOption... options) {
        if (s == null || s.isEmpty()) {
            queueFile(file, fileName, options);
        } else {
            messageChannel.sendMessage(s).addFile(file, fileName, options).queue();
        }
    }

    public void queueFile(String s, File file, AttachmentOption... options) {
        if (s == null || s.isEmpty()) {
            queueFile(file, options);
        } else {
            messageChannel.sendMessage(s).addFile(file, options).queue();
        }
    }

    public Message sendMessage(String s) {
        if (s == null || s.isEmpty()) return null;
        return messageChannel.sendMessage(s).complete();
    }

    public Message sendMessage(MessageEmbed embed) {
        return messageChannel.sendMessageEmbeds(embed).complete();
    }

    public Message sendMessage(String s, MessageEmbed embed) {
        if (s == null || s.isEmpty()) {
            return sendMessage(embed);
        } else {
            return messageChannel.sendMessage(s).setEmbeds(embed).complete();
        }
    }

    public Message sendFile(byte[] bytes, String fileName, AttachmentOption... options) {
        return messageChannel.sendFile(bytes, fileName, options).complete();
    }

    public Message sendFile(String s, byte[] bytes, String fileName, AttachmentOption... options) {
        if (s == null || s.isEmpty()) {
            return sendFile(bytes, fileName, options);
        }else {
            return messageChannel.sendMessage(s).addFile(bytes, fileName, options).complete();
        }
    }

    public Message sendFile(InputStream stream, String fileName, AttachmentOption... options) {
        return messageChannel.sendFile(stream, fileName, options).complete();
    }

    public Message sendFile(String s, InputStream stream, String fileName, AttachmentOption... options) {
        if (s == null || s.isEmpty()) {
            return sendFile(stream, fileName, options);
        }else {
            return messageChannel.sendMessage(s).addFile(stream, fileName, options).complete();
        }
    }

    public Message sendFile(File file, String fileName, AttachmentOption... options) {
        return messageChannel.sendFile(file, fileName, options).complete();
    }

    public Message sendFile(File file, AttachmentOption... options) {
        return messageChannel.sendFile(file, options).complete();
    }

    public Message sendFile(String s, File file, String fileName, AttachmentOption... options) {
        if (s == null || s.isEmpty()) {
            return sendFile(file, fileName, options);
        }else {
            return messageChannel.sendMessage(s).addFile(file, fileName, options).complete();
        }
    }

    public Message sendFile(String s, File file, AttachmentOption... options) {
        if (s == null || s.isEmpty()) {
            return sendFile(file, options);
        }else {
            return messageChannel.sendMessage(s).addFile(file, options).complete();
        }
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
