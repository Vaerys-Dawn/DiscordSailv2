package com.github.vaerys.pogos;

import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.MutedUserObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.GlobalFile;
import com.sun.istack.internal.Nullable;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;

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

    public boolean muteUser(long userID, long guildID, long time) {
        boolean found = false;
        for (MutedUserObject c : mutedUsers) {
            if (c.getID() == userID) {
                c.setRemainderSecs(time);
                found = true;
            }
        }
        if (!found) {
            mutedUsers.add(new MutedUserObject(userID, time));
        }
        return RequestHandler.muteUser(guildID, userID, true).get();
    }

    public boolean unMuteUser(long userID, long guildID) {
        boolean result = RequestHandler.muteUser(guildID, userID, false).get();
        mutedUsers.removeIf(m -> m.getID() == userID);
        return result;
    }

    public ArrayList<MutedUserObject> getMutedUsers() {
        return mutedUsers;
    }

    @Nullable
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

    public boolean muteUser(UserObject user, GuildObject guild, long timeSecs) {
        return muteUser(user.longID, guild.longID, timeSecs);
    }

    public boolean unMuteUser(UserObject user, GuildObject guild) {
        return unMuteUser(user.longID, guild.longID);
    }

    public boolean isUserMuted(IUser user) {
        for (MutedUserObject u : mutedUsers) {
            if (u.getID() == user.getLongID()) return true;
        }
        return false;
    }

    public void muteUser(CommandObject command, int i) {
        muteUser(command.user.longID, command.guild.longID, i);
    }
}
