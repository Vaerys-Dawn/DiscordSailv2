package com.github.vaerys.objects;

import com.github.vaerys.main.Utility;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class UserRateObject {
    public int counter;
    private String userID;

    public UserRateObject(long userID) {
        this.userID = Long.toUnsignedString(userID);
        counter = 1;
    }

    public void counterUp() {
        counter++;
    }

    public long getID() {
        return Utility.stringLong(userID);
    }
}
