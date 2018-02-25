package com.github.vaerys.pogos;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.DailyMessage;
import com.github.vaerys.objects.QueueObject;
import com.github.vaerys.templates.GlobalFile;

import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DailyMessages extends GlobalFile {
    public static final String FILE_PATH = "DailyMessages.json";
    ArrayList<DailyMessage> dailyMessages = new ArrayList<>();
    ArrayList<QueueObject> queuedRequests = new ArrayList<>();
    private double fileVersion = 1.1;

    public long newDailyMsgUID() {
        long result;
        Random random = new Random();
        List<Long> uIDs = dailyMessages.stream().filter(dailyMessage ->
                dailyMessage.getUID() != -1).map(dailyMessage ->
                dailyMessage.getUID()).collect(Collectors.toList());
        uIDs.addAll(queuedRequests.stream().map(queueObject ->
                queueObject.getuID()).collect(Collectors.toList()));
        if (uIDs.size() >= 8999) {
            return -1;
        }
//        for (DailyMessage d : dailyMessages) {
//            if (d.getUID() != -1) {
//                uIDs.add(d.getUID());
//            }
//        }
//        for (QueueObject o : queuedRequests) {
//            uIDs.add(o.getuID());
//        }
        result = random.nextInt(9000) + 1000;
        while (uIDs.contains(result)) {
            result = random.nextInt(9000) + 1000;
        }
        return result;
    }

    public ArrayList<DailyMessage> getMessages() {
        return dailyMessages;
    }

    public void setMessages(ArrayList<DailyMessage> messages) {
        this.dailyMessages = messages;
    }

    public ArrayList<QueueObject> getQueue() {
        return queuedRequests;
    }

    public ArrayList<DailyMessage> getDailyMessages(DayOfWeek day) {
        ArrayList dailyMessages = new ArrayList();
        for (DailyMessage d : this.dailyMessages) {
            if (d.getDay() == day) {
                dailyMessages.add(d);
            }
        }
        return dailyMessages;
    }

    public DailyMessage getMessageByUID(long dailyMessageID, CommandObject command) {
        DayOfWeek today = ZonedDateTime.now(ZoneOffset.UTC).getDayOfWeek();
        DailyMessage configMessage = null;
        for (DailyMessage d : Globals.configDailyMessages) {
            if (d.getDay() == today) {
                configMessage = d;
            }
        }
        DailyMessage object = new DailyMessage(configMessage.getContents(command), today, Globals.client.getOurUser().getLongID(), 0000);
        for (DailyMessage d : dailyMessages) {
            if (d.getUID() == dailyMessageID) {
                return d;
            }
        }
        return object;
    }
}
