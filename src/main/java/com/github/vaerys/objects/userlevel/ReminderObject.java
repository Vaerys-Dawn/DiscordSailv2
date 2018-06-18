package com.github.vaerys.objects.userlevel;

import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;

/**
 * Created by Vaerys on 05/04/2017.
 */
public class ReminderObject {
    long userID;
    long channelID;
    String message;
    boolean sent = false;
    long executeTime;

    public ReminderObject(long userID, long channelID, String message, long reminderTime) {
        this.userID = userID;
        this.channelID = channelID;
        this.message = Globals.getClient().getUserByID(userID).mention() + " " + Utility.removeMentions(message);
        this.executeTime = reminderTime;
    }

    public long getUserID() {
        return userID;
    }

    public long getChannelID() {
        return channelID;
    }

    public String getMessage() {
        return message;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
