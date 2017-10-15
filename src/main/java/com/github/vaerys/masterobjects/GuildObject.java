package com.github.vaerys.masterobjects;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.general.NewDailyMessage;
import com.github.vaerys.interfaces.ChannelSetting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildFile;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.UserRateObject;
import com.github.vaerys.pogos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.*;

import java.util.ArrayList;
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
    private List<UserRateObject> ratelimiting = new ArrayList<>();
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
        customCommands.initCustomCommands();
        this.client = new ClientObject(object.getClient(), this);
        loadCommandData();
    }

    public void loadCommandData() {
        this.commands = new ArrayList<>(Globals.getCommands(false));
        this.toggles = new ArrayList<>(Globals.getGuildGuildToggles());
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
            if (c.type().equals(channel)) {
                iterator.remove();
                logger.trace("Channel Setting: " + c.type() + " removed.");
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

    public List<UserRateObject> getRatelimiting() {
        return ratelimiting;
    }

    public List<Long> getSpokenUsers() {
        return spokenUsers;
    }

    public void forceClearRate() {
        ratelimiting = new ArrayList<>();
    }

    public boolean rateLimit(long userID) {
        int max = config.messageLimit;
        if (max == -1) {
            return false;
        }
        boolean isfound = false;
        for (UserRateObject r : ratelimiting) {
            if (r.getID() == userID) {
                r.counterUp();
                isfound = true;
                if (r.counter > max) {
                    return true;
                }
            }
        }
        if (!isfound) {
            ratelimiting.add(new UserRateObject(userID));
        }
        return false;
    }

    public int getUserRate(long userID) {
        for (UserRateObject u : ratelimiting) {
            if (u.getID() == userID) {
                return u.counter;
            }
        }
        return 0;
    }

    public void resetRateLimit() {
        ratelimiting.clear();
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
}
