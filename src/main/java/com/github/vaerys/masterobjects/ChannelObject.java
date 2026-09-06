package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Client;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

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

    public void queueFile(byte[] bytes, String fileName) {
        messageChannel.sendFiles(FileUpload.fromData(bytes, fileName)).queue();
    }

    public void queueFile(String s, byte[] bytes, String fileName) {
        if (s == null || s.isEmpty()) {
            queueFile(bytes, fileName);
        } else {
            messageChannel.sendMessage(s).addFiles(FileUpload.fromData(bytes, fileName)).queue();
        }
    }

    public void queueFile(InputStream stream, String fileName) {
        messageChannel.sendFiles(FileUpload.fromData(stream, fileName)).queue();
    }

    public void queueFile(String s, InputStream stream, String fileName) {
        if (s == null || s.isEmpty()) {
            queueFile(stream, fileName);
        } else {
            messageChannel.sendMessage(s).addFiles(FileUpload.fromData(stream, fileName)).queue();
        }
    }

    public void queueFile(File file, String fileName) {
        messageChannel.sendFiles(FileUpload.fromData(file, fileName)).queue();
    }

    public void queueFile(File file) {
        messageChannel.sendFiles(FileUpload.fromData(file)).queue();
    }

    public void queueFile(String s, File file, String fileName) {
        if (s == null || s.isEmpty()) {
            queueFile(file, fileName);
        } else {
            messageChannel.sendMessage(s).addFiles(FileUpload.fromData(file, fileName)).queue();
        }
    }

    public void queueFile(String s, File file) {
        if (s == null || s.isEmpty()) {
            queueFile(file);
        } else {
            messageChannel.sendMessage(s).addFiles(FileUpload.fromData(file)).queue();
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

    public Message sendFile(byte[] bytes, String fileName) {
        return messageChannel.sendFiles(FileUpload.fromData(bytes, fileName)).complete();
    }

    public Message sendFile(String s, byte[] bytes, String fileName) {
        if (s == null || s.isEmpty()) {
            return sendFile(bytes, fileName);
        }else {
            return messageChannel.sendMessage(s).addFiles(FileUpload.fromData(bytes, fileName)).complete();
        }
    }

    public Message sendFile(InputStream stream, String fileName) {
        return messageChannel.sendFiles(FileUpload.fromData(stream, fileName)).complete();
    }

    public Message sendFile(String s, InputStream stream, String fileName) {
        if (s == null || s.isEmpty()) {
            return sendFile(stream, fileName);
        }else {
            return messageChannel.sendMessage(s).addFiles(FileUpload.fromData(stream, fileName)).complete();
        }
    }

    public Message sendFile(File file, String fileName) {
        return messageChannel.sendFiles(FileUpload.fromData(file, fileName)).complete();
    }

    public Message sendFile(File file) {
        return messageChannel.sendFiles(FileUpload.fromData(file)).complete();
    }

    public Message sendFile(String s, File file, String fileName) {
        if (s == null || s.isEmpty()) {
            return sendFile(file, fileName);
        }else {
            return messageChannel.sendMessage(s).addFiles(FileUpload.fromData(file, fileName)).complete();
        }
    }

    public Message sendFile(String s, File file) {
        if (s == null || s.isEmpty()) {
            return sendFile(file);
        }else {
            return messageChannel.sendMessage(s).addFiles(FileUpload.fromData(file)).complete();
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
