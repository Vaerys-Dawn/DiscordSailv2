package com.github.vaerys.pogos;

import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.adminlevel.UserCountDown;
import com.github.vaerys.templates.GlobalFile;

import java.util.ArrayList;

/**
 * Created by Vaerys on 03/02/2017.
 */
public class GuildUsers extends GlobalFile {
    public static final String FILE_PATH = "Guild_Users.json";
    private double fileVersion = 1.2;
    public ArrayList<ProfileObject> profiles = new ArrayList<>();
    public ArrayList<UserCountDown> mutedUsers = new ArrayList<>();

    public ArrayList<ProfileObject> getProfiles() {
        return profiles;
    }

    public boolean muteUser(long userID, long time, long guildID) {
        boolean found = false;
        for (UserCountDown c : mutedUsers) {
            if (c.getID() == userID) {
                c.setRemainderSecs(time);
                found = true;
            }
        }
        if (!found) {
            mutedUsers.add(new UserCountDown(userID, time));
        }
        return RequestHandler.muteUser(guildID, userID, true);
    }

    public boolean unMuteUser(long userID, long guildID) {
        for (int i = 0; i < mutedUsers.size(); i++) {
            if (mutedUsers.get(i).getID() == userID) {
                mutedUsers.remove(i);
            }
        }
        return RequestHandler.muteUser(guildID, userID, false);
    }

    public ArrayList<UserCountDown> getMutedUsers() {
        return mutedUsers;
    }

    public ProfileObject addUser(long id) {
        ProfileObject user = new ProfileObject(id);
        profiles.add(user);
        return user;
    }

    public ProfileObject getUserByID(long authorID) {
        for (ProfileObject u : profiles) {
            if (u.getUserID() == authorID) {
                return u;
            }
        }
        return null;
    }

    public void addUser(ProfileObject profile) {
        profiles.add(profile);
    }

    public boolean checkForUser(long userID) {
        if (profiles.stream().map(c -> c.getUserID()).filter(c -> c == userID).toArray().length != 0) return true;
        if (mutedUsers.stream().map(c -> c.getID()).filter(c -> c == userID).toArray().length != 0) return true;
        return false;
    }
}
