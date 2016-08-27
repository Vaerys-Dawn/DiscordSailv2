package Listeners;

import Handlers.DMHandler;
import Handlers.FileHandler;
import Main.*;
import Handlers.MessageHandler;

import POGOs.Characters;
import POGOs.CustomCommands;
import POGOs.GuildConfig;
import POGOs.Servers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.*;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.Scanner;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class AnnotationListener {

    final static Logger logger = LoggerFactory.getLogger(AnnotationListener.class);

    //File Handler
    FileHandler handler = new FileHandler();


    /**
     * Sets up the relevant files for each guild.
     */
    @EventSubscriber
    public void onGuildCreateEvent(GuildCreateEvent event) {
        IGuild guild = event.getGuild();
        String guildID = guild.getID();

        logger.info("Initialising Guild With ID: " + guildID);

        //Init Cooldowns
        Globals.addCooldown(guildID);

        //Create POGO templates
        GuildConfig guildConfig = new GuildConfig();
        Servers servers = new Servers();
        CustomCommands customCommands = new CustomCommands();
        Characters characters = new Characters();

        //Init Files
        handler.createDirectory(Utility.getDirectory(guildID));
        handler.initFile(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), guildConfig);
        handler.initFile(Utility.getFilePath(guildID, Constants.FILE_SERVERS), servers);
        handler.initFile(Utility.getFilePath(guildID, Constants.FILE_CUSTOM), customCommands);
        handler.initFile(Utility.getFilePath(guildID, Constants.FILE_CHARACTERS), characters);
        handler.initFile(Utility.getFilePath(guildID, Constants.FILE_INFO));

        //Load Guild Config for Init
        String pathGuildConfig = Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG);
        String pathCustomCommands = Utility.getFilePath(guildID, Constants.FILE_CUSTOM);
        guildConfig = (GuildConfig) handler.readfromJson(pathGuildConfig, GuildConfig.class);
        customCommands = (CustomCommands) handler.readfromJson(pathCustomCommands, CustomCommands.class);
        //Init Files
        guildConfig.setGuildName(event.getGuild().getName());
        handler.writetoJson(pathGuildConfig, guildConfig);
        customCommands.initCustomCommands();
        handler.writetoJson(pathCustomCommands, customCommands);

        guildConfig.updateVariables(guild);

        //handling Login Message
        if (guildConfig.doLoginMessage) {
            IChannel channel;
            if (!guildConfig.getChannelTypeID(Constants.CHANNEL_GENERAL).equals("")) {
                channel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_GENERAL));
            } else {
                channel = guild.getChannelByID(guildID);
            }
            Utility.sendMessage("> I have finished booting and I am now listening for commands...", channel);
        }
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        try {
//            final Image avatar = Image.forFile(new File("Icons/Sailvector.png"));
//            event.getClient().changeAvatar(avatar);
            final Status status = Status.game("IN DEVELOPMENT");
            event.getClient().changeStatus(status);
            if (!event.getClient().getApplicationName().equals(Globals.botName))
                event.getClient().changeUsername(Globals.botName);
            consoleInput(event);
        } catch (DiscordException | RateLimitException e) {
            e.printStackTrace();
        }
    }

    private void consoleInput(ReadyEvent event) {
        logger.info("Console input initiated.");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            IChannel channel = event.getClient().getChannelByID(Globals.consoleMessageCID);
            String message = scanner.nextLine();
            message = message.replaceAll("#Dawn#", event.getClient().getUserByID("153159020528533505").toString());
            message = message.replaceAll("teh", "the");
            message = message.replaceAll("Teh", "The");
            if (!message.equals("")) {
                Utility.sendMessage(message, channel);
            }
        }
    }

    @EventSubscriber
    public void onMessageRecivedEvent(MessageReceivedEvent event) {
        // TODO: 14/08/2016 ccs must also be handled here
        if (event.getMessage().getChannel().isPrivate()){
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
        boolean isBeta = false;

        for (IRole r : author.getRolesForGuild(guild)) {
            if (r.getName().equalsIgnoreCase("V2 Tester")) {
                isBeta = true;
            }
        }

        //Set Console Response Channel.
        if (author.getID().equals(Globals.creatorID)) {
            Globals.consoleMessageCID = channel.getID();
        }

        if (isBeta) {
            //Sets Up Command Arguments
            if (messageLC.startsWith(Constants.COMMAND_PREFIX.toLowerCase()) || messageLC.startsWith(Constants.CC_PREFIX.toLowerCase())) {
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

    }

    @EventSubscriber
    public void onMentionEvent(MentionEvent event) {
        // TODO: 14/08/2016 set up do that mentioning the bot sends a priority message to the owner.
        if (event.getMessage().getChannel().isPrivate()){
            new DMHandler(event.getMessage());
            return;
        }

    }
}
