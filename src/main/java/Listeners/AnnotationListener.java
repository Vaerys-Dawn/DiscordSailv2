package Listeners;

import Commands.Command;
import Commands.CommandObject;
import Handlers.DMHandler;
import Handlers.FileHandler;
import Handlers.MessageHandler;
import Main.Constants;
import Main.Globals;
import Main.TimedEvents;
import Main.Utility;
import Objects.GuildContentObject;
import Objects.SplitFirstObject;
import POGOs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.member.GuildMemberEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleUpdateEvent;
import sx.blah.discord.handle.impl.obj.Channel;
import sx.blah.discord.handle.obj.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 03/08/2016.
 */

@SuppressWarnings({"unused", "StringConcatenationInsideStringBufferAppend"})
public class AnnotationListener {

    final static Logger logger = LoggerFactory.getLogger(AnnotationListener.class);

    /**
     * Sets up the relevant files for each guild.
     */
    @EventSubscriber
    public void onGuildCreateEvent(GuildCreateEvent event) {
        IGuild guild = event.getGuild();
        String guildID = guild.getID();
        logger.info("Starting Guild init process for Guild with ID: " + guildID);

        //Init Cooldowns
        TimedEvents.addGuildCoolDown(guildID);

        //Create POGO templates
        GuildConfig guildConfig = new GuildConfig();
        Servers servers = new Servers();
        CustomCommands customCommands = new CustomCommands();
        Characters characters = new Characters();
        Competition competition = new Competition();

        //Init Files
        customCommands.initCustomCommands();
        guildConfig.initConfig();
        guildConfig.setProperlyInit(true);
        servers.setProperlyInit(true);
        customCommands.setProperlyInit(true);
        characters.setProperlyInit(true);
        competition.setProperlyInit(true);
        FileHandler.createDirectory(Utility.getDirectory(guildID));
        FileHandler.createDirectory(Utility.getDirectory(guildID, true));
        FileHandler.createDirectory(Utility.getGuildImageDir(guildID));
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), guildConfig);
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_SERVERS), servers);
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_CUSTOM), customCommands);
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_CHARACTERS), characters);
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_INFO));
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_COMPETITION), competition);

        Globals.initGuild(guildID);

        logger.info("Finished Initialising Guild With ID: " + guildID);
    }

    @EventSubscriber
    public void onGuildLeaveEvent(GuildLeaveEvent event) {
        Globals.unloadGuild(event.getGuild().getID());
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        Globals.isReady = true;
        event.getClient().changePlayingText(Globals.playing);
        Utility.updateUsername(Globals.botName);
    }

    // TODO: 15/02/2017 ---------------PIXEL XP-----------------
    // TODO: 16/01/2017 add xp system 20 xp per min max, decay of 150 xp per day, level up to be exponential unsure at what rate tho
    // TODO: 16/01/2017 roles removed when level threshold is no longer high enough, ability to set role to gain on level,
    // TODO: 16/01/2017 min word limit to gain xp default = 10 words,
    // TODO: 16/01/2017 create a list of users that have spoken in the current min, clear list at end of min
    // TODO: 16/01/2017 send all level up and rank messages to DMS
    // TODO: 16/01/2017 ability to toggle xp per channel
    // TODO: 16/01/2017 make level decay toggleable
    // TODO: 16/01/2017 make everything optional/ modifiable
    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        if (event.getMessage().getChannel().isPrivate()) {
            new DMHandler(event.getMessage());
            return;
        }
        IMessage message = event.getMessage();
        IGuild guild = message.getGuild();
        IChannel channel = message.getChannel();
        IUser author = message.getAuthor();
        String messageLC = message.toString().toLowerCase();
        String args = "";
        String command = "";
        while (!event.getClient().isReady()) ;
        GuildConfig guildConfig = Globals.getGuildContent(guild.getID()).getGuildConfig();

        //Set Console Response Channel.
        if (author.getID().equals(Globals.creatorID)) {
            Globals.consoleMessageCID = channel.getID();
        }

        if (messageLC.startsWith(guildConfig.getPrefixCommand().toLowerCase()) || messageLC.startsWith(guildConfig.getPrefixCC().toLowerCase())) {
            String[] splitMessage = message.toString().split(" ");
            command = splitMessage[0];
            StringBuilder getArgs = new StringBuilder();
            getArgs.append(message.toString());
            getArgs.delete(0, splitMessage[0].length() + 1);
            args = getArgs.toString();
        }

        //message and command handling
        CommandObject commandObject = new CommandObject(message);
        new MessageHandler(command, args, commandObject);
    }

    @EventSubscriber
    public void onMentionEvent(MentionEvent event) {
        IChannel channel = event.getMessage().getChannel();
        if (channel.isPrivate()) {
            new DMHandler(event.getMessage());
            return;
        }
        IGuild guild = event.getMessage().getGuild();
        IUser author = event.getMessage().getAuthor();
        String guildOwnerID = guild.getOwner().getID();
        String sailMention = event.getClient().getOurUser().mention(false);
        String sailMentionNick = event.getClient().getOurUser().mention(true);
        String prefix;
        String message = event.getMessage().toString();
        String[] splitMessage;

        if (event.getMessage().mentionsEveryone() || event.getMessage().mentionsHere()) {
            return;
        }
        if (author.getID().equals(Globals.getClient().getOurUser().getID())) {
            return;
        }

        /**This lets you set the guild's Prefix if you run "@Bot SetCommandPrefix [New Prefix]"*/
        if (author.getID().equals(guildOwnerID) || author.getID().equals(Globals.creatorID)) {
            SplitFirstObject mentionSplit = new SplitFirstObject(message);
            SplitFirstObject getArgs = new SplitFirstObject(mentionSplit.getRest());
            if (mentionSplit.getFirstWord() != null) {
                if (mentionSplit.getFirstWord().equals(sailMention) || mentionSplit.getFirstWord().equals(sailMentionNick)) {
                    String guildID = guild.getID();
                    GuildConfig guildConfig = Globals.getGuildContent(guildID).getGuildConfig();
                    if (getArgs.getRest() != null && !getArgs.getRest().contains(" ") && !getArgs.getRest().contains("\n")) {
                        prefix = getArgs.getRest();
                        if (getArgs.getFirstWord() != null && getArgs.getFirstWord().equalsIgnoreCase("setCommandPrefix")) {
                            Utility.sendMessage("> Updated Command Prefix to be : " + prefix, channel);
                            guildConfig.setPrefixCommand(prefix);
                        } else if (getArgs.getFirstWord() != null && getArgs.getFirstWord().equalsIgnoreCase("setCCPrefix")) {
                            Utility.sendMessage("> Updated CC Prefix to be : " + prefix, channel);
                            guildConfig.setPrefixCC(prefix);
                        }
                    } else {
                        Utility.sendMessage("> An error occurred trying to set Prefix\n" + Constants.PREFIX_INDENT + "Be sure that the prefix does not contain a newline or any spaces.", channel);
                    }
                }
            }
        }
    }

    @EventSubscriber
    public void onRoleUpdateEvent(RoleUpdateEvent event) {
        updateVariables(event.getGuild());
    }

    @EventSubscriber
    public void onRoleDeleteEvent(RoleDeleteEvent event) {
        updateVariables(event.getGuild());
    }

    @EventSubscriber
    public void onChannelUpdateEvent(sx.blah.discord.handle.impl.events.guild.channel.ChannelUpdateEvent event) {
        if (event.getChannel().isPrivate()) {
            return;
        }
        updateVariables(event.getNewChannel().getGuild());
    }

    @EventSubscriber
    public void onChannelDeleteEvent(ChannelDeleteEvent event) {
        if (event.getChannel().isPrivate()) {
            return;
        }
        updateVariables(event.getChannel().getGuild());
    }

    private void updateVariables(IGuild guild) {
        String guildID = guild.getID();
        GuildConfig guildConfig = Globals.getGuildContent(guildID).getGuildConfig();
        guildConfig.updateVariables(guild);
    }

    @EventSubscriber
    public void onReactionAddEvent(ReactionAddEvent event) {
        if (event.getChannel().isPrivate()) {
            if (event.getReaction().toString().equals("❌")) {
                if (event.getMessage().getAuthor().getID().equals(Globals.getClient().getOurUser().getID())) {
                    Utility.deleteMessage(event.getMessage());
                }
            }
            return;
        }
        if (Utility.canBypass(event.getUser(), event.getGuild())) {
            if (event.getReaction().toString().equals("❌")) {
                if (event.getMessage().getAuthor().getID().equals(Globals.getClient().getOurUser().getID())) {
                    Utility.deleteMessage(event.getMessage());
                }
            }
        }
    }

    @EventSubscriber
    public void onMessageDeleteEvent(MessageDeleteEvent event) {
        if (event.getChannel().isPrivate()) {
            return;
        }
        CommandObject command = new CommandObject(event.getMessage());
        String content;
        IChannel logging = command.client.getChannelByID(command.guildConfig.getChannelTypeID(Command.CHANNEL_SERVER_LOG));
        IUser ourUser = command.client.getOurUser();
        String infoID = command.guildConfig.getChannelTypeID(Command.CHANNEL_INFO);
        String serverLogID = command.guildConfig.getChannelTypeID(Command.CHANNEL_SERVER_LOG);
        String adminLogID = command.guildConfig.getChannelTypeID(Command.CHANNEL_ADMIN_LOG);
        if (event.getMessage() == null) {
            return;
        }
        if (serverLogID != null && serverLogID.equals(command.channelID) && ourUser.getID().equals(command.authorID)) {
            return;
        }
        if (infoID != null && serverLogID.equals(command.channelID) && ourUser.getID().equals(command.authorID)) {
            return;
        }
        if (adminLogID != null && serverLogID.equals(command.channelID) && ourUser.getID().equals(command.authorID)) {
            return;
        }
        if (logging != null && command.guildConfig.deleteLogging) {
            if (command.message.getContent() == null){
                return;
            }
            if (command.message.getContent().isEmpty()) {
                return;
            }
            int charLimit = 1800;
            if (command.message.getContent().length() > charLimit) {
                content = command.message.getContent().substring(0, 1800);
            } else {
                content = command.message.getContent();
            }
            if ((content.equals("`Loading...`") || content.equals("`Working...`")) && command.authorID.equals(command.client.getOurUser().getID())) {
                return;
            }
            Utility.sendMessage("> **@" + command.authorUserName + "'s** Message was deleted in channel: " + command.channel.mention() + " with contents:\n" + content, logging);
        }
    }

    @EventSubscriber
    public void onUserJoinEvent(UserJoinEvent event) {
        joinLeaveLogging(event, true);
    }

    @EventSubscriber
    public void onUserLeaveEvent(UserLeaveEvent event) {
        joinLeaveLogging(event, false);
    }

    public void joinLeaveLogging(GuildMemberEvent event, boolean joining) {
        IGuild guild = event.getGuild();
        GuildContentObject content = Globals.getGuildContent(guild.getID());
        if (content.getGuildConfig().joinLeaveLogging) {
            IChannel logging = Globals.getClient().getChannelByID(content.getGuildConfig().getChannelTypeID(Command.CHANNEL_SERVER_LOG));
            if (logging != null) {
                if (joining) {
                    Utility.sendMessage("> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** Has Joined the Server.", logging);
                } else {
                    Utility.sendMessage("> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** Has Left the Server.", logging);
                }
            }
        }
    }

    @EventSubscriber
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        if (event.getNewMessage().getContent() == null || event.getOldMessage().getContent() == null){
            return;
        }
        if (event.getNewMessage().getContent().isEmpty() || event.getOldMessage().getContent().isEmpty()){
            return;
        }
        if (event.getNewMessage().getContent().equals(event.getOldMessage().getContent())) {
            return;
        }
        if (event.getChannel().isPrivate()) {
            return;
        }
        CommandObject command = new CommandObject(event.getMessage());
        String content;
        IChannel logging = command.client.getChannelByID(command.guildConfig.getChannelTypeID(Command.CHANNEL_SERVER_LOG));
        IUser ourUser = command.client.getOurUser();
        String serverLogID = command.guildConfig.getChannelTypeID(Command.CHANNEL_SERVER_LOG);
        if (event.getMessage() == null) {
            return;
        }
        if (serverLogID != null && serverLogID.equals(command.channelID) && ourUser.getID().equals(command.authorID)) {
            return;
        }
        if (logging != null && command.guildConfig.editLogging) {
            if (command.message.getContent().isEmpty()) {
                return;
            }
            int charLimit = 1800;
            if (event.getOldMessage().getContent().length() > charLimit) {
                content = event.getOldMessage().getContent().substring(0, 1800);
            } else {
                content = event.getOldMessage().getContent();
            }
            Utility.sendMessage("> **@" + command.authorUserName + "'s** Message was Edited in channel: " + command.channel.mention() + " Old Contents:\n" + content, logging);
        }
    }

    @EventSubscriber
    public void onUserRoleUpdateEvent(UserRoleUpdateEvent event) {
        IGuild guild = event.getGuild();
        GuildContentObject content = Globals.getGuildContent(guild.getID());
        if (content.getGuildConfig().userRoleUpdatelogging) {
            IChannel logging = Globals.getClient().getChannelByID(content.getGuildConfig().getChannelTypeID(Command.CHANNEL_SERVER_LOG));
            if (logging != null) {
                ArrayList<String> oldRoles = new ArrayList<>();
                ArrayList<String> newRoles = new ArrayList<>();
                oldRoles.addAll(event.getOldRoles().stream().map(IRole::getName).collect(Collectors.toList()));
                newRoles.addAll(event.getNewRoles().stream().map(IRole::getName).collect(Collectors.toList()));
                for (int i = 0; i < oldRoles.size();i++){
                    if (oldRoles.get(i).equalsIgnoreCase("@everyone")){
                        oldRoles.remove(i);
                    }
                }
                for (int i = 0; i < newRoles.size();i++){
                    if (newRoles.get(i).equalsIgnoreCase("@everyone")){
                        newRoles.remove(i);
                    }
                }
                String prefix = "> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "'s** Role have been Updated.";

                Utility.sendMessage(prefix + "\nOld Roles: " + Utility.listFormatter(oldRoles, true) + "\nNew Roles: " + Utility.listFormatter(newRoles, true), logging);
            }
        }
    }
}
