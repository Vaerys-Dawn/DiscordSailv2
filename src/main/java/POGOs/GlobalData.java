package POGOs;


import java.util.ArrayList;

/**
 * Created by Vaerys on 10/02/2017.
 */
public class GlobalData {
    ArrayList<String> blockedFromDMS = new ArrayList<>();
    private boolean taskComplete = false;

    public ArrayList<String> getBlockedFromDMS() {
        return blockedFromDMS;
    }

    public boolean blockUserFromDMS(String userID) {
        blockedFromDMS.add(userID);
        return true;
    }


    public boolean isTaskComplete() {
        return taskComplete;
    }

    public void taskComplete() {
        taskComplete = true;
    }
}
