package com.github.vaerys.objects;

import com.github.vaerys.handlers.TagHandler;
import com.github.vaerys.masterobjects.GuildObject;

import java.time.DayOfWeek;

/**
 * Created by Vaerys on 19/07/2017.
 */
public class DailyUserMessageObject {
    long userID = -1;
    String content;
    DayOfWeek day;
    long uID = -1;

    public DailyUserMessageObject(String description, DayOfWeek day, long userID, long uID) {
        this.content = description;
        this.day = day;
        this.userID = userID;
        this.uID = uID;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public String getContents(GuildObject guild) {
        return TagHandler.prepDailyMessage(content, guild);
    }

    public long getUID() {
        return uID;
    }

    public void setContents(String contents) {
        this.content = contents;
    }

    public long getUserID() {
        return userID;
    }

    public void setDay(DayOfWeek newDay) {
        day = newDay;
    }
}
