package com.github.vaerys.masterobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.general.NewDailyMessage;
import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.objects.GuildLogObject;
import com.github.vaerys.objects.LogObject;
import com.github.vaerys.objects.UserRateObject;
import com.github.vaerys.pogos.ChannelData;
import com.github.vaerys.pogos.Characters;
import com.github.vaerys.pogos.Competition;
import com.github.vaerys.pogos.CustomCommands;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.pogos.GuildLog;
import com.github.vaerys.pogos.GuildUsers;
import com.github.vaerys.pogos.Servers;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildFile;
import com.github.vaerys.templates.GuildToggle;
import com.github.vaerys.templates.SAILType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

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
    public GuildLog guildLog;
    public List<GuildFile> guildFiles;
    public List<Command> commands;
    public List<GuildToggle> toggles;
    public ChannelSetting[] channelSettings;
    public List<SAILType> commandTypes;
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
        this.guildLog = (GuildLog) GuildLog.create(GuildLog.FILE_PATH, longID, new GuildLog());
        this.guildFiles = new ArrayList<GuildFile>() {{
            add(config);
            add(customCommands);
            add(servers);
            add(characters);
            add(competition);
            add(users);
            add(channelData);
            add(guildLog);
        }};
        customCommands.initCustomCommands(get());
        this.client = new ClientObject(object.getClient(), this);
        loadCommandData();
    }

    public void sendDebugLog(CommandObject command, String type, String name, String contents) {
        GuildLogObject object = new GuildLogObject(command, type, name, contents);
        String output = object.getOutput(command);
        if (command.guild.get() != null) {
            guildLog.addLog(object, command.guild.longID);
        } else {
            Globals.addToLog(new LogObject(object, -1));
        }
        logger.trace(output);
    }


    public void loadCommandData() {
        this.commands = Globals.getCommands(false);
        this.toggles = Globals.getGuildToggles();
        this.channelSettings = Globals.getChannelSettings();
        this.commandTypes = Arrays.asList(Globals.getCommandTypes());
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
                if (toggle.name() ==g.name()) {
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
                if (command.names[0] == new NewDailyMessage().names[0]) {
                    logger.trace(longID + ": Removed newDailyMsg command.");
                    iterator.remove();
                }
            }
        }
    }

    public void removeCommandsByType(SAILType type) {
        ListIterator iterator = commands.listIterator();
        while (iterator.hasNext()) {
            Command c = (Command) iterator.next();
            if (c.type == type) {
                logger.trace("Command: " + c.names[0] + " removed.");
                iterator.remove();
            }
        }
        for (SAILType t : commandTypes) {
            if (t == type) {
                logger.trace("Type: " + t.toString() + " removed.");
                commandTypes.remove(t);
                return;
            }
        }
    }

    /**
     * Removes a channel setting from the specified channel
     * @param channel
     */
    public void removeChannelSetting(String channel) {
        for (ChannelSetting s : channelSettings) {
            if (s.toString().equals(channel)) {
                logger.trace("Channel Setting: " + s.toString() + " removed.");
            }
        }
    }

    /**
     * 
     * @param names
     */
    public void removeCommand(String[] names) {
        ListIterator iterator = commands.listIterator();
        while (iterator.hasNext()) {
            Command c = (Command) iterator.next();
            if (c.names[0].equals(names[0])) {
                iterator.remove();
                logger.trace("Command: " + c.names[0] + " removed.");
            }
        }
    }

    public void removeToggle(SAILType name) {
        for (GuildToggle g : toggles) {
            if (g.name() == name) {
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

    public List<SAILType> getAllTypes(CommandObject command) {
        List<SAILType> types = new ArrayList<>(commandTypes.size());
        if (command.user.get().equals(command.client.creator)) {
            types.add(SAILType.CREATOR);
        }
        types.add(SAILType.DM);
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
        IChannel general = getChannelByType(ChannelSetting.GENERAL);
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

    public IChannel getChannelByType(ChannelSetting type) {
        List<IChannel> channels = getChannelsByType(type);
        if (channels.size() != 0) {
            return channels.get(0);
        }
        return null;
    }

    public List<IChannel> getChannelsByType(ChannelSetting type) {
        List<IChannel> channels = new ArrayList<>();
        for (ChannelSettingObject c : channelData.getChannelSettings()) {
            if (c.getType() == type) {
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


    public IRole getXPDeniedRole() {
        return object.getRoleByID(config.getMutedRoleID());
    }

    public IRole getTopTenRole() {
        return object.getRoleByID(config.topTenRoleID);
    }
}
