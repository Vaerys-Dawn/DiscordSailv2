package com.github.vaerys.oldcode;

import java.time.DayOfWeek;

@Deprecated
public class OldUserDailyMessageObject {
    String author;
    String content;
    DayOfWeek day;

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public DayOfWeek getDay() {
        return day;
    }
}
