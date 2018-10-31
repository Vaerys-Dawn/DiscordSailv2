package com.github.vaerys.pogos;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.objects.GroupUpObject;
import com.github.vaerys.objects.JoinMessage;
import com.github.vaerys.objects.TrackLikes;
import com.github.vaerys.templates.GlobalFile;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Vaerys on 12/05/2017.
 */
public class ChannelData extends GlobalFile {
    public static final String FILE_PATH = "Channel_Data.json";
    private double fileVersion = 1.3;
    List<ChannelSettingObject> channelSettings = new ArrayList<>();
    ArrayList<Long> pinnedMessages = new ArrayList<>();
    ArrayList<GroupUpObject> groupUpObjects = new ArrayList<>();
    List<TrackLikes> likes = new ArrayList<>();
    private List<JoinMessage> joinMessages = new LinkedList<>();


    public ArrayList<GroupUpObject> getGroupUpObjects() {
        return groupUpObjects;
    }

    public ArrayList<Long> getPinnedMessages() {
        return pinnedMessages;
    }


    public List<TrackLikes> getLikes() {
        return likes;
    }

    public TrackLikes getLiked(long longID) {
        for (TrackLikes l : likes) {
            if (l.getMessageID() == longID) {
                return l;
            }
        }
        return null;
    }

    public List<ChannelSettingObject> getChannelSettings() {
        return channelSettings;
    }

    public void updateVariables(IGuild guild) {
        //update channels
        for (ChannelSettingObject c : channelSettings) {
            ListIterator iterator = c.getChannelIDs().listIterator();
            while (iterator.hasNext()) {
                IChannel channel = guild.getChannelByID((Long) iterator.next());
                if (channel == null) {
                    iterator.remove();
                }
            }
        }
    }

    public List<JoinMessage> getJoinMessages() {
        return joinMessages;
    }

    public ChannelSettingObject getChannelSetting(ChannelSetting setting) {
        for (ChannelSettingObject c : channelSettings) {
            if (c.getType() == setting) return c;
        }
        return null;
    }
}
