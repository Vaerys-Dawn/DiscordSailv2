package com.github.vaerys.pogos;


import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.depreciated.BlackListObject;
import com.github.vaerys.objects.userlevel.ReminderObject;
import com.github.vaerys.templates.GlobalFile;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 10/02/2017.
 */
public class GlobalData extends GlobalFile {
    public static final String FILE_PATH = "Global_Data.json";
    private double fileVersion = 1.0;
    ArrayList<Long> blockedFromDMS = new ArrayList<>();
    List<BlackListObject.BlacklistedUserObject> blacklistedUsers = new ArrayList<>();
    ArrayList<ReminderObject> reminders = new ArrayList<>();
    private long presentID = -1;
    List<Long> giftsGiven = new ArrayList<>();

    public List<BlackListObject.BlacklistedUserObject> getBlacklistedUsers() {
        return blacklistedUsers;
    }

    public ArrayList<Long> getBlockedFromDMS() {
        return blockedFromDMS;
    }

    public void blockUserFromDMS(long userID) {
        blockedFromDMS.add(userID);
    }

    public BlackListObject.BlacklistedUserObject blacklistUser(long userID) {
        return blacklistUser(userID, 0);
    }

    public BlackListObject.BlacklistedUserObject blacklistUser(long userID, long count) {
        BlackListObject.BlacklistedUserObject blacklistedUser;

        ListIterator<BlackListObject.BlacklistedUserObject> litr = blacklistedUsers.listIterator();

        while (litr.hasNext()) {
            blacklistedUser = litr.next();
            if (userID == blacklistedUser.getUserID()) {
                if (count != 0) count = blacklistedUser.getCounter();
                litr.remove();
                break;
            }
        }

        count++;
        blacklistedUser = new BlackListObject.BlacklistedUserObject(userID, count);
        blacklistedUsers.add(blacklistedUser);
        return blacklistedUser;
    }

    public ArrayList<ReminderObject> getReminders() {
        return reminders;
    }

    public List<ReminderObject> getRemindersUser(long userID) {
        return reminders.stream().filter(r -> r.getUserID() == userID).collect(Collectors.toList());
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

    public void setPresentId(long longID) {
        presentID = longID;
    }

    public void giveGift(long messageID, UserObject pinner, GuildObject guild) {
        if (messageID != presentID) return;
        if (giftsGiven.contains(pinner.longID)) return;
        pinner.getProfile(guild).addXP(1000, guild.config);
        giftsGiven.add(pinner.longID);
    }
}
