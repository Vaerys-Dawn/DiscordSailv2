package com.github.vaerys.objects.events;

import java.time.DayOfWeek;

public class EventAvatar {
    String name;
    DayOfWeek day;
    String link;

    public EventAvatar(DayOfWeek day, String link) {
        this.day = day;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
