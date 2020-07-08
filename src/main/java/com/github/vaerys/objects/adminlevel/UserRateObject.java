package com.github.vaerys.objects.adminlevel;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import sx.blah.discord.api.internal.DiscordUtils;
import sx.blah.discord.handle.obj.TextChannel;
import sx.blah.discord.handle.obj.IUser;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class UserRateObject {
    private long userID;
    private List<Long> messageIDs = new LinkedList<>();
    private List<Long> channelIDs = new LinkedList<>();
    private boolean muted = false;

    public UserRateObject(CommandObject command) {
        this.userID = command.user.longID;
        messageIDs.add(command.message.longID);
        channelIDs.add(command.channel.longID);
    }

    public boolean isRateLimited(GuildObject guild) {
        if (messageIDs.size() > guild.config.messageLimit) {
            return getDifference() < 10000;
        }
        return false;
    }

    public void addMessage(CommandObject command) {
        messageIDs.add(command.message.longID);
        if (!channelIDs.contains(command.channel.longID)) channelIDs.add(command.channel.longID);
    }

    private long getDifference() {
        Instant startTime = DiscordUtils.getSnowflakeTimeFromID(messageIDs.get(0));
        Instant testTime = DiscordUtils.getSnowflakeTimeFromID(messageIDs.get(messageIDs.size() - 1));
        return testTime.toEpochMilli() - startTime.toEpochMilli();
    }

    public int getSize() {
        return messageIDs.size();
    }

    public List<TextChannel> getChannels(GuildObject guild) {
        return channelIDs.stream().map(l -> guild.getChannelByID(l)).collect(Collectors.toList());
    }

    public IUser getUser(GuildObject guild) {
        return guild.getUserByID(userID);
    }

    public long getUserID() {
        return userID;
    }

    public boolean isMuted() {
        return muted;
    }

    public void mute() {
        muted = true;
    }

    public long getTimeStamp() {
        return DiscordUtils.getSnowflakeTimeFromID(messageIDs.get(messageIDs.size() - 1)).toEpochMilli() * 1000;
    }
}
