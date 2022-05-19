package com.github.vaerys.pogos;

import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.MutedUserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.GlobalFile;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;

/**
 * Created by Vaerys on 03/02/2017.
 */
public class GuildUsers extends GlobalFile {
    public static final String FILE_PATH = "Guild_Users.json";
    private double fileVersion = 1.3;
    public Map<Long, ProfileObject> profiles = new HashMap<>();
    public Map<Long, MutedUserObject> mutedUsers = new HashMap<>();

    public GuildUsers() {
        // do nothing
    }

    public GuildUsers(Map<Long, ProfileObject> newProfileSet, Map<Long, MutedUserObject> newMutedUsersSet, double fileVersion) {
        this.profiles = newProfileSet;
        this.mutedUsers = newMutedUsersSet;
        this.fileVersion = fileVersion;
    }

    public Map<Long, ProfileObject> getProfiles() {
        return profiles;
    }

    public Map<Long, MutedUserObject> getMutedUsers() {
        return mutedUsers;
    }

    public MutedUserObject getMutedUser(long userID) {
        return mutedUsers.get(userID);
    }

    public ProfileObject addUser(long id) {
        if (profiles.containsKey(id)) return profiles.get(id);
        ProfileObject user = new ProfileObject(id);
        profiles.put(id, user);
        return user;
    }

    public ProfileObject getUserByID(long authorID) {
        return profiles.get(authorID);
    }

    public void addUser(ProfileObject profile) {
        if (profiles.containsKey(profile.getUserID())) {
            profile.merge(profiles.get(profile.getUserID()));
        }
        profiles.put(profile.getUserID(), profile);
    }

    public boolean checkForUser(long userID) {
        return profiles.containsKey(userID) || mutedUsers.containsKey(userID);
    }

    public boolean muteUser(long userID, long guildID, long time, List<Role> roles) {
        boolean found = false;
        if (mutedUsers.containsKey(userID)) {
            mutedUsers.get(userID).setRemainderSecs(time);
        }else {
            mutedUsers.put(userID, new MutedUserObject(userID, time, roles));
        }
        return RequestHandler.muteUser(guildID, userID, true);
    }

    public boolean muteUser(UserObject user, GuildObject guild, long timeSecs) {
        return muteUser(user.longID, guild.longID, timeSecs, user.roles);
    }

    public void muteUser(CommandObject command, int duration) {
        muteUser(command.user.longID, command.guild.longID, duration, command.user.roles);
    }

    public boolean unMuteUser(long userID, long guildID) {
        return RequestHandler.muteUser(guildID, userID, false);
    }

    public boolean unMuteUser(UserObject user, GuildObject guild) {
        return unMuteUser(user.longID, guild.longID);
    }

    public boolean isUserMuted(Member user) {
        return mutedUsers.containsKey(user.getIdLong());
    }
}
