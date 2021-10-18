package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.Optional;

public class ClientObject {
    public GlobalUserObject bot;
    public GlobalUserObject creator;
    private JDA object;

    public ClientObject() {
        this.object = Client.getClient();
        bot = new GlobalUserObject(object.getSelfUser(), this);
        creator = new GlobalUserObject(getUserByID(Globals.creatorID), this);
    }

    public JDA get() {
        return object;
    }

    public User getUserByID(long userID) {
        return object.retrieveUserById(userID).complete();
    }

    public List<Guild> getGuilds() {
        return object.getGuilds();
    }

    public Emote getEmojiByID(long emojiID) {
        Optional<Emote> emote = getGuilds().stream().flatMap(g-> g.getEmotes().stream().filter(e->e.getIdLong() == emojiID)).findFirst();
        if (emote.isPresent()) return emote.get();
        else return null;
    }
}
