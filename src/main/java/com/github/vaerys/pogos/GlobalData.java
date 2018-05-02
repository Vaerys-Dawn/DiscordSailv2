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
    private double fileVersion = 1.0;
    ArrayList<Long> blockedFromDMS = new ArrayList<>();
    List<BlacklistedUserObject> blacklistedUsers = new ArrayList<>();
    ArrayList<ReminderObject> reminders = new ArrayList<>();

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
        return blacklistUser(userID, 0);
    }

    public BlacklistedUserObject blacklistUser(long userID, long count) {
        BlacklistedUserObject blacklistedUser;

        ListIterator<BlacklistedUserObject> litr = blacklistedUsers.listIterator();

        while (litr.hasNext()) {
            blacklistedUser = litr.next();
            if (userID == blacklistedUser.getUserID()) {
                if (count != 0) count = blacklistedUser.getCounter();
                litr.remove();
                break;
            }
        }

        count++;
        blacklistedUser = new BlacklistedUserObject(userID, count);
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
