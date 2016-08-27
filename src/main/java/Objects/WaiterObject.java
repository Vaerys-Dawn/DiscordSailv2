package Objects;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class WaiterObject {
    String ID;
    public int removerCountdown;

    public WaiterObject(String ID) {
        this.ID = ID;
        this.removerCountdown = 60;
    }

    public String getID() {
        return ID;
    }
}
