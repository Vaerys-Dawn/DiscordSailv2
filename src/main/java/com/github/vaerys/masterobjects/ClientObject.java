package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

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
        if (userID > 9223372036854775807L || userID < 0) throw new IllegalStateException(userID + " is not a valid UserID");
        return object.retrieveUserById(userID).complete();
    }

    public List<Guild> getGuilds() {
        return object.getGuilds();
    }

    public RichCustomEmoji getEmojiByID(long emojiID) {
        Optional<RichCustomEmoji> emote = getGuilds().stream().flatMap(g -> g.getEmojis().stream().filter(e -> e.getIdLong() == emojiID)).findFirst();
        return emote.orElse(null);
    }
}
