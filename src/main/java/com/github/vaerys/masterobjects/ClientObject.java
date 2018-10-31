package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;

public class ClientObject {
    public UserObject bot;
    public UserObject creator;
    private IDiscordClient object;

    public ClientObject(GuildObject guild) {
        this.object = Client.getClient();
        if (!object.isReady()) return;
        bot = new UserObject(object.getOurUser(), guild, this);
        creator = new UserObject(fetchUser(Globals.creatorID), guild, this);
    }

    public IDiscordClient get() {
        return object;
    }

    public IUser getUserByID(long userID) {
        return object.getUserByID(userID);
    }

    public IUser fetchUser(long l) {
        return RequestBuffer.request(() -> object.fetchUser(l)).get();
    }

    public List<IGuild> getGuilds() {
        return object.getGuilds();
    }

    public IEmoji getEmojiByID(long emojiID) {
        List<IEmoji> emojis = new ArrayList<>();
        getGuilds().forEach(g -> emojis.addAll(g.getEmojis()));
        for (IEmoji e : emojis) {
            if (e.getLongID() == emojiID) return e;
        }
        return null;
    }
}
