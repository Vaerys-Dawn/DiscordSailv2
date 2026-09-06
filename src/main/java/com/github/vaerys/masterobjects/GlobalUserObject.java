package com.github.vaerys.masterobjects;

import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.userlevel.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class GlobalUserObject {

    protected User object;
    public ClientObject client;
    public long longID;
    public String name;
    public String username;
    public Color color;

    public String notAllowed;
    public boolean isPatron;
    public boolean isBot;
    public String avatarURL;
    public Instant creationDate;

    private Optional<HashMap<Long, List<CCommandObject>>> customCommands = Optional.empty();
    private Optional<HashMap<Long, List<CharacterObject>>> characters = Optional.empty();
    private Optional<HashMap<Long, List<ServerObject>>> servers = Optional.empty();
    private Optional<List<DailyMessage>> dailyMessages = Optional.empty();
    private Optional<HashMap<Long, ProfileObject>> profiles = Optional.empty();
    private Optional<List<ReminderObject>> reminders = Optional.empty();
    private Optional<List<GuildObject>> guilds = Optional.empty();

    Logger logger = LoggerFactory.getLogger(GlobalUserObject.class);

    public User get() {
        return object;
    }


    public GlobalUserObject(User user, ClientObject client) {
        this.client = client;
        init(user);
    }

    public GlobalUserObject(User user) {
        client = Client.getClientObject();
        init(user);
    }

    protected void init(User user) {
        object = user;
        longID = user.getIdLong();
        name = user.getName();
        username = user.getAsTag();
        color = getRandomColour();
        isPatron = Globals.getPatrons().contains(longID);
        isBot = user.isBot();
        notAllowed = "\\> I'm sorry " + name + ", I'm afraid I can't let you do that.";
        avatarURL = user.getAvatarUrl();
        creationDate = user.getTimeCreated().toInstant();
    }

    public GlobalUserObject(long userID) {
        client = Client.getClientObject();
        object = client.getUserByID(userID);
        longID = userID;
        if (object != null) {
            name = object.getName();
            username = object.getAsTag();
            isBot = object.isBot();
            avatarURL = object.getAvatarUrl();
            creationDate = object.getTimeCreated().toInstant();
        } else {
            name = "Unknown User";
            username = "Unknown User#0000";
            isBot = false;
            avatarURL = Utility.getDefaultAvatarURL(longID);
            creationDate = TimeUtil.getTimeCreated(longID).toInstant();
        }
        color = getRandomColour();
        isPatron = Globals.getPatrons().contains(longID);
        notAllowed = "\\> I'm sorry " + name + ", I'm afraid I can't let you do that.";
    }

    public Color getRandomColour() {
        Random random = new Random(longID);
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);
        return new Color(red, green, blue);
    }


    public List<CCommandObject> getCustomCommands() {
        if (!customCommands.isPresent()) {
            HashMap<Long, List<CCommandObject>> temp = new HashMap<>();
            getGuilds().forEach(g -> temp.put(g.longID, g.customCommands.getCommandList().stream().filter(c -> c.getUserID() == longID).collect(Collectors.toList())));
            customCommands = Optional.of(temp);
        }
        return customCommands.get().values().stream().flatMap(s -> s.stream()).collect(Collectors.toList());
    }

    public List<CCommandObject> getCustomCommands(long guildID) {
        if (!customCommands.isPresent()) {
            getCustomCommands();
        }
        return customCommands.get().get(guildID);
    }

    public List<CharacterObject> getCharacters() {
        if (!characters.isPresent()) {
            HashMap<Long, List<CharacterObject>> temp = new HashMap<>();
            getGuilds().forEach(g -> temp.put(g.longID, g.characters.getCharactersForUser(longID)));
            characters = Optional.of(temp);
        }
        return characters.get().values().stream().flatMap(s -> s.stream()).collect(Collectors.toList());
    }

    public List<CharacterObject> getCharacters(long guildID) {
        if (!characters.isPresent()) {
            getCharacters();
        }
        return characters.get().get(guildID);
    }

    public List<ServerObject> getServers() {
        if (!servers.isPresent()) {
            HashMap<Long, List<ServerObject>> temp = new HashMap<>();
            getGuilds().forEach(g -> temp.put(g.longID, g.servers.getServers().stream().filter(s -> s.getCreatorID() == longID).collect(Collectors.toList())));
            servers = Optional.of(temp);
        }
        return servers.get().values().stream().flatMap(s -> s.stream()).collect(Collectors.toList());
    }

    public List<ServerObject> getServers(long guildID) {
        if (!servers.isPresent()) {
            getServers();
        }
        return servers.get().get(guildID);
    }

    public List<ProfileObject> getProfiles() {
        if (!profiles.isPresent()) {
            HashMap<Long, ProfileObject> temp = new HashMap<>();
            List<GuildObject> guilds = getGuilds();
            guilds.forEach(guildObject -> {
                temp.put(guildObject.longID, guildObject.users.getUserByID(longID));
            });
            profiles = Optional.of(temp);
        }
        return new ArrayList<>(profiles.get().values());
    }

    public ProfileObject getProfile(long guildID) {
        if (!profiles.isPresent()) {
            getProfiles();
        }
        return profiles.get().get(guildID);
    }

    public List<ReminderObject> getReminders() {
        if (!reminders.isPresent()) {
            reminders = Optional.of(Globals.getGlobalData().getReminders().stream().filter(object1 -> object1.getUserID() == longID).collect(Collectors.toList()));
        }
        return reminders.get();
    }

    public List<DailyMessage> getDailyMessages() {
        if (!dailyMessages.isPresent()) {
            dailyMessages = Optional.of(Globals.getDailyMessages().getMessages().stream().filter(d -> d.getUserID() == longID).collect(Collectors.toList()));
        }
        return dailyMessages.get();
    }

    public List<GuildObject> getGuilds() {
        if (!guilds.isPresent()) {
            guilds = Optional.of(Globals.getGuilds().stream().filter(g -> checkForUser(longID, g)).collect(Collectors.toList()));
        }
        return guilds.get();
    }

    public long getAccountAgeSeconds() {
        Instant now = Instant.now();
        return now.getEpochSecond() - creationDate.getEpochSecond();
    }


    public boolean checkIsCreator() {
        return longID == client.creator.longID;
    }

    public boolean equals(UserObject user) {
        return user.longID == this.longID;
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

    public boolean isBlockedFromDms() {
        return Globals.getGlobalData().getBlockedFromDMS().contains(longID);
    }

    public UserObject getUserObject(GuildObject guild) {
        return new UserObject(object, guild);
    }

    public PrivateChannel getDmChannel() {
        return object.openPrivateChannel().complete();
    }

    public void queueDm(String s) {
        if (getDmChannel() == null) return;
        getDmChannel().sendMessage(s).queue();
    }

    public void queueDm(MessageEmbed embed) {
        if (getDmChannel() == null) return;
        getDmChannel().sendMessageEmbeds(embed).queue();
    }

    public void queueDm(String s, MessageEmbed embed) {
        if (getDmChannel() == null) return;
        getDmChannel().sendMessage(s).addEmbeds(embed).queue();
    }

    public Message sendDm(String s) {
        PrivateChannel channel = getDmChannel();
        if (channel == null) return null;
        return channel.sendMessage(s).complete();
    }

    public Message sendDm(MessageEmbed embed) {
        if (getDmChannel() == null) return null;
        return getDmChannel().sendMessageEmbeds(embed).complete();
    }

    public Message sendDm(String s, MessageEmbed embed) {
        if (getDmChannel() == null) return null;
        return getDmChannel().sendMessage(s).addEmbeds(embed).complete();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GlobalUserObject) {
            return longID == ((GlobalUserObject) obj).longID;
        } else {
            return super.equals(obj);
        }
    }

    public void sendErrorFileDm(String toString) {
        sendErrorFileDm("", toString);
    }

    public void sendErrorFileDm(String message, String error) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(Instant.now().getEpochSecond(), 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd-h-mm-ss", Locale.ENGLISH);
        String formattedDate = dateTime.format(formatter);
        String fileName = "Error_" + formattedDate + ".txt";
        ByteArrayInputStream stream = new ByteArrayInputStream(error.getBytes(StandardCharsets.UTF_8));
        if (message.isEmpty()) {
            getDmChannel().sendFiles(FileUpload.fromData(stream, fileName)).queue();
        } else {
            FileUpload upload = FileUpload.fromData(stream, fileName);
            getDmChannel().sendMessage(message).addFiles(upload).queue();
        }
    }


}
