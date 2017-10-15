package com.github.vaerys.oldcode;

import com.github.vaerys.objects.DailyUserMessageObject;
import com.github.vaerys.objects.QueueObject;

import java.util.ArrayList;

@Deprecated
public class OldGlobalData {
    ArrayList<QueueObject> queuedRequests = new ArrayList<>();
    ArrayList<DailyUserMessageObject> dailyMessages = new ArrayList<>();

    public ArrayList<QueueObject> getQueuedRequests() {
        return queuedRequests;
    }

    public ArrayList<DailyUserMessageObject> getDailyMessages() {
        return dailyMessages;
    }
}
