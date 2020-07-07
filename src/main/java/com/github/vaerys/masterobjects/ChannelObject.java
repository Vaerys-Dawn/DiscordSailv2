package com.github.vaerys.masterobjects;

import com.github.vaerys.enums.ChannelSetting;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

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
        this.client = new ClientObject( guild);
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
}
