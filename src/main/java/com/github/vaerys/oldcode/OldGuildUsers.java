package com.github.vaerys.oldcode;

import com.github.vaerys.objects.adminlevel.MutedUserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.pogos.GuildUsers;
import com.github.vaerys.templates.GlobalFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class OldGuildUsers extends GlobalFile {
    public static final String FILE_PATH = "Guild_Users.json";
    public double fileVersion = 1.2;
    public ArrayList<ProfileObject> profiles = new ArrayList<>();
    public ArrayList<MutedUserObject> mutedUsers = new ArrayList<>();

    public transient Map<Long, ProfileObject> newProfileSet = new HashMap<>();
    public transient Map<Long, MutedUserObject> newMutedUsersSet = new HashMap<>();

    public GuildUsers convert() {
        for (ProfileObject p: profiles) {
            if (newProfileSet.containsKey(p.getUserID())) {
                p.merge(newProfileSet.get(p.getUserID()));
            }
            newProfileSet.put(p.getUserID(), p);
        }
        for (MutedUserObject p: mutedUsers) {
            if (newMutedUsersSet.containsKey(p.getID())) continue;
            newMutedUsersSet.put(p.getID(), p);
        }
        return new GuildUsers(newProfileSet, newMutedUsersSet, fileVersion);
    }
}
