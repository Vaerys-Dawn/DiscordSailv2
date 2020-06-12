package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.userlevel.ReminderObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalUserObject extends UserObject {

    public List<GuildObject> guilds = new ArrayList<>();
    public List<ProfileObject> profiles = new ArrayList<>();
    public List<ReminderObject> reminders;

    public GlobalUserObject(long lastDmUserID) {
        super(Client.getClient().fetchUser(lastDmUserID), null);
        for (GuildObject g : Globals.getGuilds()) {
            if (g.get().getUserByID(longID) != null) {
                customCommands.addAll(g.customCommands.getCommandList().stream().filter(cCommandObject -> cCommandObject.getUserID() == longID).collect(Collectors.toList()));
                characters.addAll(g.characters.getCharacters(g.get()).stream().filter(characterObject -> characterObject.getUserID() == longID).collect(Collectors.toList()));
                servers.addAll(g.servers.getServers().stream().filter(serverObject -> serverObject.getCreatorID() == longID).collect(Collectors.toList()));
                ProfileObject profileObject = g.users.getUserByID(longID);
                if (profileObject != null) profiles.add(profileObject);
                guilds.add(g);
            }
        }
        reminders = Globals.getGlobalData().getReminders().stream().filter(object1 -> object1.getUserID() == longID).collect(Collectors.toList());
        dailyMessages = Globals.getDailyMessages().getMessages().stream().filter(d -> d.getUserID() == longID).collect(Collectors.toList());
    }
}
