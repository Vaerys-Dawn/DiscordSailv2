package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Globals;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;

public class ClientObject {
    public UserObject bot;
    public UserObject creator;
    private IDiscordClient object;

    public ClientObject(IDiscordClient client, GuildObject guild) {
        this.object = client;
        if (!client.isReady()) return;
        bot = new UserObject(client.getOurUser(), guild, this);
        creator = new UserObject(client.fetchUser(Globals.creatorID), guild, this);

    }

    public IDiscordClient get() {
        return object;
    }

    public IUser getUserByID(long userID) {
        return object.getUserByID(userID);
    }

    public IUser fetchUser(long l) {
        return RequestBuffer.request(() -> {
            return object.fetchUser(l);
        }).get();
    }
}
