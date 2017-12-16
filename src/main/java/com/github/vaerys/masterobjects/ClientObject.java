package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClientObject {
    private IDiscordClient object;
    public IUser bot;
    public long longID;
    public List<IRole> roles;
    public Color color;
    public IUser creator;

    public ClientObject(IDiscordClient client, GuildObject guild) {
        this.object = client;
        if (!client.isReady()) return;
        this.bot = object.getOurUser();
        this.longID = bot.getLongID();
        if (guild != null && guild.get() != null) {
            roles = bot.getRolesForGuild(guild.get());
            color = Utility.getUsersColour(bot, guild.get());
        } else {
            roles = new ArrayList<>();
            color = Color.cyan;
        }
        creator = client.fetchUser(Globals.creatorID);
    }

    public IDiscordClient get() {
        return object;
    }

    public IUser getUserByID(long userID) {
        return object.getUserByID(userID);
    }
}
