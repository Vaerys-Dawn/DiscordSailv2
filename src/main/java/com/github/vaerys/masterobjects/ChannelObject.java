package com.github.vaerys.masterobjects;

import com.github.vaerys.interfaces.ChannelSetting;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.List;

public class ChannelObject {
    public ClientObject client;
    private IChannel object;
    public long longID;
    public String stringID;
    public String name;
    public long position;
    public List<String> settings = new ArrayList<>();

    public ChannelObject(IChannel channel, GuildObject guild) {
        this.client = new ClientObject(channel.getClient(), guild);
        this.object = channel;
        this.longID = channel.getLongID();
        this.stringID = channel.getStringID();
        this.name = channel.getName();
        if (guild.get() != null) {
            this.position = channel.getPosition();
            for (ChannelSetting setting : guild.channelSettings) {
                if (setting.getIDs(guild.config) == null || setting.getIDs(guild.config).size() == 0) {
                    break;
                } else if (setting.getIDs(guild.config).contains(stringID)) {
                    settings.add(setting.type());
                }
            }
        }
    }

    public IChannel get() {
        return object;
    }
}
