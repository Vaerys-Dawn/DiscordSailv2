package com.github.vaerys.objects.depreciated;

import com.github.vaerys.main.Constants;

import java.time.Instant;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class BlackListObject {
    private String name;
    private String phrase;
    private String reason;
    private String type;

    public BlackListObject(String name, String phrase) {
        this.phrase = phrase;
        this.name = name;
        type = Constants.BL_PENDING;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class BlacklistedUserObject {
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
}
