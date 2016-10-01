package Objects;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class WaiterObject {
    String userID;
    public int removerCountdown;

    public WaiterObject(String userID) {
        this.userID = userID;
        this.removerCountdown = 60;
    }

    public String getID() {
        return userID;
    }
}
