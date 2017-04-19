package Objects;

import Main.Globals;

/**
 * Created by Vaerys on 05/04/2017.
 */
public class ReminderObject {
    String userID;
    String channelID;
    String message;
    boolean sent = false;
    long executeTime;

    public ReminderObject(String userID, String channelID, String message, long reminderTime) {
        this.userID = userID;
        this.channelID = channelID;
        this.message = Globals.getClient().getUserByID(userID).mention() + message;
        this.executeTime = reminderTime;
    }

    public String getUserID() {
        return userID;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getMessage() {
        return message;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isSent() {
        return sent;
    }
}
