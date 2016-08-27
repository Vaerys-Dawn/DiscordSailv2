package Listeners;

import Annotations.AliasAnnotation;
import Annotations.CommandAnnotation;
import Commands.Commands;
import Main.Constants;
import Main.FileHandler;
import Main.Globals;
import Main.Utility;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        } catch (DiscordException e) {
            e.printStackTrace();
        } catch (RateLimitException e) {
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
        IMessage message = event.getMessage();
        IGuild guild = message.getGuild();
        IChannel channel = message.getChannel();
        IUser author = message.getAuthor();
        String messageLC = message.toString().toLowerCase();
        String args = "";
        String command = "";
        String guildID = guild.getID();
        boolean isBeta = false;

        for (IRole r : author.getRolesForGuild(guild)) {
            if (r.getName().equalsIgnoreCase("Sail Beta Tester")) {
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
            Commands commands = new Commands(args, message);
            if (command.equalsIgnoreCase(Constants.COMMAND_PREFIX + "Echo")) {
                Utility.sendMessage(args, channel);
            }

            //
            if (messageLC.startsWith(Constants.COMMAND_PREFIX.toLowerCase())) {
                Method[] methods = Commands.class.getMethods();
                for (Method m : methods) {
                    if (m.isAnnotationPresent(CommandAnnotation.class)) {
                        CommandAnnotation commandAnno = m.getAnnotation(CommandAnnotation.class);
                        List<String> aliases = new ArrayList<>();
                        if (m.isAnnotationPresent(AliasAnnotation.class)) {
                            AliasAnnotation aliasAnno = m.getAnnotation(AliasAnnotation.class);
                            aliases = new ArrayList<>(Arrays.asList(aliasAnno.alias()));
                        }
                        aliases.add(commandAnno.name());
                        for (String s : aliases) {
                            if ((Constants.COMMAND_PREFIX + s).equalsIgnoreCase(command)) {
                                //init files
                                if (commandAnno.requiresArgs() && args.equals("")){
                                    Utility.sendMessage("Commands is missing Arguments: \n"+ Utility.getCommandInfo(commandAnno),channel);
                                    return;
                                }
                                if (commandAnno.type().equals(Constants.TYPE_SERVERS) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                                    commands.loadServers();
                                }
                                if (commandAnno.type().equals(Constants.TYPE_ROLE_SELECT) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                                    commands.loadCharacters();
                                }
                                if (commandAnno.type().equals(Constants.TYPE_CC) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                                    commands.loadCC();
                                }
                                GuildConfig guildConfig = (GuildConfig) handler.readfromJson(Utility.getFilePath(guildID,Constants.FILE_GUILD_CONFIG),GuildConfig.class);
                                if (commandAnno.doLogging() && guildConfig.doLogging){
                                    if (!guildConfig.getChannelTypeID(Constants.CHANNEL_LOGGING).equals("")){
                                        channel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_LOGGING));
                                        Utility.sendMessage("**"+author.getDisplayName(guild) + "** Has Used Command `" + command +"` with args: `" + args+ "`.",channel);
                                    }
                                }
                                handleCommand(m, message, commands);
                                commands.flushFiles();
                            }
                        }
                    }
                }
            }

            if (messageLC.startsWith(Constants.CC_PREFIX.toLowerCase())) {

                commands.flushFiles();
            }

        }

    }

    private void handleCommand(Method m, IMessage message, Commands commands) {
        boolean channelCorrect;
        boolean permsCorrct;
        IGuild guild = message.getGuild();
        GuildConfig guildConfig = (GuildConfig) handler.readfromJson(Utility.getFilePath(guild.getID(), Constants.FILE_GUILD_CONFIG), GuildConfig.class);
        IChannel channel = message.getChannel();
        IUser author = message.getAuthor();
        CommandAnnotation commandAnno = m.getAnnotation(CommandAnnotation.class);

        //Channel Validation
        if (!commandAnno.channel().equals(Constants.CHANNEL_ANY)) {
            if (channel.getID().equals(guildConfig.getChannelTypeID(commandAnno.channel())) || guildConfig.getChannelTypeID(commandAnno.channel()).equals("")) {
                channelCorrect = true;
            } else channelCorrect = false;
        } else channelCorrect = true;

        //Permission Validation
        if (channelCorrect) {
            if (!Arrays.equals(commandAnno.perms(), new Permissions[]{Permissions.SEND_MESSAGES})) {
                Permissions[] compiledPerms = new Permissions[commandAnno.perms().length];
                int permsIndex = 0;
                for (Permissions aP : commandAnno.perms()) {
                    for (IRole r : author.getRolesForGuild(guild)) {
                        for (Permissions p : r.getPermissions()) {
                            if (aP.equals(p)) {
                                compiledPerms[permsIndex] = p;
                            }
                        }
                    }
                    permsIndex++;
                }
                if (Arrays.equals(compiledPerms, commandAnno.perms())) {
                    permsCorrct = true;
                } else permsCorrct = false;
            } else permsCorrct = true;

            //message sending
            if (channelCorrect && permsCorrct) {
                try {
                    if (commandAnno.doResponseGeneral()){
                        channel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_GENERAL));
                    }
                    Utility.sendMessage((String) m.invoke(commands, new Object[]{}), channel);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else
                Utility.sendMessage("> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't do that.", channel);
        } else {
            Utility.sendMessage("> Command must be performed in the " + guild.getChannelByID(guildConfig.getChannelTypeID(commandAnno.channel())).mention() + " channel.", channel);
        }
    }

    @EventSubscriber
    public void onMentionEvent(MentionEvent event) {
        // TODO: 14/08/2016 set up do that mentioning the bot sends a priority message to the owner.

    }
}
