package com.github.vaerys.objects.adminlevel;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class UserCountDown {
    long userID;
    long remainderSecs;

    public UserCountDown(long userID, long remainderSecs) {
        this.userID = userID;
        this.remainderSecs = remainderSecs;
    }

    public long getID() {
        return userID;
    }

    public long getRemainderSecs() {
        return remainderSecs;
    }

    public void setRemainderSecs(long remainderSecs) {
        this.remainderSecs = remainderSecs;
    }

    public void tickDown(int i) {
        if (remainderSecs != -1) {
            remainderSecs -= i;
            if (remainderSecs < 0) {
                remainderSecs = 0;
            }
        }
    }
}
