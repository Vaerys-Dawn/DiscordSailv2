package com.github.vaerys.masterobjects;

import java.util.LinkedList;
import java.util.List;
import com.github.vaerys.templates.ChannelSetting;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

public class ChannelObject {
    public ClientObject client;
    private IChannel object;
    public long longID;
    public String name;
    public long position;
    public String mention = "#DM";
    public List<ChannelSetting> settings = new LinkedList<>();

    public ChannelObject(IChannel channel, GuildObject guild) {
        if (channel == null) return;
        this.client = new ClientObject(channel.getClient(), guild);
        this.object = channel;
        this.longID = channel.getLongID();
        this.name = channel.getName();
        if (guild.get() != null) {
            this.mention = channel.mention();
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

    public IChannel get() {
        return object;
    }

    public IMessage getMessageByID(long next) {
        return object.getMessageByID(next);
    }

    public int getPinCount() {
        return RequestBuffer.request(() -> {
            return object.getPinnedMessages().size();
        }).get();
    }
}
