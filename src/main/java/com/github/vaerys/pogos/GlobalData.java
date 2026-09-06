package com.github.vaerys.pogos;


import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.depreciated.BlackListObject;
import com.github.vaerys.objects.userlevel.ReminderObject;
import com.github.vaerys.templates.GlobalFile;
import com.github.vaerys.utilobjects.Pair;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 10/02/2017.
 */
public class GlobalData extends GlobalFile {
    public static final String FILE_PATH = "Global_Data.json";
    private double fileVersion = 1.1;
    List<Long> blockedFromDMS = new ArrayList<>();
    List<BlackListObject.BlacklistedUserObject> blacklistedUsers = new ArrayList<>();
    private List<Pair<Long, Long>> dataRequests = new ArrayList<>();
    private List<Pair<Long, Long>> deleteRequests = new ArrayList<>();
    ArrayList<ReminderObject> reminders = new ArrayList<>();
    private long presentID = -1;
    List<Long> giftsGiven = new ArrayList<>();

    public List<BlackListObject.BlacklistedUserObject> getBlacklistedUsers() {
        return blacklistedUsers;
    }

    public List<Long> getBlockedFromDMS() {
        if (blockedFromDMS == null) blockedFromDMS = new ArrayList<>();
        return blockedFromDMS;
    }

    private Pair<Long, Long> searchOrCreateDataRequests(long userID) {
        Pair<Long, Long> found = null;
        for (Pair<Long, Long> check: dataRequests) {
            if (check.getFirst() == userID) found = check;
        }
        if (found == null) {
            found = new Pair<>(userID, 0L);
            dataRequests.add(found);
        }
        return found;
    }

    private Pair<Long, Long> searchOrCreateDeleteRequests(long userID) {
        Pair<Long, Long> found = null;
        for (Pair<Long, Long> check: deleteRequests) {
            if (check.getFirst() == userID) found = check;
        }
        if (found == null) {
            found = new Pair<>(userID, 0L);
            deleteRequests.add(found);
        }
        return found;
    }

    public boolean canSendDataRequest(long userID) {
        Pair<Long, Long> check = searchOrCreateDataRequests(userID);
        return check.getSecond() + (60 * 60 * 3) < ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
    }

    public void updateDataRequest(long userID) {
        searchOrCreateDataRequests(userID).setSecond(ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond());
    }

    public boolean canSendDeleteRequest(long userID) {
        Pair<Long, Long> check = searchOrCreateDeleteRequests(userID);
        return check.getSecond() + (60 * 60 * 24) < ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
    }

    public void updateDeleteRequest(long userID) {
        searchOrCreateDeleteRequests(userID).setSecond(ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond());
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

    public List<ReminderObject> getReminders() {
        return reminders;
    }

    public List<ReminderObject> getRemindersUser(long userID) {
        return reminders.stream().filter(r -> r.getUserID() == userID).collect(Collectors.toList());
    }

    public void addReminder(ReminderObject object) {
        reminders.add(object);
    }

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
        pinner.getProfile().addXP(1000, guild.config);
        giftsGiven.add(pinner.longID);
    }
}
