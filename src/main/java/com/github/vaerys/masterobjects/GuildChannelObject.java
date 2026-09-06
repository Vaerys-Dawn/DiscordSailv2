package com.github.vaerys.masterobjects;

import com.github.vaerys.enums.ChannelSetting;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;

import java.util.LinkedList;
import java.util.List;

public class GuildChannelObject extends ChannelObject {

    public long position;
    public String mention;
    public List<ChannelSetting> settings = new LinkedList<>();
    private GuildMessageChannel object;
    public GuildObject guild;

    public GuildChannelObject(GuildMessageChannel channel, GuildObject guild) {
        super(channel);
        this.mention = channel.getAsMention();
        this.position = channel instanceof TextChannel ? ((TextChannel) channel).getPosition() : 0;
        this.object = channel;
        this.mention = channel.getAsMention();
        this.guild = guild;
        if (guild != null) {
            for (ChannelSetting setting : guild.channelSettings) {
                if (setting.getIDs(guild).isEmpty()) {
                    break;
                } else if (setting.getIDs(guild).contains(longID)) {
                    settings.add(setting);
                }
            }
        }
    }

    public GuildMessageChannel get() {
        return object;
    }

}
