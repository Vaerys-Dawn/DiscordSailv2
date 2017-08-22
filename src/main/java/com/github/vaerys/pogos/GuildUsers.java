package com.github.vaerys.pogos;

import com.github.vaerys.interfaces.GuildFile;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.UserCountDown;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Vaerys on 03/02/2017.
 */
public class GuildUsers extends GuildFile {
    public static final String FILE_PATH = "Guild_Users.json";
    private boolean properlyInit;
    // TODO: 12/08/2017 make this just a liiitle bit less of a hack.
    @SerializedName("users")
    public ArrayList<ProfileObject> profiles = new ArrayList<>();
    public ArrayList<UserCountDown> mutedUsers = new ArrayList<>();

    public ArrayList<ProfileObject> getProfiles() {
        return profiles;
    }

    public boolean muteUser(String userID, long time, long guildID) {
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

    public boolean unMuteUser(String userID, long guildID) {
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
        ProfileObject user = new ProfileObject(id);
        profiles.add(user);
    }

    public ProfileObject getUserByID(String authorSID) {
        for (ProfileObject u : profiles) {
            if (u.getID().equalsIgnoreCase(authorSID)) {
                return u;
            }
        }
        return null;
    }

    public void addUser(ProfileObject profile) {
        profiles.add(profile);
    }
}
