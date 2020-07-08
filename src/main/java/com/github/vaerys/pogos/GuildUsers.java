package com.github.vaerys.pogos;

import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.MutedUserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.GlobalFile;
import sx.blah.discord.handle.obj.Role;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 03/02/2017.
 */
public class GuildUsers extends GlobalFile {
    public static final String FILE_PATH = "Guild_Users.json";
    private double fileVersion = 1.2;
    public ArrayList<ProfileObject> profiles = new ArrayList<>();
    public ArrayList<MutedUserObject> mutedUsers = new ArrayList<>();

    public ArrayList<ProfileObject> getProfiles() {
        return profiles;
    }

    public ArrayList<MutedUserObject> getMutedUsers() {
        return mutedUsers;
    }

    public MutedUserObject getMutedUser(long userID) {
        for (MutedUserObject m : mutedUsers) {
            if (m.getID() == userID) return m;
        }
        return null;
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

    public boolean muteUser(long userID, long guildID, long time, List<Role> roles) {
        boolean found = false;
        for (MutedUserObject c : mutedUsers) {
            if (c.getID() == userID) {
                c.setRemainderSecs(time);
                found = true;
            }
        }
        if (!found) {
            mutedUsers.add(new MutedUserObject(userID, time, roles));
        }
        return RequestHandler.muteUser(guildID, userID, true).get();
    }

    public boolean muteUser(UserObject user, GuildObject guild, long timeSecs) {
        return muteUser(user.longID, guild.longID, timeSecs, user.roles);
    }

    public void muteUser(CommandObject command, int duration) {
        muteUser(command.user.longID, command.guild.longID, duration, command.user.roles);
    }

    public boolean unMuteUser(long userID, long guildID) {
        boolean result = RequestHandler.muteUser(guildID, userID, false).get();
        return result;
    }

    public boolean unMuteUser(UserObject user, GuildObject guild) {
        return unMuteUser(user.longID, guild.longID);
    }

    public boolean isUserMuted(IUser user) {
        for (MutedUserObject u : mutedUsers) {
            if (u.getID() == user.getIdLong()) return true;
        }
        return false;
    }
}
