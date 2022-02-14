package com.github.vaerys.masterobjects;

import com.github.vaerys.enums.ChannelSetting;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.LinkedList;
import java.util.List;

public class GuildChannelObject extends ChannelObject {

    public long position;
    public String mention;
    public List<ChannelSetting> settings = new LinkedList<>();
    private TextChannel object;
    public GuildObject guild;

    public GuildChannelObject(TextChannel channel, GuildObject guild) {
        super(channel);
        this.mention = channel.getAsMention();
        this.position = channel.getPosition();
        this.object = channel;
        this.mention = channel.getAsMention();
        this.guild = guild;
        if (guild != null) {
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

}
