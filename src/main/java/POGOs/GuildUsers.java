package POGOs;

import Commands.CommandObject;
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

    public ArrayList<UserTypeObject> getUsers() {
        return users;
    }

    public void addXP(CommandObject object) {
        boolean isfound = false;
        UserTypeObject user = new UserTypeObject(object.authorID);
        for (UserTypeObject u: users){
            if (u.getID().equals(object.authorID)){
                isfound = true;
                user = u;
            }
        }
        if (!isfound) {
            users.add(user);
        }
        user.addXP(object.guildConfig);
    }

    public void addLevels(){
        for (UserTypeObject u: users){
            u.addLevel();
        }
    }

    public boolean isProperlyInit() {
        return properlyInit;
    }

    public void setProperlyInit(boolean properlyInit) {
        this.properlyInit = properlyInit;
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
