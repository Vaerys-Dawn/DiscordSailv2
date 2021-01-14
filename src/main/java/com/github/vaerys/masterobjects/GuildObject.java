package com.github.vaerys.masterobjects;

import com.github.vaerys.commands.CommandList;
import com.github.vaerys.commands.general.NewDailyMessage;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.FilePaths;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.guildtoggles.ToggleList;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.objects.adminlevel.ChannelSettingObject;
import com.github.vaerys.objects.adminlevel.UserRateObject;
import com.github.vaerys.objects.utils.GuildLogObject;
import com.github.vaerys.objects.utils.LogObject;
import com.github.vaerys.pogos.*;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.FileFactory;
import com.github.vaerys.templates.GlobalFile;
import com.github.vaerys.templates.GuildToggle;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Wrapper for the Guild API object. Also contains all bot data related to said Guild.
 */
public class GuildObject {

    private final static Logger logger = LoggerFactory.getLogger(GuildObject.class);

    public ClientObject client;
    public long longID;
    public GuildConfig config;
    public CustomCommands customCommands;
    public Servers servers;
    public Characters characters;
    public Competition competition;
    public GuildUsers users;
    public ChannelData channelData;
    public GuildLog guildLog;
    public AdminCCs adminCCs;
    public List<GlobalFile> guildFiles;
    public List<Command> commands;
    private List<GuildToggle> toggles;
    public List<ChannelSetting> channelSettings;
    public List<SAILType> commandTypes;
    private Guild object;
    private List<UserRateObject> rateUsers = new ArrayList<>();
    private List<Long> spokenUsers = new ArrayList<>();
    private List<GuildToggle> toRemove = new ArrayList<>();

    public GuildObject(Guild object) {
        this.object = object;
        this.longID = object.getIdLong();
        this.config = FileFactory.create(longID, FilePaths.GUILD_CONFIG, GuildConfig.class);
        this.customCommands = FileFactory.create(longID, FilePaths.CUSTOM_COMMANDS, CustomCommands.class);
        this.servers = FileFactory.create(longID, FilePaths.SERVERS, Servers.class);
        this.characters = FileFactory.create(longID, FilePaths.CHARACTERS, Characters.class);
        this.competition = FileFactory.create(longID, FilePaths.COMPETITION, Competition.class);
        this.users = FileFactory.create(longID, FilePaths.GUILD_USERS, GuildUsers.class);
        this.channelData = FileFactory.create(longID, FilePaths.CHANNEL_DATA, ChannelData.class);
        this.guildLog = FileFactory.create(longID, FilePaths.GUILD_LOG, GuildLog.class);
        this.adminCCs = FileFactory.create(longID, FilePaths.ADMIN_CCS, AdminCCs.class);
        this.guildFiles = new ArrayList<GlobalFile>() {{
            add(config);
            add(customCommands);
            add(servers);
            add(characters);
            add(competition);
            add(users);
            add(channelData);
            add(guildLog);
            add(adminCCs);
        }};
        customCommands.initCustomCommands(get());
        this.client = Client.getClientObject();
        loadCommandData();
    }

