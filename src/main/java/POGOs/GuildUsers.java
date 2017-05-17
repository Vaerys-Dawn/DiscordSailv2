package POGOs;

import Commands.CommandObject;
import Main.Utility;
import Objects.UserCountDown;
import Objects.UserTypeObject;

import java.util.ArrayList;

/**
 * Created by Vaerys on 03/02/2017.
 */
public class GuildUsers {
    private boolean properlyInit;
    public ArrayList<UserTypeObject> users = new ArrayList<>();
    //    public ArrayList<ReminderObject> reminders = new ArrayList<>();
//    public ArrayList<ReminderObject> soonToExecute = new ArrayList<>();
//    public ArrayList<WaiterObject> groupedUp = new ArrayList<>();
    public ArrayList<UserCountDown> mutedUsers = new ArrayList<>();

    public ArrayList<UserTypeObject> getUsers() {
        return users;
    }

    public void addXP(CommandObject object) {
        if (object.author.isBot()) {
            return;
        }
        boolean isFound = false;
        UserTypeObject user = new UserTypeObject(object.authorSID);
        for (UserTypeObject u : users) {
            if (u.getID().equals(object.authorSID)) {
                isFound = true;
                user = u;
            }
        }
        if (!isFound) {
            users.add(user);
        }
//        user.addXP(object.guildConfig);
    }

  
    public boolean muteUser(String userID, long time, String guildID) {
        boolean found = false;
        for (UserCountDown c : mutedUsers) {
            if (c.getID().equals(userID)) {
                c.setRemainderSecs(time);
                found = true;
            }
        }
        if (!found) {
            mutedUsers.add(new UserCountDown(userID, time));
        }
        return Utility.muteUser(guildID, userID, true);
    }

    public boolean unMuteUser(String userID, String guildID) {
        for (int i = 0; i < mutedUsers.size(); i++) {
            if (mutedUsers.get(i).getID().equals(userID)) {
                mutedUsers.remove(i);
            }
        }
        return Utility.muteUser(guildID, userID, false);
    }

    public boolean isProperlyInit() {
        return properlyInit;
    }

    public void setProperlyInit(boolean properlyInit) {
        this.properlyInit = properlyInit;
    }

    public ArrayList<UserCountDown> getMutedUsers() {
        return mutedUsers;
    }

    public void addUser(String id) {
        UserTypeObject user = new UserTypeObject(id);
        users.add(user);
    }
//
//
//    public ArrayList<ReminderObject> getReminders() {
//        return reminders;
//    }
//
//    public void setReminders(ArrayList<ReminderObject> reminders) {
//        this.reminders = reminders;
//    }
//
//    public ArrayList<ReminderObject> getSoonToExecute() {
//        return soonToExecute;
//    }
//
//    public void setSoonToExecute(ArrayList<ReminderObject> soonToExecute) {
//        this.soonToExecute = soonToExecute;
//    }
//
//    public ArrayList<WaiterObject> getGroupedUp() {
//        return groupedUp;
//    }
//
//    public void setGroupedUp(ArrayList<WaiterObject> groupedUp) {
//        this.groupedUp = groupedUp;
//    }
}
