package com.github.vaerys.objects.adminlevel;

import com.github.vaerys.masterobjects.GuildObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class UserRateObject {
    public int counter;
    private long userID;
    List<Long> channels = new LinkedList<>();
    public long timeStamp;

    public UserRateObject(long userID, IChannel channel, long timeStamp) {
        this.userID = userID;
        counter = 1;
        channels.add(channel.getLongID());
        this.timeStamp = timeStamp;
    }

    public void counterUp(IChannel channel, long timeStamp) {
        counter++;
        if (!channels.contains(channel.getLongID())) {
            channels.add(channel.getLongID());
        }
        this.timeStamp = timeStamp;
    }

    public long getID() {
        return userID;
    }

    public List<IChannel> getChannels(GuildObject task) {
        Stream<Long> stream = channels.stream();
        List<IChannel> channelObjects = stream.map(id -> task.getChannelByID(id)).collect(Collectors.toList());
        stream.close();
        return channelObjects;
    }

    public IUser getUser(GuildObject task) {
        return task.getUserByID(userID);
    }
}
