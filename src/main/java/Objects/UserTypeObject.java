package Objects;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class UserTypeObject {
    String displayName;
    String ID;

    public UserTypeObject(String displayName, String ID) {
        this.displayName = displayName;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
