package Objects;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class WaiterObject {
    String userID;
    long remainderSecs;

    public WaiterObject(String userID) {
        this.userID = userID;
        this.remainderSecs = 60;
    }

    public String getID() {
        return userID;
    }

    public long getRemainderSecs() {
        return remainderSecs;
    }

    public void setRemainderSecs(long remainderSecs) {
        this.remainderSecs = remainderSecs;
    }
}
