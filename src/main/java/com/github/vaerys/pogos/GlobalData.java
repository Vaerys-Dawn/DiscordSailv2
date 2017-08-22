package com.github.vaerys.pogos;


import com.github.vaerys.interfaces.GlobalFile;
import com.github.vaerys.objects.PatchObject;
import com.github.vaerys.objects.ReminderObject;

import java.util.ArrayList;

/**
 * Created by Vaerys on 10/02/2017.
 */
public class GlobalData extends GlobalFile {
    public static final String FILE_PATH = "Global_Data.json";
    ArrayList<String> blockedFromDMS = new ArrayList<>();
    ArrayList<ReminderObject> reminders = new ArrayList<>();
    private ArrayList<PatchObject> patches = new ArrayList<>();
    private ArrayList<String> globalPatches = new ArrayList<>();

//    private ArrayList<QueueObject> queuedRequests = new ArrayList<>();
//    private ArrayList<DailyUserMessageObject> dailyMessages = new ArrayList<>();

    public ArrayList<PatchObject> getPatches() {
        return patches;
    }

    public ArrayList<String> getBlockedFromDMS() {
        return blockedFromDMS;
    }

//    public ArrayList<QueueObject> getQueuedRequests() {
//        return queuedRequests;
//    }

    public boolean blockUserFromDMS(String userID) {
        blockedFromDMS.add(userID);
        return true;
    }


    public ArrayList<ReminderObject> getReminders() {
        return reminders;
    }

    public void addReminder(ReminderObject object) {
        reminders.add(object);
    }

    public void removeReminder(String userID) {
        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).getUserID().equals(userID)) {
                reminders.remove(i);
            }
        }
    }

    public void removeReminder(String userID, String reminder) {
        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).getUserID().equals(userID) && reminders.get(i).getMessage().equals(reminder)) {
                reminders.remove(i);
            }
        }
    }

    public ArrayList<String> getGlobalPatches() {
        return globalPatches;
    }

//    public ArrayList<DailyUserMessageObject> getMessages() {
//        return dailyMessages;
//    }

//    public ArrayList<DailyUserMessageObject> getMessages(DayOfWeek day) {
//        ArrayList dailyMessages = new ArrayList();
//        for (DailyUserMessageObject d: this.dailyMessages){
//            if (d.getDay() == day){
//                dailyMessages.add(d);
//            }
//        }
//        return dailyMessages;
//    }
}
