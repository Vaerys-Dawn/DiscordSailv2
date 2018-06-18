package com.github.vaerys.masterobjects;

import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.userlevel.*;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import sx.blah.discord.api.internal.DiscordUtils;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.cache.LongMap;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Wrapper for the User API object. Contains All bot information linked to that user for the Guild assigned to it.
 */

public class UserObject {
    public ClientObject client;
    public long longID;
    public String name;
    public String displayName;
    public String username;
    public List<IRole> roles;
    public Color color;
    public List<CCommandObject> customCommands = new LinkedList<>();
    public List<CharacterObject> characters = new LinkedList<>();
    public List<ServerObject> servers = new LinkedList<>();
    public List<DailyMessage> dailyMessages = new LinkedList<>();
    public String notAllowed;
    public boolean isPatron;
    public boolean isBot;
    public String avatarURL;
    public Instant creationDate;
    IUser object;


    public UserObject(IUser object, GuildObject guild) {
        if (object == null) return;
        this.client = new ClientObject(guild);
        init(object, guild, false);
    }

    public UserObject(IUser object, GuildObject guild, boolean light) {
        if (object == null) return;
        this.client = new ClientObject(guild);
        init(object, guild, light);
    }

    public UserObject(IUser object, GuildObject guild, ClientObject client) {
        this.client = client;
        init(object, guild, false);
    }

    public static boolean checkForUser(long userID, GuildObject guild) {
        if (guild.adminCCs.checkForUser(userID)) return true;
        if (guild.channelData.checkForUser(userID)) return true;
        if (guild.characters.checkForUser(userID)) return true;
        if (guild.competition.checkForUser(userID)) return true;
        if (guild.customCommands.checkForUser(userID)) return true;
        if (guild.servers.checkForUser(userID)) return true;
        if (guild.users.checkForUser(userID)) return true;
        return false;
    }

    public static UserObject getNewUserObject(long userID, GuildObject guild) {
        if (!checkForUser(userID, guild)) return null;
        UserObject user = new UserObject(userID, guild);
        return user;
    }

    private UserObject(long userID, GuildObject guild) {
        this.client = guild.client;
        IUser user = guild.getUserByID(userID);
        if (user == null) user = Client.getClient().getUserByID(userID);
        if (user == null) initNull(userID, guild);
        else init(user, guild, true);
    }

    private void initNull(long id, GuildObject guild) {
        longID = id;
        object = null;
        name = "Unknown User";
        displayName = name;
        username = name + "#0000";
        color = Color.gray;
        roles = new LinkedList<>();
        if (guild != null) {
            loadExtraData(guild);
        }
        notAllowed = "> I'm sorry " + displayName + ", I'm afraid I can't let you do that.";
        isPatron = Globals.getPatrons().contains(longID);
        isBot = false;
        avatarURL = Utility.getDefaultAvatarURL(longID);
        creationDate = DiscordUtils.getSnowflakeTimeFromID(longID);
    }

    private void init(IUser object, GuildObject guild, boolean light) {
        if (object == null) {
            initNull(-1, guild);
            return;
        }
        this.object = object;
        this.longID = object.getLongID();
        this.name = object.getName();
        this.username = object.getName() + "#" + object.getDiscriminator();
        this.isBot = object.isBot();
        this.avatarURL = object.getAvatarURL();
        this.creationDate = object.getCreationDate();
        if (guild != null && guild.get() != null) {
            this.displayName = object.getDisplayName(guild.get());
            this.roles = object.getRolesForGuild(guild.get());
            this.color = GuildHandler.getUsersColour(get(), guild.get());
            if (!light) {
                loadExtraData(guild);
            }
        } else {
            this.displayName = name;
            this.roles = new ArrayList<>();
            this.color = Color.white;
        }
        escapeMentions();
        notAllowed = "> I'm sorry " + displayName + ", I'm afraid I can't let you do that.";
        isPatron = Globals.getPatrons().contains(longID);
    }

    private void escapeMentions() {
        Pattern pattern = Pattern.compile("(?i)(@everyone|@here|<@[!|&]?[0-9]*>)");
        Matcher nameMatcher = pattern.matcher(name);
        Matcher displayMatcher = pattern.matcher(displayName);

        if (nameMatcher.find() || displayMatcher.find()) {
            name = name.replace("@", "@" + Command.spacer);
            displayName = displayName.replace("@", "@" + Command.spacer);
            username = username.replace("@", "@" + Command.spacer);
        }
    }

    public UserObject loadExtraData(GuildObject guild) {
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
        ProfileObject profile = guild.users.getUserByID(longID);
        if (profile == null && (object != null && !object.isBot())) {
            profile = guild.users.addUser(longID);
        }
        if (profile != null && profile.getSettings() != null && profile.getSettings().size() != 0) {
            profile.setSettings(new ArrayList<>(profile.getSettings().stream().distinct().collect(Collectors.toList())));
        }
        return profile;
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
        return PixelHandler.rank(guild.users, guild.get(), longID) != -1;
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

    public Color getRandomColour() {
        Random random = new Random(longID);
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);
        return new Color(red, green, blue);
    }

    public boolean isDecaying(GuildObject guild) {
        return getProfile(guild).daysDecayed(guild) > 7;
    }

    public ProfileObject addProfile(GuildObject guild) {
        return guild.users.addUser(longID);
    }

    public int getRewardValue(CommandObject command) {
        return PixelHandler.getRewardCount(command.guild, longID);
    }

    public List<IRole> getCosmeticRoles(CommandObject command) {
        return roles.stream().filter(iRole -> command.guild.config.isRoleCosmetic(iRole.getLongID())).collect(Collectors.toList());
    }

    public EnumSet<Permissions> getPermissions(GuildObject guild) {
        return object.getPermissionsForGuild(guild.get());
    }

    public boolean checkIsCreator() {
        return longID == client.creator.longID;
    }

    public ProfileObject getProfile(CommandObject command) {
        return getProfile(command.guild);
    }

    public long getAccountAgeSeconds() {
        Instant now = Instant.now();
        return now.getEpochSecond() - creationDate.getEpochSecond();
    }
}