    public GuildObject() {
        this.client = Client.getClientObject();
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
        this.commands = new ArrayList<>(CommandList.getCommands(true));
        this.object = null;
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

    public void sendDebugLog(User user, TextChannel channel, String type, String name, String contents) {
        GuildLogObject object = new GuildLogObject(user, channel, type, name, contents);
        String output = object.getOutput(this);
        if (object != null) {
            guildLog.addLog(object, longID);
        } else {
            Globals.addToLog(new LogObject(object, -1));
        }
        logger.trace(output);
    }

    public void loadCommandData() {
        this.commands = new ArrayList<>(CommandList.getCommands(false));
        this.toggles = new ArrayList<>(ToggleList.getAllToggles());
        this.channelSettings = new ArrayList<>(Globals.getChannelSettings());
        this.commandTypes = new ArrayList<>(Globals.getCommandTypes());
        checkToggles();
    }

    public Guild get() {
        return object;
    }

    private void checkToggles() {
        toRemove = new ArrayList<>();
        for (GuildToggle g : toggles) {
            if (!g.enabled(config)) {
                g.execute(this);
            }
        }

        for (GuildToggle g : toRemove) {
            ListIterator iterator = toggles.listIterator();
            while (iterator.hasNext()) {
                GuildToggle toggle = (GuildToggle) iterator.next();
                if (toggle.name() == g.name()) {
                    if (g.isModule()) {
                        logger.trace("Module: " + g.name() + " removed.");
                    } else {
                        logger.trace("Toggle: " + g.name() + " removed.");
                    }
                    iterator.remove();
                }
            }
        }
        TextChannel channel = client.get().getTextChannelById(Globals.queueChannelID);
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
     * Removes a messageChannel setting from the specified messageChannel
     *
     * @param channel
     */
    public void removeChannelSetting(ChannelSetting channel) {
        channelSettings.remove(ChannelSetting.FROM_DM);
        for (ChannelSetting s : channelSettings) {
            if (s == channel) {
                channelSettings.remove(s);
                logger.trace("Channel Setting: " + s.toString() + " removed.");
                break;
            }
        }
    }

    /**
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

    public List<UserRateObject> getRateUsers() {
        return rateUsers;
    }

    public List<Long> getSpokenUsers() {
        return spokenUsers;
    }

    public void forceClearRate() {
        rateUsers = new ArrayList<>();
    }

    public void resetRateLimit() {
        rateUsers.clear();
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
            allCommands.addAll(CommandList.getCreatorCommands());
        }
        return allCommands;
    }

    public TextChannel getChannelByID(long id) {
        return object.getTextChannelById(id);
    }

    public Member getUserByID(long id) {
        return object.getMemberById(id);
    }

    public Role getRoleById(long id) {
        return object.getRoleById(id);
    }

    public VoiceChannel getVoiceChannelByID(long id) {
        return object.getVoiceChannelById(id);
    }

    public Emote getEmojiById(long id) {
        return object.getEmoteById(id);
    }

    public long getOwnerID() {
        return object.getOwnerIdLong();
    }

    public Member getOwner() {
        return object.getOwner();
    }

    public List<Member> getUsers() {
        return object.getMembers();
    }

    public List<Emote> getEmojiByName(String name) {
        return object.getEmotesByName(name, true);
    }

    public void handleWelcome(CommandObject command) {
        if (config.initialMessage) {
            return;
        }
        if (command.guild.get() == null || command.guildChannel == null) {
            return;
        }
        TextChannel general = getChannelByType(ChannelSetting.GENERAL);
        if (general != null && command.guildChannel.longID != general.getIdLong()) {
            return;
        }
        if (!GuildHandler.testForPerms(command, Permission.MANAGE_SERVER)) return;
        Message message = command.guildChannel.sendMessage(Constants.getWelcomeMessage(command));
        if (message != null) {
            command.guild.config.initialMessage = true;
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(5 * 60 * 1000);
                    message.delete().complete();
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

    public TextChannel getChannelByType(ChannelSetting type) {
        List<TextChannel> channels = getChannelsByType(type);
        if (channels.size() != 0) {
            return channels.get(0);
        }
        return null;
    }

    public List<TextChannel> getChannelsByType(ChannelSetting type) {
        List<TextChannel> channels = new ArrayList<>();
        for (ChannelSettingObject c : channelData.getChannelSettings()) {
            if (c.getType() == type) {
                for (long s : c.getChannelIDs()) {
                    TextChannel channel = getChannelByID(s);
                    if (channel != null) {
                        channels.add(channel);
                    }
                }
            }
        }
        return channels;
    }

    public List<Role> getRewardRoles() {
        List<Role> roles = new LinkedList<>();
        config.getRewardRoles().forEach(rewardRoleObject -> roles.add(object.getRoleById(rewardRoleObject.getRoleID())));
        return roles;
    }

    public Role getMutedRole() {
        return object.getRoleById(config.getMutedRoleID());
    }

    public Role getXPDeniedRole() {
        return object.getRoleById(config.getMutedRoleID());
    }

    public Role getTopTenRole() {
        return object.getRoleById(config.topTenRoleID);
    }

    public List<Role> getCosmeticRoles() {
        return config.getCosmeticRoleIDs().stream().map(id -> object.getRoleById(id)).collect(Collectors.toList());
    }

    public List<Role> getModifierRoles() {
        return config.getModifierRoleIDs().stream().map(id -> object.getRoleById(id)).collect(Collectors.toList());
    }

    public Role getRuleCodeRole() {
        return object.getRoleById(config.ruleCodeRewardID);
    }

    public boolean channelHasSetting(ChannelSetting setting, long channelID) {
        ChannelSettingObject settings = channelData.getChannelSetting(setting);
        if (settings == null) return false;
        return settings.getChannelIDs().contains(channelID);
    }

    public boolean channelHasSetting(ChannelSetting setting, TextChannel channel) {
        return channelHasSetting(setting, channel.getIdLong());
    }

    public boolean channelHasSetting(ChannelSetting setting, ChannelObject channel) {
        return channelHasSetting(setting, channel.longID);
    }

    public Message fetchMessage(long l) {
        for (TextChannel c : object.getTextChannels()) {
            return c.retrieveMessageById(l).complete();
        }
        return null;
    }

    public Message getMessageByID(Long l) {
        return fetchMessage(l);
    }

    public UserRateObject rateLimit(CommandObject command) {
        UserRateObject rateObject = null;
        ListIterator iterator = rateUsers.listIterator();
        while (iterator.hasNext()) {
            UserRateObject r = (UserRateObject) iterator.next();
            if (r.getUserID() == command.user.longID) {
                rateObject = r;
                rateObject.addMessage(command);
            }
        }
        if (rateObject == null) {
            rateObject = new UserRateObject(command);
            rateUsers.add(rateObject);
        }
        return rateObject;
    }

    public List<GuildToggle> getEnabledSettings() {
        List<SAILType> settings = new LinkedList<>();
        for (GuildToggle toggle : toggles) {
            if (toggle.isModule() && toggle.enabled(config)) settings.addAll(toggle.settings);
        }
        return settings.stream().map(s -> ToggleList.getSetting(s)).collect(Collectors.toList());
    }

    public GuildToggle getSetting(SAILType type) {
        List<GuildToggle> settings = getEnabledSettings();
        for (GuildToggle setting : settings) {
            if (setting.name() == type) return setting;
        }
        return null;
    }

    public List<GuildToggle> getModules() {
        return toggles.stream().filter(t -> t.isModule() && !t.equals(SAILType.DEFAULT)).collect(Collectors.toList());
    }

    public List<GuildToggle> getSettings() {
        return toggles.stream().filter(t -> !t.isModule()).collect(Collectors.toList());
    }

    public List<GuildToggle> getToggles() {
        return toggles.stream().filter(t -> t.name() != SAILType.DEFAULT).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GuildObject) {
            return longID == ((GuildObject) obj).longID;
        } else {
            return super.equals(obj);
        }
    }
}
