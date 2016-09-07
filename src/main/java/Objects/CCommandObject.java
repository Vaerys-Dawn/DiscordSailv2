package Objects;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class CCommandObject {

    String name;
    String userID;
    boolean isLocked;
    boolean shitpost = false;
    long timesRun;
    List<String> contents;

    public CCommandObject(boolean isLocked, String userID, String name, String contents) {
        this.isLocked = isLocked;
        this.userID = userID;
        this.name = name;
        this.contents = Arrays.asList(contents.split("\n"));
        timesRun = 0;
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
        timesRun++;
        return contents;
    }

    public long getTimesRun() {
        return timesRun;
    }

    public void toggleShitPost(){
        shitpost = !shitpost;
    }
}
