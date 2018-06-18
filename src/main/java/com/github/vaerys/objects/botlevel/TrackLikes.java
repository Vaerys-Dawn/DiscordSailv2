package com.github.vaerys.objects.botlevel;

import java.util.ArrayList;
import java.util.List;

public class TrackLikes {
    long messageID = -1;
    List<Long> users = new ArrayList<>();

    public TrackLikes(long messageID) {
        this.messageID = messageID;
    }

    public long getMessageID() {
        return messageID;
    }

    public List<Long> getUsers() {
        return users;
    }
}
