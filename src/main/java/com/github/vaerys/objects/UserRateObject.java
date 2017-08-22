package com.github.vaerys.objects;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class UserRateObject {
    public String userID;
    public int counter;

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
