package com.github.vaerys.pogos;

import com.github.vaerys.objects.TimedEvent;
import com.github.vaerys.templates.GlobalFile;

import java.util.ArrayList;
import java.util.List;

public class Events extends GlobalFile {
    public static final String FILE_PATH = "Events.json";
    private double fileVersion = 1.0;
    List<TimedEvent> events = new ArrayList<>();

    public List<TimedEvent> getEvents() {
        return events;
    }
}
