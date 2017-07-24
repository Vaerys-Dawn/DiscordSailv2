package POGOs;


import GuildToggles.Toggles.DailyMessage;
import Objects.DailyMessageObject;
import Objects.DailyUserMessageObject;
import Objects.PatchObject;
import Objects.ReminderObject;

import java.time.DayOfWeek;
import java.util.ArrayList;

/**
 * Created by Vaerys on 10/02/2017.
 */
public class GlobalData {
    ArrayList<String> blockedFromDMS = new ArrayList<>();
    ArrayList<ReminderObject> reminders = new ArrayList<>();
    private ArrayList<PatchObject> patches = new ArrayList<>();
    private ArrayList<String> globalPatches = new ArrayList<>();
    private ArrayList<Long> queuedRequests = new ArrayList<>();
    private ArrayList<DailyUserMessageObject> dailyUserMessages = new ArrayList<>();

    public ArrayList<PatchObject> getPatches() {
        return patches;
    }

    public ArrayList<String> getBlockedFromDMS() {
        return blockedFromDMS;
    }

    public ArrayList<Long> getQueuedRequests() {
        return queuedRequests;
    }

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
        for (int i = 0; i < reminders.size();i++){
            if(reminders.get(i).getUserID().equals(userID)){
                reminders.remove(i);
            }
        }
    }

    public void removeReminder(String userID,String reminder) {
        for (int i = 0; i < reminders.size();i++){
            if(reminders.get(i).getUserID().equals(userID) && reminders.get(i).getMessage().equals(reminder)){
                reminders.remove(i);
            }
        }
    }

    public ArrayList<String> getGlobalPatches() {
        return globalPatches;
    }

    public ArrayList<DailyUserMessageObject> getDailyUserMessages() {
        return dailyUserMessages;
    }

    public ArrayList<DailyUserMessageObject> getDailyMessages(DayOfWeek day) {
        ArrayList dailyMessages = new ArrayList();
        for (DailyUserMessageObject d: dailyUserMessages){
            if (d.getDay() == day){
                dailyMessages.add(d);
            }
        }
        return dailyMessages;
    }
}
