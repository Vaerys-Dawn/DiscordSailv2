package Objects;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class UserTypeObject {
    String displayName;
    String ID;
    long xp;
    long level;

    public UserTypeObject(String ID) {
        this.ID = ID;
        xp = 0;
        level = 1;
    }

    public String getID() {
        return ID;
    }

}
