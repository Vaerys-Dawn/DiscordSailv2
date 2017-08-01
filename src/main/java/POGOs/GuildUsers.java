package POGOs;

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
    public ArrayList<UserCountDown> mutedUsers = new ArrayList<>();

    public ArrayList<UserTypeObject> getUsers() {
        return users;
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

    public UserTypeObject getUserByID(String authorSID) {
        for (UserTypeObject u : users) {
            if (u.getID().equalsIgnoreCase(authorSID)) {
                return u;
            }
        }
        return null;
    }
}
