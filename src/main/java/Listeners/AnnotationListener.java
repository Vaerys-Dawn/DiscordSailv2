package Listeners;

import Handlers.DMHandler;
import Handlers.FileHandler;
import Handlers.MessageHandler;
import Main.Constants;
import Main.Globals;
import Main.TimedEvents;
import Main.Utility;
import POGOs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class AnnotationListener {

    final static Logger logger = LoggerFactory.getLogger(AnnotationListener.class);

    /**
     * Sets up the relevant files for each guild.
     */
    @EventSubscriber
    public void onGuildCreateEvent(GuildCreateEvent event) {
        IGuild guild = event.getGuild();
        String guildID = guild.getID();
        logger.info("Starting Guild init proccess for Guild with ID: " + guildID);

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

        //Update Variables.
        //Guild Config
        String pathGuildConfig = Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG);
        guildConfig = (GuildConfig) FileHandler.readFromJson(pathGuildConfig, GuildConfig.class);
        guildConfig.updateVariables(guild);
        FileHandler.writeToJson(pathGuildConfig, guildConfig);

        logger.info("Finished Initialising Guild With ID: " + guildID);
        //handling Login Message
//        while (!Globals.isReady);
//        logger.info("Attempting to Send Login Message For guild with id: " + guildID);
//        if (guildConfig.doLoginMessage()) {
//            IChannel channel;
//            if (guildConfig.getChannelTypeID(Constants.CHANNEL_GENERAL) == null) {
//                channel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_GENERAL));
//            } else {
//                channel = guild.getChannelByID(guildID);
//            }
//            Utility.sendMessage("> I have finished booting and I am now listening for commands...", channel);
//        }
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        try {
            Globals.isReady = true;
            event.getClient().changeStatus(Status.game("Starbound"));
            if (!event.getClient().getOurUser().getName().equals(Globals.botName)) {
                event.getClient().changeUsername(Globals.botName);
            }

        } catch (DiscordException | RateLimitException e) {
            e.printStackTrace();
        }
    }


    @EventSubscriber
    public void onMessageRecivedEvent(MessageReceivedEvent event) {
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
        GuildConfig guildConfig = (GuildConfig) Utility.initFile(guild.getID(), Constants.FILE_GUILD_CONFIG, GuildConfig.class);

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
        new MessageHandler(command, args, message);
    }

    @EventSubscriber
    public void onMentionEvent(MentionEvent event) {
        IGuild guild = event.getMessage().getGuild();
        IUser author = event.getMessage().getAuthor();
        String guildOwnerID = guild.getOwner().getID();
        IChannel channel = event.getMessage().getChannel();
        if (channel.isPrivate()) {
            new DMHandler(event.getMessage());
            return;
        }
        if (event.getMessage().mentionsEveryone() || event.getMessage().mentionsHere()) {
            return;
        }
        if (author.getID().equals(Globals.getClient().getOurUser().getID())) {
            return;
        }
        if (author.getID().equals(guildOwnerID)){

        }
    }

    @EventSubscriber
    public void onRoleDeleteEvent(RoleDeleteEvent event) {
        IGuild guild = event.getGuild();
        String guildID = guild.getID();
        GuildConfig guildConfig = (GuildConfig) Utility.initFile(guildID, Constants.FILE_GUILD_CONFIG, GuildConfig.class);
        guildConfig.updateVariables(guild);
        Utility.flushFile(guildID, Constants.FILE_GUILD_CONFIG, guildConfig, guildConfig.isProperlyInit());
    }

    @EventSubscriber
    public void onRoleUpdateEvent(RoleUpdateEvent event) {
        IGuild guild = event.getGuild();
        String guildID = guild.getID();
        GuildConfig guildConfig = (GuildConfig) Utility.initFile(guildID, Constants.FILE_GUILD_CONFIG, GuildConfig.class);
        guildConfig.updateVariables(guild);
        Utility.flushFile(guildID, Constants.FILE_GUILD_CONFIG, guildConfig, guildConfig.isProperlyInit());
    }
}
