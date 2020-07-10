package com.github.vaerys.masterobjects;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.main.Client;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class ChannelObject {
    public ClientObject client;
    public long longID;
    public String name;
    public long position;
    public String mention = "#DM";
    public List<ChannelSetting> settings = new LinkedList<>();
    private TextChannel object;

    public ChannelObject(TextChannel channel, GuildObject guild) {
        if (channel == null) return;
        this.client = Client.getClientObject();
        this.object = channel;
        this.longID = channel.getIdLong();
        this.name = channel.getName();
        if (guild.get() != null) {
            this.mention = channel.getAsMention();
            this.position = channel.getPosition();
            for (ChannelSetting setting : guild.channelSettings) {
                if (setting.getIDs(guild).size() == 0) {
                    break;
                } else if (setting.getIDs(guild).contains(Long.toUnsignedString(longID))) {
                    settings.add(setting);
                }
            }
        }
    }

    public TextChannel get() {
        return object;
    }

    public Message getMessageByID(long next) {
        return object.retrieveMessageById(next).complete();
    }

    public int getPinCount() {
        return object.retrievePinnedMessages().complete().size();
    }

    public void queueMessage(String s) {
        object.sendMessage(s).queue();
    }

    public void queueMessage(MessageEmbed embed) {
        object.sendMessage(embed).queue();
    }

    public void queueMessage(String s, MessageEmbed embed) {
        object.sendMessage(s).embed(embed).queue();
    }

    public void queueFile(byte[] bytes, String fileName, AttachmentOption... options) {
        object.sendFile(bytes,fileName,options).queue();
    }

    public void queueFile(String s, byte[] bytes, String fileName, AttachmentOption... options) {
        object.sendMessage(s).addFile(bytes, fileName,options).queue();
    }

    public void queueFile(InputStream stream, String fileName, AttachmentOption... options) {
        object.sendFile(stream, fileName, options).queue();
    }

    public void queueFile(String s, InputStream stream, String fileName, AttachmentOption... options) {
        object.sendMessage(s).addFile(stream, fileName,options).queue();
    }

    public void queueFile(File file, String fileName, AttachmentOption... options) {
        object.sendFile(file, fileName, options).queue();
    }

    public void queueFile(String s, File file, String fileName, AttachmentOption... options) {
        object.sendMessage(s).addFile(file, fileName, options).queue();
    }

    public void queueFile(String s, File file, AttachmentOption... options) {
        object.sendMessage(s).addFile(file, options).queue();
    }

    public Message sendMessage(String s) {
        return object.sendMessage(s).complete();
    }

    public Message sendMessage(MessageEmbed embed) {
        return object.sendMessage(embed).complete();
    }

    public Message sendMessage(String s, MessageEmbed embed) {
        return object.sendMessage(s).embed(embed).complete();
    }

    public Message sendFile(byte[] bytes, String fileName, AttachmentOption... options) {
        return object.sendFile(bytes,fileName,options).complete();
    }

    public Message sendFile(String s, byte[] bytes, String fileName, AttachmentOption... options) {
        return object.sendMessage(s).addFile(bytes, fileName,options).complete();
    }

    public Message sendFile(InputStream stream, String fileName, AttachmentOption... options) {
        return object.sendFile(stream, fileName, options).complete();
    }

    public Message sendFile(String s, InputStream stream, String fileName, AttachmentOption... options) {
        return object.sendMessage(s).addFile(stream, fileName,options).complete();
    }

    public Message sendFile(File file, String fileName, AttachmentOption... options) {
        return object.sendFile(file, fileName, options).complete();
    }

    public Message sendFile(String s, File file, String fileName, AttachmentOption... options) {
        return object.sendMessage(s).addFile(file, fileName, options).complete();
    }

    public Message sendFile(String s, File file, AttachmentOption... options) {
        return object.sendMessage(s).addFile(file, options).complete();
    }

}
