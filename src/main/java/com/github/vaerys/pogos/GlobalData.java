package com.github.vaerys.pogos;


import com.github.vaerys.objects.BlacklistedUserObject;
import com.github.vaerys.objects.ReminderObject;
import com.github.vaerys.templates.GlobalFile;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Vaerys on 10/02/2017.
 */
public class GlobalData extends GlobalFile {
    public static final String FILE_PATH = "Global_Data.json";
    ArrayList<Long> blockedFromDMS = new ArrayList<>();
    List<BlacklistedUserObject> blacklistedUsers = new ArrayList<>();
    ArrayList<ReminderObject> reminders = new ArrayList<>();
    private double fileVersion = 1.0;

    public List<BlacklistedUserObject> getBlacklistedUsers() {
        return blacklistedUsers;
    }

    public ArrayList<Long> getBlockedFromDMS() {
        return blockedFromDMS;
    }

    public void blockUserFromDMS(long userID) {
        blockedFromDMS.add(userID);
    }

    public BlacklistedUserObject blacklistUser(long userID) {
        BlacklistedUserObject blacklistedUser;
        long blacklistCounter = 0;
        ListIterator<BlacklistedUserObject> iterator = blacklistedUsers.listIterator();
        while(iterator.hasNext()) {
            blacklistedUser = iterator.next();
            if (userID == blacklistedUser.getUserID()) {
                blacklistCounter = blacklistedUser.getCounter();
                iterator.remove();
                break;
            }
        }
        blacklistCounter++;
        blacklistedUser = new BlacklistedUserObject(userID, blacklistCounter);
        blacklistedUsers.add(blacklistedUser);
        return blacklistedUser;
    }

    public ArrayList<ReminderObject> getReminders() {
        return reminders;
    }

    public void addReminder(ReminderObject object) {
        reminders.add(object);
    }

//    public void removeReminder(long userID) {
//        for (int i = 0; i < reminders.size(); i++) {
//            if (reminders.getAllToggles(i).getUserID() == userID) {
//                reminders.remove(i);
//            }
//        }
//    }

    public void removeReminder(ReminderObject object) {
        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).getUserID() == object.getUserID() && reminders.get(i).getExecuteTime() == object.getExecuteTime()) {
                reminders.remove(i);
                return;
            }
        }
    }
}
