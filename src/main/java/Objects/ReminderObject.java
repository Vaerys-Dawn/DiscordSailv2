package Objects;

/**
 * Created by Vaerys on 13/10/2016.
 */
public class ReminderObject {
    String userID;
    String channelID;
    String message;
    long timeMins;

    public ReminderObject(String userID, String channelID, String message, long timeMins) {
        this.userID = userID;
        this.channelID = channelID;
        this.message = message;
        this.timeMins = timeMins;
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

    public long getTimeMins() {
        return timeMins;
    }

    public void setTimeMins(long timeMins) {
        this.timeMins = timeMins;
    }
}
