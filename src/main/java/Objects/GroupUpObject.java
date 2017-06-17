package Objects;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class GroupUpObject {
    long userID;
    String presence;

    public GroupUpObject(String presence, long userID) {
        this.presence = presence;
        this.userID = userID;
    }

    public long getUserID() {
        return userID;
    }

    public String getPresence() {
        return presence;
    }
}
