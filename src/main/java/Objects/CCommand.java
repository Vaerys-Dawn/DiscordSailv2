package Objects;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class CCommand {

    String name;
    String userID;
    boolean isLocked;
    List<String> contents;

    public CCommand(boolean isLocked, String userID, String name, String contents) {
        this.isLocked = isLocked;
        this.userID = userID;
        this.name = name;
        this.contents = Arrays.asList(contents.split("\n"));
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public void setContents(String contents) {
        this.contents = Arrays.asList(contents.split("\n"));
    }

    public String getName() {
        return name;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public List<String> getContents() {
        return contents;
    }
}
