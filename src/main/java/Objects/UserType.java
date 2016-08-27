package Objects;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class UserType {
    String displayName;
    String ID;

    public UserType(String displayName, String ID) {
        this.displayName = displayName;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }
}
