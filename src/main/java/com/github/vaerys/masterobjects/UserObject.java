package com.github.vaerys.masterobjects;

import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.*;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserObject {
    public ClientObject client;
    private IUser object;
    public long longID;
    public String stringID;
    public String name;
    public String displayName;
    public String username;
    public List<IRole> roles;
    public Color color;
    public List<CCommandObject> customCommands;
    public List<CharacterObject> characters;
    public List<ServerObject> servers;
    public List<DailyUserMessageObject> dailyMessages;
    public String notAllowed;


    public UserObject(IUser object, GuildObject guild) {
        this.client = new ClientObject(object.getClient(), guild);
        this.object = object;
        this.longID = object.getLongID();
        this.stringID = object.getStringID();
        this.name = object.getName();
        this.username = object.getName() + "#" + object.getDiscriminator();
        if (guild.get() != null) {
            this.displayName = object.getDisplayName(guild.get());
            this.roles = object.getRolesForGuild(guild.get());
            this.color = Utility.getUsersColour(get(), guild.get());
            customCommands = guild.customCommands.getCommandList().stream().filter(c -> c.getUserID().equals(stringID)).collect(Collectors.toList());
            characters = guild.characters.getCharacters(guild.get()).stream().filter(c -> c.getUserID().equals(stringID)).collect(Collectors.toList());
            servers = guild.servers.getServers().stream().filter(s -> s.getCreatorID().equals(stringID)).collect(Collectors.toList());
            dailyMessages = Globals.getDailyMessages().getMessages().stream().filter(d -> d.getUserID() == longID).collect(Collectors.toList());
        } else {
            this.displayName = name;
            this.roles = new ArrayList<>();
            this.color = Color.white;
            customCommands = new ArrayList<>();
            characters = new ArrayList<>();
            servers = new ArrayList<>();
            dailyMessages = new ArrayList<>();
        }
        notAllowed = "> I'm sorry " + displayName + ", I'm afraid I can't let you do that.";
    }

    public IUser get() {
        return object;
    }

    public ProfileObject getProfile(GuildObject guild) {
        return guild.users.getUserByID(stringID);
    }

    public boolean isPrivateProfile(GuildObject guild) {
        ProfileObject profile = getProfile(guild);
        if (profile == null) {
            return false;
        }
        if (profile.getSettings() == null || profile.getSettings().size() == 0) {
            return false;
        } else {
            return getProfile(guild).getSettings().contains(UserSetting.PRIVATE_PROFILE);
        }
    }

    public boolean showRank(GuildObject guild) {
        return XpHandler.rank(guild.users, guild.get(), stringID) != -1;
    }
}
