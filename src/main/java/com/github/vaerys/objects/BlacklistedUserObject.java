package com.github.vaerys.objects;

import java.time.Instant;

public class BlacklistedUserObject {
    // The duration a block will last for, multiplied by
    // counter
    private static final long BLOCK_TIME_SECONDS = 60;
    private long userID;
    private long counter;
    private long endTime;

    public BlacklistedUserObject() {
        this.userID = 0;
        this.counter = 0;
        this.endTime = 0;
    }

    public BlacklistedUserObject(long userID, long counter) {
        this.userID = userID;
        this.counter = counter;
        if (counter >= 5) {
            this.endTime = -1;
        } else {
            long duration = (BLOCK_TIME_SECONDS * 1000) * counter;
            this.endTime = Instant.now().toEpochMilli() + duration;
        }
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
