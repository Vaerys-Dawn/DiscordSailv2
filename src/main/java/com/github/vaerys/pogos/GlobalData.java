package com.github.vaerys.pogos;


import com.github.vaerys.interfaces.GlobalFile;
import com.github.vaerys.objects.ReminderObject;

import java.util.ArrayList;

/**
 * Created by Vaerys on 10/02/2017.
 */
public class GlobalData extends GlobalFile {
    public static final String FILE_PATH = "Global_Data.json";
    private double fileVersion = 1.0;
    ArrayList<Long> blockedFromDMS = new ArrayList<>();
    ArrayList<ReminderObject> reminders = new ArrayList<>();

    public ArrayList<Long> getBlockedFromDMS() {
        return blockedFromDMS;
    }


    public void blockUserFromDMS(long userID) {
        blockedFromDMS.add(userID);
    }


    public ArrayList<ReminderObject> getReminders() {
        return reminders;
    }

    public void addReminder(ReminderObject object) {
        reminders.add(object);
    }

    public void removeReminder(long userID) {
        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).getUserID() == userID) {
                reminders.remove(i);
            }
        }
    }

    public void removeReminder(long userID, String reminder) {
        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).getUserID() == userID && reminders.get(i).getMessage().equals(reminder)) {
                reminders.remove(i);
            }
        }
    }
}
