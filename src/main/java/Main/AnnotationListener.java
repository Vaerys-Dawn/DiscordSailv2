package Main;

import Commands.CommandObject;
import Commands.General.NewDailyMessage;
import Handlers.*;
import Interfaces.Command;
import Objects.GuildContentObject;
import Objects.SplitFirstObject;
import POGOs.*;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.member.GuildMemberEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.role.RoleUpdateEvent;
import sx.blah.discord.handle.obj.*;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
        String guildID = guild.getStringID();
        logger.debug("Starting Initialisation process for Guild with ID: " + guildID);

        if (new File(Utility.getDirectory(guildID)).exists()) {
            PatchHandler.guildPatches(guild);
        }

        //Create POGO templates
        GuildConfig guildConfig = new GuildConfig();
        Servers servers = new Servers();
        CustomCommands customCommands = new CustomCommands();
        Characters characters = new Characters();
        Competition competition = new Competition();
        GuildUsers guildUsers = new GuildUsers();
        ChannelData channelData = new ChannelData();

        //Preps Objects for initial load
        guildConfig.initConfig();
        customCommands.initCustomCommands();

        //null prevention code. unsure if needed still.
        guildConfig.setProperlyInit(true);
        servers.setProperlyInit(true);
        customCommands.setProperlyInit(true);
        characters.setProperlyInit(true);
        competition.setProperlyInit(true);
        guildUsers.setProperlyInit(true);

        FileHandler.createDirectory(Utility.getDirectory(guildID));
        FileHandler.createDirectory(Utility.getDirectory(guildID, true));
        FileHandler.createDirectory(Utility.getGuildImageDir(guildID));

        //initial load of all files, creates files if they don't already exist
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), guildConfig);
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_SERVERS), servers);
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_CUSTOM), customCommands);
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_CHARACTERS), characters);
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_INFO));
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_COMPETITION), competition);
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_GUILD_USERS), guildUsers);
        FileHandler.initFile(Utility.getFilePath(guildID, Constants.FILE_CHANNEl_DATA), channelData);

        //loads all Files for the guild;
        guildConfig = (GuildConfig) Utility.initFile(guildID, Constants.FILE_GUILD_CONFIG, GuildConfig.class);
        customCommands = (CustomCommands) Utility.initFile(guildID, Constants.FILE_CUSTOM, CustomCommands.class);
        servers = (Servers) Utility.initFile(guildID, Constants.FILE_SERVERS, Servers.class);
        characters = (Characters) Utility.initFile(guildID, Constants.FILE_CHARACTERS, Characters.class);
        competition = (Competition) Utility.initFile(guildID, Constants.FILE_COMPETITION, Competition.class);
        guildUsers = (GuildUsers) Utility.initFile(guildID, Constants.FILE_GUILD_USERS, GuildUsers.class);
        channelData = (ChannelData) Utility.initFile(guildID, Constants.FILE_CHANNEl_DATA, ChannelData.class);
        //sends objects to globals
        Globals.initGuild(guildID, guildConfig, servers, customCommands, characters, competition, guildUsers, channelData);
        updateVariables(guild);
        logger.info("Finished Initialising Guild With ID: " + guildID);

        NewDailyMessage.checkIsEnabled(Globals.client.getChannelByID(Globals.queueChannelID) != null);
    }

    @EventSubscriber
    public void onGuildLeaveEvent(GuildLeaveEvent event) {
        Globals.unloadGuild(event.getGuild().getStringID());
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        Globals.isReady = true;
        event.getClient().changePlayingText(Globals.playing);
        Utility.updateUsername(Globals.botName);
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
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
        if (Globals.getGuildContent(guild.getStringID()) == null) return;
        GuildConfig guildConfig = Globals.getGuildContent(guild.getStringID()).getGuildConfig();


        //Set Console Response Channel.
        if (author.getStringID().equals(Globals.creatorID)) {
            Globals.consoleMessageCID = channel.getStringID();
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
        String guildOwnerID = guild.getOwner().getStringID();
        String sailMention = event.getClient().getOurUser().mention(false);
        String sailMentionNick = event.getClient().getOurUser().mention(true);
        String prefix;
        String message = event.getMessage().toString();
        String[] splitMessage;

        if (event.getMessage().mentionsEveryone() || event.getMessage().mentionsHere()) {
            return;
        }
        if (author.getStringID().equals(Globals.getClient().getOurUser().getStringID())) {
            return;
        }

        /**This lets you set the guild's Prefix if you run "@Bot SetCommandPrefix [New Prefix]"*/
        if (author.getStringID().equals(guildOwnerID) || author.getStringID().equals(Globals.creatorID)) {
            SplitFirstObject mentionSplit = new SplitFirstObject(message);
            SplitFirstObject getArgs = new SplitFirstObject(mentionSplit.getRest());
            if (mentionSplit.getFirstWord() != null) {
                if (mentionSplit.getFirstWord().equals(sailMention) || mentionSplit.getFirstWord().equals(sailMentionNick)) {
                    String guildID = guild.getStringID();
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
    public void onChannelUpdateEvent(ChannelUpdateEvent event) {
        if (event.getChannel().isPrivate()) {
            return;
        }
        updateVariables(event.getNewChannel().getGuild());

        GuildContentObject content = Globals.getGuildContent(event.getGuild().getStringID());
        if (content.getGuildConfig().channelLogging) {
            if (!event.getOldChannel().getName().equalsIgnoreCase(event.getNewChannel().getName())) {
                String log = "> Channel " + event.getNewChannel().mention() + "'s name was changed.\nOld name : #" + event.getOldChannel().getName() + ".";
                Utility.sendLog(log, content.getGuildConfig(), false);
            } else if ((event.getOldChannel().getTopic() == null || event.getOldChannel().getTopic().isEmpty()) && (event.getNewChannel().getTopic() == null || event.getNewChannel().getTopic().isEmpty())) {
                //do nothing
            } else if ((event.getOldChannel().getTopic() == null || event.getOldChannel().getTopic().isEmpty()) && (event.getNewChannel().getTopic() != null || !event.getNewChannel().getTopic().isEmpty())) {
                StringBuilder log = new StringBuilder("> Channel " + event.getNewChannel().mention() + "'s Channel topic was added.\n");
                log.append("**New Topic**: " + event.getNewChannel().getTopic());
                Utility.sendLog(log.toString(), content.getGuildConfig(), false);
            } else if ((event.getOldChannel().getTopic() != null || !event.getOldChannel().getTopic().isEmpty()) && (event.getNewChannel().getTopic() == null || event.getNewChannel().getTopic().isEmpty())) {
                StringBuilder log = new StringBuilder("> Channel " + event.getNewChannel().mention() + "'s Channel topic was removed.\n");
                log.append("**Old Topic**: " + event.getOldChannel().getTopic());
                Utility.sendLog(log.toString(), content.getGuildConfig(), false);
            } else if (!event.getOldChannel().getTopic().equalsIgnoreCase(event.getNewChannel().getTopic())) {
                StringBuilder log = new StringBuilder("> Channel " + event.getNewChannel().mention() + "'s Channel topic was changed.");
                log.append("\n**Old Topic**: " + event.getOldChannel().getTopic());
                log.append("\n**New Topic**: " + event.getNewChannel().getTopic());
                Utility.sendLog(log.toString(), content.getGuildConfig(), false);
            }
        }
    }

    @EventSubscriber
    public void onChannelDeleteEvent(ChannelDeleteEvent event) {
        if (event.getChannel().isPrivate()) {
            return;
        }
        GuildContentObject content = Globals.getGuildContent(event.getGuild().getStringID());
        if (content.getGuildConfig().channelLogging) {
            String log = "> Channel #" + event.getChannel().getName() + " was deleted.";
            Utility.sendLog(log, content.getGuildConfig(), false);
        }
        updateVariables(event.getChannel().getGuild());
    }

    @EventSubscriber
    public void onChannelCreateEvent(ChannelCreateEvent event) {
        GuildContentObject content = Globals.getGuildContent(event.getGuild().getStringID());
        if (content.getGuildConfig().channelLogging) {
            String log = "> Channel " + event.getChannel().mention() + " was created.";
            Utility.sendLog(log, content.getGuildConfig(), false);
        }
    }

    private void updateVariables(IGuild guild) {
        String guildID = guild.getStringID();
        GuildConfig guildConfig = Globals.getGuildContent(guildID).getGuildConfig();
        guildConfig.updateVariables(guild);
    }

    @EventSubscriber
    public void onReactionAddEvent(ReactionAddEvent event) {
        Emoji x = EmojiManager.getForAlias("x");
        Emoji pin = EmojiManager.getForAlias("pushpin");
        Emoji thumbsUp = EmojiManager.getForAlias("thumbsup");
        Emoji thumbsDown = EmojiManager.getForAlias("thumbsdown");
        if (event.getReaction().isCustomEmoji()) {
            return;
        }
        if (!event.getChannel().isPrivate()) {
            CommandObject object = new CommandObject(event.getMessage());
            if (object.guildConfig.artPinning) {
                if (event.getReaction().getUnicodeEmoji().equals(pin)) {
                    new ArtHandler(object.setAuthor(event.getUser()));
                }
            }
            if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, event.getUser(), event.getGuild())) {
                if (event.getReaction().getUnicodeEmoji().equals(x)) {
                    if (event.getMessage().getAuthor().getStringID().equals(Globals.getClient().getOurUser().getStringID())) {
                        Utility.deleteMessage(event.getMessage());
                        return;
                    }
                }
            }
            if (event.getChannel().getLongID() == Globals.queueChannelID) {
                if (!event.getReaction().isCustomEmoji()) {
                    if (event.getReaction().getUnicodeEmoji().equals(thumbsUp) || event.getReaction().getUnicodeEmoji().equals(thumbsDown)) {
                        IUser owner = Globals.getClient().getUserByID(Globals.creatorID);
                        if (event.getReaction().getUserReacted(owner)) {
                            QueueHandler.addedReaction(event.getMessage(), event.getReaction());
                        }
                    }
                }
            }
        } else {
            if (event.getReaction().getUnicodeEmoji().equals(x)) {
                if (event.getMessage().getAuthor().getStringID().equals(Globals.getClient().getOurUser().getStringID())) {
                    Utility.deleteMessage(event.getMessage());
                    return;
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
        IUser ourUser = command.client.getOurUser();

        List<String> infoID = command.guildConfig.getChannelIDsByType(Command.CHANNEL_INFO);
        List<String> serverLogID = command.guildConfig.getChannelIDsByType(Command.CHANNEL_SERVER_LOG);
        List<String> adminLogID = command.guildConfig.getChannelIDsByType(Command.CHANNEL_ADMIN_LOG);
        List<String> dontLog = command.guildConfig.getChannelIDsByType(Command.CHANNEL_DONT_LOG);
        if (dontLog != null) {
            for (String id : dontLog) {
                if (command.channelSID.equals(id)) {
                    return;
                }
            }
        }
        if (command.guildConfig.dontLogBot && command.author.isBot()) {
            return;
        }
        if (event.getMessage() == null) {
            return;
        }
        if (serverLogID != null && serverLogID.get(0).equals(command.channelSID) && ourUser.getStringID().equals(command.authorSID)) {
            return;
        }
        if (infoID != null && infoID.get(0).equals(command.channelSID) && ourUser.getStringID().equals(command.authorSID)) {
            return;
        }
        if (adminLogID != null && adminLogID.get(0).equals(command.channelSID) && ourUser.getStringID().equals(command.authorSID)) {
            return;
        }
        if (command.guildConfig.deleteLogging) {
            if (command.message.getContent() == null) {
                return;
            }
            if (command.message.getContent().isEmpty()) {
                return;
            }
            int charLimit = 1800;
            if (command.message.getContent().length() > charLimit) {
                content = Utility.unFormatMentions(command.message).substring(0, 1800);
            } else {
                content = Utility.unFormatMentions(command.message);
            }
            if ((content.equals("`Loading...`") || content.equals("`Working...`")) && command.authorSID.equals(command.client.getOurUser().getStringID())) {
                return;
            }
            long difference = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - event.getMessage().getTimestamp().atZone(ZoneOffset.UTC).toEpochSecond();
            String formatted;
            if (command.guildConfig.useTimeStamps) {
                formatted = "at `" + Utility.formatTimestamp(event.getMessage().getTimestamp().atZone(ZoneOffset.UTC)) + " - UTC`";
            } else {
                formatted = "from " + Utility.formatTimeDifference(difference);
            }
            Utility.sendLog("> **@" + command.authorUserName + "'s** Message " + formatted + " was **Deleted** in channel: " + command.channel.mention() + " with contents:\n" + content, command.guildConfig, false);
        }
    }

    @EventSubscriber
    public void onUserJoinEvent(UserJoinEvent event) {
        GuildContentObject content = Globals.getGuildContent(event.getGuild().getStringID());
        if (content.getGuildConfig().joinsServerMessages) {
            String message = content.getGuildConfig().getJoinMessage();
            message = message.replace("<server>", event.getGuild().getName());
            message = message.replace("<user>", event.getUser().getName());
            Utility.sendDM(message, event.getUser().getStringID());
        }
        joinLeaveLogging(event, true);
    }

    @EventSubscriber
    public void onUserLeaveEvent(UserLeaveEvent event) {
        joinLeaveLogging(event, false);
    }

    public void joinLeaveLogging(GuildMemberEvent event, boolean joining) {
        IGuild guild = event.getGuild();
        GuildContentObject content = Globals.getGuildContent(guild.getStringID());
        if (content.getGuildConfig().joinLeaveLogging) {
            if (joining) {
                Utility.sendLog("> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** has **Joined** the server.", content.getGuildConfig(), false);
            } else {
                Utility.sendLog("> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "** has **Left** the server.", content.getGuildConfig(), false);
            }
        }
    }

    @EventSubscriber
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        if (event.getNewMessage().getContent() == null || event.getOldMessage().getContent() == null) {
            return;
        }
        if (event.getNewMessage().getContent().isEmpty() || event.getOldMessage().getContent().isEmpty()) {
            return;
        }
        if (event.getNewMessage().getContent().equals(event.getOldMessage().getContent())) {
            return;
        }
        if (event.getChannel().isPrivate()) {
            return;
        }
        CommandObject command = new CommandObject(event.getMessage());
        IUser ourUser = command.client.getOurUser();
        List<String> logID = command.guildConfig.getChannelIDsByType(Command.CHANNEL_SERVER_LOG);
        List<String> dontLog = command.guildConfig.getChannelIDsByType(Command.CHANNEL_DONT_LOG);
        if (dontLog != null) {
            for (String id : dontLog) {
                if (command.channelSID.equals(id)) {
                    return;
                }
            }
        }
        if (logID != null) {
            String serverLogID = logID.get(0);

            if (command.guildConfig.dontLogBot && command.author.isBot()) {
                return;
            }
            if (event.getMessage() == null) {
                return;
            }
            if (serverLogID != null && serverLogID.equals(command.channelSID) && ourUser.getStringID().equals(command.authorSID)) {
                return;
            }
            if (command.guildConfig.editLogging) {
                //formats how long ago this was.
                String formatted;
                String oldContent;
                String newContent;
                StringBuilder response = new StringBuilder();
                ZonedDateTime oldMessageTime = event.getOldMessage().getTimestamp().atZone(ZoneOffset.UTC);
                ZonedDateTime newMessageTime = event.getNewMessage().getEditedTimestamp().get().atZone(ZoneOffset.UTC);
                int charLimit;
                long difference = newMessageTime.toEpochSecond() - oldMessageTime.toEpochSecond();
                if (command.guildConfig.useTimeStamps) {
                    formatted = "at `" + Utility.formatTimestamp(oldMessageTime) + " - UTC`";
                } else {
                    formatted = "from " + Utility.formatTimeDifference(difference);
                }
                response.append("> **@" + command.authorUserName + "'s** Message " + formatted + " was **Edited** in channel: " + command.channel.mention() + ". ");
                //remove excess text that would cause a max char limit error.
                if (command.message.getContent().isEmpty()) {
                    return;
                }
                if (command.guildConfig.extendEditLog) {
                    charLimit = 900;
                } else {
                    charLimit = 1800;
                }
                if (event.getOldMessage().getContent().length() > charLimit) {
                    oldContent = Utility.unFormatMentions(event.getOldMessage()).substring(0, charLimit) + "...";
                } else {
                    oldContent = Utility.unFormatMentions(event.getOldMessage());
                }
                if (event.getNewMessage().getContent().length() > charLimit) {
                    newContent = Utility.unFormatMentions(event.getNewMessage()).substring(0, charLimit) + "...";
                } else {
                    newContent = Utility.unFormatMentions(event.getNewMessage());
                }
                response.append("**\nMessage's Old Contents:**\n" + oldContent);
                if (command.guildConfig.extendEditLog) {
                    response.append("\n**Message's New Contents:**\n" + newContent);
                }
                Utility.sendLog(response.toString(), command.guildConfig, false);
            }
        }
    }

    @EventSubscriber
    public void onUserRoleUpdateEvent(UserRoleUpdateEvent event) {
        IGuild guild = event.getGuild();
        GuildContentObject content = Globals.getGuildContent(guild.getStringID());
        if (content.getGuildConfig().userRoleLogging) {
            ArrayList<String> oldRoles = new ArrayList<>();
            ArrayList<String> newRoles = new ArrayList<>();
            oldRoles.addAll(event.getOldRoles().stream().filter(r -> !r.isEveryoneRole()).map(IRole::getName).collect(Collectors.toList()));
            newRoles.addAll(event.getNewRoles().stream().filter(r -> !r.isEveryoneRole()).map(IRole::getName).collect(Collectors.toList()));
            StringBuilder oldRoleList = new StringBuilder();
            StringBuilder newRoleList = new StringBuilder();
            for (String r : oldRoles) {
                oldRoleList.append(r + ", ");
            }
            for (String r : newRoles) {
                newRoleList.append(r + ", ");
            }
            logger.debug("Old Roles:");
            logger.debug(oldRoleList.toString());
            logger.debug("New Roles:");
            logger.debug(newRoleList.toString());
            String prefix = "> **@" + event.getUser().getName() + "#" + event.getUser().getDiscriminator() + "'s** Role have been Updated.";
            Utility.sendLog(prefix + "\nOld Roles: " + Utility.listFormatter(oldRoles, true) + "\nNew Roles: " + Utility.listFormatter(newRoles, true), content.getGuildConfig(), false);
        }
    }

    @EventSubscriber
    public void onMessagePinEvent(MessagePinEvent event) {
        // do stuff
    }
}
