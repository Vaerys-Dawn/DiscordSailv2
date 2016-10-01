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
    boolean shitPost;
    long timesRun;
    List<String> contents;

    public CCommandObject(boolean isLocked, String userID, String name, String contents,boolean shitPost) {
        this.isLocked = isLocked;
        this.userID = userID;
        this.name = name;
        this.contents = Arrays.asList(contents.split("\n"));
        this.shitPost = shitPost;
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

    public String getContents() {
        timesRun++;
        StringBuilder builder = new StringBuilder();
        for (String s: contents){
            builder.append(s + "\n");
        }
        return builder.toString();
    }

    public long getTimesRun() {
        return timesRun;
    }

    public void toggleShitPost(){
        shitPost = !shitPost;
    }

    public boolean isShitPost() {
        return shitPost;
    }
}
