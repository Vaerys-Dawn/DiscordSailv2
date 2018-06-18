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
        this.instants = new ArrayList<Instant>() {{
            add(command.message.get().getTimestamp());
        }};
    }

    public boolean addCount(Instant instant) {
        if (blocked) return true;
        counter++;
        instants.add(instant);
        if (counter > 5) {
            Instant min = instants.stream().min(Instant::compareTo).get();
            Instant max = instants.stream().max(Instant::compareTo).get();
            if (max.toEpochMilli() - 60 * 1000 < min.toEpochMilli()) {
                Globals.getGlobalData().blockUserFromDMS(userID);
                blocked = true;
                return true;
            }
        }
        return false;
    }

    public long getUserID() {
        return userID;
    }
}
