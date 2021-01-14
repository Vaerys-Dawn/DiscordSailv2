package com.github.vaerys.objects.userlevel;

import com.github.vaerys.masterobjects.CommandObject;
import net.dv8tion.jda.api.entities.Member;

/**
 * Created by Vaerys on 31/05/2017.
 */
public class GroupUpObject {
    long userID;
    String presence;

    public GroupUpObject(String presence, long userID) {
        this.presence = presence;
        this.userID = userID;
    }

    public long getUserID() {
        return userID;
    }

    public String getPresence() {
        return presence;
    }

    public Member getUser(CommandObject command) {
        return command.guild.getUserByID(userID);
    }
}
