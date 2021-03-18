package com.github.vaerys.objects.botlevel;

import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AutoBlocker {
    long userID;
    int counter = 1;
    List<Instant> instants;
    boolean blocked = false;

    public AutoBlocker(CommandObject command) {
        this.userID = command.user.longID;
        this.instants = new ArrayList<>();
        instants.add(command.message.getTimestamp());
    }

    public void addCount(Instant instant) {
        if (blocked) return;
        counter++;
        instants.add(instant);
        if (counter > 5) {
            Instant min = instants.stream().min(Instant::compareTo).orElseGet(Instant::now);
            Instant max = instants.stream().max(Instant::compareTo).orElseGet(Instant::now);
            if (max.toEpochMilli() - 60 * 1000 < min.toEpochMilli()) {
                Globals.getGlobalData().blockUserFromDMS(userID);
                blocked = true;
            }
        }
    }

    public long getUserID() {
        return userID;
    }
}
