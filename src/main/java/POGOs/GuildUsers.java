package POGOs;

import Commands.CommandObject;
import Interfaces.Command;
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

    public void addXP(CommandObject object) {
        //bots don't get XP
        if (object.author.isBot()) {
            return;
        }
        //you can only gain xp once per min
        if(object.guildContent.getSpokenUsers().contains(object.authorID)){
            return;
        }
        //you must have typed at least 10 chars to gain xp
        if (object.message.getContent().length() < 10){
            return;
        }
        //you cannot gain xp in an xpDenied channel
        ArrayList<String> xpChannels = object.guildConfig.getChannelIDsByType(Command.Channel_XP_DENIED);
        if (xpChannels != null && xpChannels.size() > 0){
            if (xpChannels.contains(object.channelSID)){
                return;
            }
        }
        object.guildContent.getSpokenUsers().add(object.authorID);
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
        user.addXP(object.guildConfig);
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
        for (UserTypeObject u: users){
            if (u.getID().equalsIgnoreCase(authorSID)){
                return u;
            }
        }
        return null;
    }
}
