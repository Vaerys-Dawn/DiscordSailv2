package com.github.vaerys.pogos;

import com.github.vaerys.interfaces.GlobalFile;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.DailyMessageObject;
import com.github.vaerys.objects.DailyUserMessageObject;
import com.github.vaerys.objects.QueueObject;

import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DailyMessages extends GlobalFile {
    private double fileVersion = 1.1;
    ArrayList<DailyUserMessageObject> dailyMessages = new ArrayList<>();
    ArrayList<QueueObject> queuedRequests = new ArrayList<>();
    public static final String FILE_PATH = "DailyMessages.json";

    public long newDailyMsgUID() {
        long result;
        Random random = new Random();
        List<Long> uIDs = new ArrayList<>();
        for (DailyUserMessageObject d : dailyMessages) {
            if (d.getUID() != -1) {
                uIDs.add(d.getUID());
            }
        }
        for (QueueObject o : queuedRequests) {
            uIDs.add(o.getuID());
        }
        result = random.nextInt(9000) + 1000;
        while (uIDs.contains(result)) {
            result = random.nextInt(9000) + 1000;
        }
        return result;
    }

    public ArrayList<DailyUserMessageObject> getMessages() {
        return dailyMessages;
    }

    public ArrayList<QueueObject> getQueue() {
        return queuedRequests;
    }

    public ArrayList<DailyUserMessageObject> getDailyMessages(DayOfWeek day) {
        ArrayList dailyMessages = new ArrayList();
        for (DailyUserMessageObject d : this.dailyMessages) {
            if (d.getDay() == day) {
                dailyMessages.add(d);
            }
        }
        return dailyMessages;
    }

    public DailyUserMessageObject getMessageByUID(long dailyMessageID) {
        DayOfWeek today = ZonedDateTime.now(ZoneOffset.UTC).getDayOfWeek();
        DailyMessageObject configMessage = null;
        for (DailyMessageObject d : Globals.configDailyMessages) {
            if (d.getDayOfWeek() == today) {
                configMessage = d;
            }
        }
        DailyUserMessageObject object = new DailyUserMessageObject(configMessage.getContents(), today, Globals.client.getOurUser().getLongID(), 0000);
        for (DailyUserMessageObject d : dailyMessages) {
            if (d.getUID() == dailyMessageID) {
                return d;
            }
        }
        return object;
    }
}
