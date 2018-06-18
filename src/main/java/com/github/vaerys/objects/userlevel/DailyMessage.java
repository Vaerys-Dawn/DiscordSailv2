package com.github.vaerys.objects.userlevel;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.TagObject;

import java.time.DayOfWeek;

/**
 * Created by Vaerys on 19/07/2017.
 */
public class DailyMessage {
    long userID;
    String content;
    DayOfWeek day;
    long uID;
    String specialID = null;


    public DailyMessage(String description, DayOfWeek day, long userID, long uID) {
        this.content = description;
        this.day = day;
        this.userID = userID;
        this.uID = uID;
    }

    public DailyMessage(String content, DayOfWeek day, long userID, String specialID) {
        this.userID = userID;
        this.content = content;
        this.day = day;
        this.uID = -1;
        this.specialID = specialID;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek newDay) {
        day = newDay;
    }

    public String getContents(CommandObject command) {
        String newContent = content;
        for (TagObject t : TagList.getType(TagType.DAILY)) {
            newContent = t.handleTag(newContent, command, "");
        }
        char[] prefixCheck = new char[4];
        newContent.getChars(0, 4, prefixCheck, 0);
        boolean found = false;
        for (char c : prefixCheck) {
            if (c == '>') {
                found = true;
            }
        }
        if (!found) newContent = "> " + newContent;
        return newContent;
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

    public String getSpecialID() {
        return specialID;
    }
}
