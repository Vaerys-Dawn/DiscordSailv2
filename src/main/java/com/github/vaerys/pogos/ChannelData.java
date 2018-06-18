package com.github.vaerys.pogos;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.ChannelSettingObject;
import com.github.vaerys.objects.adminlevel.JoinMessage;
import com.github.vaerys.objects.botlevel.TrackLikes;
import com.github.vaerys.objects.userlevel.GroupUpObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.GlobalFile;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.time.Instant;
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

    public void checkGroupUp(CommandObject command) {
        ListIterator iterator = groupUpObjects.listIterator();
        while (iterator.hasNext()) {
            GroupUpObject g = (GroupUpObject) iterator.next();
            // if they aren't on the guild anymore remove them
            UserObject user = UserObject.getNewUserObject(g.getUserID(), command.guild);
            if (user == null) {
                iterator.remove();
                continue;
            }
            //if they have no profile remove them.
            ProfileObject profile = user.getProfile(command);
            if (profile == null) {
                iterator.remove();
                continue;
            }
            // if user hasn't spoken in the past 2 days remove from the list.
            Instant now = command.message.getTimestamp();
            long time = now.toEpochMilli() / 1000;
            long diff = time - profile.getLastTalked();
            long twoDays = 5 * 24 * 60 * 60;
            if (diff > twoDays) iterator.remove();
        }
    }

    public boolean checkForUser(long userID) {
        if (likes.stream()
                .map(c -> c.getUsers())
                .filter(c -> c.contains(userID))
                .toArray().length != 0) return true;
        if (groupUpObjects.stream()
                .map(c -> c.getUserID())
                .filter(c -> c == userID)
                .toArray().length != 0) return true;
        if (joinMessages.stream()
                .map(c -> c.getCreator())
                .filter(c -> c == userID)
                .toArray().length != 0) return true;
        return false;
    }
}
