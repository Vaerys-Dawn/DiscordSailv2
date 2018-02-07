package com.github.vaerys.masterobjects;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.general.NewDailyMessage;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.objects.UserRateObject;
import com.github.vaerys.pogos.*;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildFile;
import com.github.vaerys.templates.GuildToggle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GuildObject {
    public ClientObject client;
    private IGuild object;
    public long longID;
    public GuildConfig config;
    public CustomCommands customCommands;
    public Servers servers;
    public Characters characters;
    public Competition competition;
    public GuildUsers users;
    public ChannelData channelData;
    public List<GuildFile> guildFiles;
    public List<Command> commands;
    public List<GuildToggle> toggles;
    public List<ChannelSetting> channelSettings;
    public List<String> commandTypes;
    private List<UserRateObject> rateLimiting = new ArrayList<>();
    private List<Long> spokenUsers = new ArrayList<>();
    private List<GuildToggle> toRemove = new ArrayList<>();

    private final static Logger logger = LoggerFactory.getLogger(GuildObject.class);

    public GuildObject(IGuild object) {
        this.object = object;
        this.longID = object.getLongID();
        this.config = (GuildConfig) GuildConfig.create(GuildConfig.FILE_PATH, longID, new GuildConfig());
        this.customCommands = (CustomCommands) CustomCommands.create(CustomCommands.FILE_PATH, longID, new CustomCommands());
        this.servers = (Servers) Servers.create(Servers.FILE_PATH, longID, new Servers());
        this.characters = (Characters) Characters.create(Characters.FILE_PATH, longID, new Characters());
        this.competition = (Competition) Competition.create(Competition.FILE_PATH, longID, new Competition());
        this.users = (GuildUsers) GuildUsers.create(GuildUsers.FILE_PATH, longID, new GuildUsers());
        this.channelData = (ChannelData) ChannelData.create(ChannelData.FILE_PATH, longID, new ChannelData());
        this.guildFiles = new ArrayList<GuildFile>() {{
            add(config);
            add(customCommands);
            add(servers);
            add(characters);
            add(competition);
            add(users);
            add(channelData);
        }};
        customCommands.initCustomCommands(get());
        this.client = new ClientObject(object.getClient(), this);
        loadCommandData();
    }

    public void loadCommandData() {
        this.commands = new ArrayList<>(Globals.getCommands(false));
        this.toggles = new ArrayList<>(Globals.getGuildToggles());
        this.channelSettings = new ArrayList<>(Globals.getChannelSettings());
        this.commandTypes = new ArrayList<>(Globals.getCommandTypes());
        checkToggles();
    }

    public GuildObject() {
        this.client = new ClientObject(Globals.getClient(), this);
        this.object = null;
        this.longID = -1;
        this.config = new GuildConfig();
        this.customCommands = new CustomCommands();
        this.servers = new Servers();
        this.characters = new Characters();
        this.competition = new Competition();
        this.users = new GuildUsers();
        this.channelData = new ChannelData();
        this.guildFiles = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.commands = new ArrayList<>(Globals.getCommands(true));
    }

    public IGuild get() {
        return object;
    }

    private void checkToggles() {
        toRemove = new ArrayList<>();
        for (GuildToggle g : toggles) {
            if (!g.get(config)) {
                g.execute(this);
            }
        }

        for (GuildToggle g : toRemove) {
            ListIterator iterator = toggles.listIterator();
            while (iterator.hasNext()) {
                GuildToggle toggle = (GuildToggle) iterator.next();
                if (toggle.name().equalsIgnoreCase(g.name())) {
                    if (g.isModule()) {
                        logger.trace("Module: " + g.name() + " removed.");
                    } else {
                        logger.trace("Toggle: " + g.name() + " removed.");
                    }
                    iterator.remove();
                }
            }
        }
        IChannel channel = client.get().getChannelByID(Globals.queueChannelID);
        if (channel == null) {
            ListIterator iterator = commands.listIterator();
            while (iterator.hasNext()) {
                Command command = (Command) iterator.next();
                if (command.names()[0] == new NewDailyMessage().names()[0]) {
                    logger.trace(longID + ": Removed newDailyMsg command.");
                    iterator.remove();
                }
            }
        }
    }

    public void removeCommandsByType(String type) {
        ListIterator iterator = commands.listIterator();
        while (iterator.hasNext()) {
            Command c = (Command) iterator.next();
            if (c.type().equals(type)) {
                logger.trace("Command: " + c.names()[0] + " removed.");
                iterator.remove();
            }
        }
        for (String s : commandTypes) {
            if (s.equals(type)) {
                logger.trace("Type: " + s + " removed.");
                commandTypes.remove(s);
                return;
            }
        }
    }

    public void removeChannel(String channel) {
        ListIterator iterator = channelSettings.listIterator();
        while (iterator.hasNext()) {
            ChannelSetting c = (ChannelSetting) iterator.next();
            if (c.name().equals(channel)) {
                iterator.remove();
                logger.trace("Channel Setting: " + c.name() + " removed.");
            }
        }
    }

    public void removeCommand(String[] names) {
        ListIterator iterator = commands.listIterator();
        while (iterator.hasNext()) {
            Command c = (Command) iterator.next();
            if (c.names()[0].equals(names[0])) {
                iterator.remove();
                logger.trace("Command: " + c.names()[0] + " removed.");
            }
        }
    }

    public void removeToggle(String name) {
        for (GuildToggle g : toggles) {
            if (g.name().equalsIgnoreCase(name)) {
                toRemove.add(g);
            }
        }
    }

    public List<UserRateObject> getRateLimiting() {
        return rateLimiting;
    }

    public List<Long> getSpokenUsers() {
        return spokenUsers;
    }

    public void forceClearRate() {
        rateLimiting = new ArrayList<>();
    }

    public boolean rateLimit(long userID) {
        int max = config.messageLimit;
        if (max == -1) {
            return false;
        }
        boolean isfound = false;
        for (UserRateObject r : rateLimiting) {
            if (r.getID() == userID) {
                r.counterUp();
                isfound = true;
                if (r.counter > max) {
                    return true;
                }
            }
        }
        if (!isfound) {
            rateLimiting.add(new UserRateObject(userID));
        }
        return false;
    }

    public int getUserRate(long userID) {
        for (UserRateObject u : rateLimiting) {
            if (u.getID() == userID) {
                return u.counter;
            }
        }
        return 0;
    }

    public void resetRateLimit() {
        rateLimiting.clear();
    }

    public List<String> getAllTypes(CommandObject command) {
        List types = new ArrayList<>(commandTypes);
        if (command.user.get().equals(command.client.creator)) {
            types.add(Command.TYPE_CREATOR);
        }
        types.add(Command.TYPE_DM);
        return types;
    }

    public List<Command> getAllCommands(CommandObject command) {
        List<Command> allCommands = new ArrayList<>(commands);
        if (command.user.get().equals(command.client.creator)) {
            allCommands.addAll(Globals.getALLCreatorCommands());
        }
        return allCommands;
    }

    public IChannel getChannelByID(long id) {
        return object.getChannelByID(id);
    }

    public IUser getUserByID(long id) {
        return object.getUserByID(id);
    }

    public IRole getRoleByID(long id) {
        return object.getRoleByID(id);
    }

    public IVoiceChannel getVoiceChannelByID(long id) {
        return object.getVoiceChannelByID(id);
    }

    public IEmoji getEmojiById(long id) {
        return object.getEmojiByID(id);
    }

    public long getOwnerID() {
        return object.getOwnerLongID();
    }

    public IUser getOwner() {
        return object.getOwner();
    }

    public List<IUser> getUsers() {
        return object.getUsers();
    }

    public IEmoji getEmojiByName(String name) {
        return object.getEmojiByName(name);
    }

    public void handleWelcome(CommandObject command) {
        if (config.welcomeMessage) {
            return;
        }
        if (command.guild.get() == null || command.channel == null) {
            return;
        }
        IChannel general = getChannelByType(Command.TYPE_GENERAL);
        if (general != null && command.channel.longID != general.getLongID()) {
            return;
        }
        IMessage message = RequestHandler.sendMessage(Constants.getWelcomeMessage(command), command.channel.get()).get();
        if (message != null) {
            command.guild.config.welcomeMessage = true;
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(5 * 60 * 1000);
                    RequestHandler.deleteMessage(message);
                } catch (InterruptedException e) {
                    // do nothing
                }
            });
            thread.start();
        }
    }

    public void resetOffenders() {
        config.resetOffenders();
    }

    public IChannel getChannelByType(String type) {
        List<IChannel> channels = getChannelsByType(type);
        if (channels.size() != 0) {
            return channels.get(0);
        }
        return null;
    }

    public List<IChannel> getChannelsByType(String type) {
        List<IChannel> channels = new ArrayList<>();
        for (ChannelSettingObject c : config.getChannelSettings()) {
            if (c.getType().equalsIgnoreCase(type)) {
                for (long s : c.getChannelIDs()) {
                    IChannel channel = getChannelByID(s);
                    if (channel != null) {
                        channels.add(channel);
                    }
                }
            }
        }
        return channels;
    }

    public List<IRole> getRewardRoles() {
        List<IRole> roles = new LinkedList<>();
        config.getRewardRoles().forEach(rewardRoleObject -> roles.add(object.getRoleByID(rewardRoleObject.getRoleID())));
        return roles;
    }

    public IRole getMutedRole() {
        return object.getRoleByID(config.getMutedRoleID());
    }
}
