package Objects;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class UserRateObject {
    String userID;
    int counter;

    public UserRateObject(String userID) {
        this.userID = userID;
        counter = 1;
    }

    public void counterUp(){
        counter++;
    }

    public String getID() {
        return userID;
    }
}
