package com.github.vaerys.masterobjects;

import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.*;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.cache.LongMap;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class UserObject {
    public ClientObject client;
    IUser object;
    public long longID;
    public String name;
    public String displayName;
    public String username;
    public List<IRole> roles;
    public Color color;
    public List<CCommandObject> customCommands;
    public List<CharacterObject> characters;
    public List<ServerObject> servers;
    public List<DailyMessage> dailyMessages;
    public String notAllowed;
    public boolean isPatron;


    public UserObject(IUser object, GuildObject guild) {
        if (object == null) return;
        this.client = new ClientObject(object.getClient(), guild);
        init(object, guild, false);
    }

    public UserObject(IUser object, GuildObject guild, boolean light) {
        if (object == null) return;
        this.client = new ClientObject(object.getClient(), guild);
        init(object, guild, light);
    }

    public UserObject(IUser object, GuildObject guild, ClientObject client) {
        this.client = client;
        init(object, guild, false);
    }

    private void init(IUser object, GuildObject guild, boolean light) {
        if (object == null) return;
        this.object = object;
        this.longID = object.getLongID();
        this.name = object.getName();
        this.username = object.getName() + "#" + object.getDiscriminator();
        if (guild != null && guild.get() != null) {
            this.displayName = object.getDisplayName(guild.get());
            this.roles = object.getRolesForGuild(guild.get());
            this.color = Utility.getUsersColour(get(), guild.get());
            if (!light) {
                customCommands = guild.customCommands.getCommandList().stream().filter(c -> c.getUserID() == longID).collect(Collectors.toList());
                characters = guild.characters.getCharacters(guild.get()).stream().filter(c -> c.getUserID() == longID).collect(Collectors.toList());
                servers = guild.servers.getServers().stream().filter(s -> s.getCreatorID() == longID).collect(Collectors.toList());
                dailyMessages = Globals.getDailyMessages().getMessages().stream().filter(d -> d.getUserID() == longID).collect(Collectors.toList());
            } else {
                customCommands = new ArrayList<>();
                characters = new ArrayList<>();
                servers = new ArrayList<>();
                dailyMessages = new ArrayList<>();
            }
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
        isPatron = Globals.getPatrons().contains(longID);
    }

    public UserObject loadExtraData(GuildObject guild){
        customCommands = guild.customCommands.getCommandList().stream().filter(c -> c.getUserID() == longID).collect(Collectors.toList());
        characters = guild.characters.getCharacters(guild.get()).stream().filter(c -> c.getUserID() == longID).collect(Collectors.toList());
        servers = guild.servers.getServers().stream().filter(s -> s.getCreatorID() == longID).collect(Collectors.toList());
        dailyMessages = Globals.getDailyMessages().getMessages().stream().filter(d -> d.getUserID() == longID).collect(Collectors.toList());
        return this;
    }

    public List<IChannel> getVisibleChannels(List<IChannel> channels) {
        List<IChannel> newSet = new ArrayList<>();
        for (IChannel c : channels) {
            boolean hasSend = false;
            boolean hasRead = false;
            boolean everOverride = false;
            c.getModifiedPermissions(object);
            LongMap<PermissionOverride> overrides = c.getRoleOverrides();
            EnumSet<Permissions> everyoneOverride = c.getModifiedPermissions(c.getGuild().getEveryoneRole());

            for (IRole r : roles) {
                PermissionOverride override = overrides.get(r.getLongID());
                if (override != null && !r.isEveryoneRole()) {
                    if (override.allow().contains(Permissions.READ_MESSAGES)) hasRead = true;
                    if (override.allow().contains(Permissions.SEND_MESSAGES)) hasSend = true;
                }
            }
            if (everyoneOverride.contains(Permissions.READ_MESSAGES)) {
                if (everyoneOverride.contains(Permissions.SEND_MESSAGES) || hasSend) {
                    everOverride = true;
                }
            }
            if ((hasRead && hasSend) || everOverride) {
                newSet.add(c);
            }
        }
        return newSet;
    }

    public IUser get() {
        return object;
    }

    public ProfileObject getProfile(GuildObject guild) {
        return guild.users.getUserByID(longID);
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
        return XpHandler.rank(guild.users, guild.get(), longID) != -1;
    }


    public IChannel getDmChannel() {
        return RequestBuffer.request(() -> {
            return object.getOrCreatePMChannel();
        }).get();
    }

    public RequestBuffer.RequestFuture<IMessage> sendDm(String s) {
        return RequestHandler.sendMessage(s, getDmChannel());
    }

    public String mention() {
        return object.mention();
    }

    public boolean isBlockedFromDms() {
        return Globals.getGlobalData().getBlockedFromDMS().contains(longID);
    }

    public RequestBuffer.RequestFuture<IMessage> sendEmbededDm(String s, XEmbedBuilder builder) {
        return RequestHandler.sendEmbedMessage(s, builder, getDmChannel());
    }

    public String getAvatarURL() {
        return object.getAvatarURL();
    }
}
