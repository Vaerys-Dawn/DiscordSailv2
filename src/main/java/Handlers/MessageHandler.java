package Handlers;

import Annotations.AliasAnnotation;
import Annotations.CommandAnnotation;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.BlackListedPhrase;
import Objects.CCommand;
import POGOs.Characters;
import POGOs.CustomCommands;
import POGOs.GuildConfig;
import POGOs.Servers;
import org.apache.commons.lang3.text.WordUtils;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class MessageHandler {

    private IMessage message;
    private IGuild guild;
    private IChannel channel;
    private IUser author;
    private String guildID;
    private String command;
    private String args;

    private boolean loadedConfig = false;
    private boolean loadedCC = false;
    private boolean loadedServers = false;
    private boolean loadedCharacters = false;

    private GuildConfig guildConfig = new GuildConfig();
    private CustomCommands customCommands = new CustomCommands();
    private Servers servers = new Servers();
    private Characters characters = new Characters();

    private FileHandler handler = new FileHandler();

    public MessageHandler(String command, String args, IMessage message) {
        this.command = command;
        this.args = args;
        this.message = message;
        guild = message.getGuild();
        channel = message.getChannel();
        author = message.getAuthor();
        guildID = guild.getID();
        loadConfig();
        checkBlacklist();
        if (author.isBot()){
            return;
        }
        if (command.toLowerCase().startsWith(Constants.COMMAND_PREFIX.toLowerCase())) {
            handleCommand();
        }
        if (command.toLowerCase().startsWith(Constants.CC_PREFIX.toLowerCase())){
            new CCHandler(command,args,message);
        }
    }

    //File handlers
    private void loadConfig() {
        guildConfig = (GuildConfig) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), GuildConfig.class);
        loadedConfig = true;
    }

    private void loadCC() {
        customCommands = (CustomCommands) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_CUSTOM), CustomCommands.class);
        loadedCC = true;
    }

    private void loadServers() {
        servers = (Servers) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_SERVERS), Servers.class);
        loadedServers = true;
    }

    private void loadCharacters() {
        characters = (Characters) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_CHARACTERS), Characters.class);
        loadedCharacters = true;
    }

    private void flushFiles() {
        if (loadedConfig) handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), guildConfig);
        if (loadedCC) handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_CUSTOM), customCommands);
        if (loadedServers) handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_SERVERS), servers);
        if (loadedCharacters)
            handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_CHARACTERS), characters);
    }

    //BlackListed Phrase Remover
    private void checkBlacklist() {
        if (guildConfig.doBlackListing) {
            for (BlackListedPhrase bLP : guildConfig.getBlacklist()) {
                if (message.toString().toLowerCase().contains(bLP.phrase.toLowerCase())) {
                    String response = bLP.getReason();
                    if (!guildConfig.getRoleToMention().getRoleID().equals(Constants.NULL_VARIABLE)) {
                        response = response.replaceAll("#mentionAdmin#", guild.getRoleByID(guildConfig.getRoleToMention().getRoleID()).mention());
                    } else {
                        response = response.replaceAll("#mentionAdmin#", "Admin");
                    }
                    if (Globals.getCooldowns(guildID).doAdminMention == 0) {
                        Utility.sendMessage(response, channel);
                        Globals.getCooldowns(guildID).doAdminMention = 60;
                    }
                    try {
                        message.delete();
                    } catch (MissingPermissionsException | RateLimitException e) {
                        e.printStackTrace();
                    } catch (DiscordException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //Command Handler
    private void handleCommand() {
        boolean channelCorrect;
        boolean permsCorrect;

        Method[] methods = this.getClass().getMethods();

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
                        if (commandAnno.requiresArgs() && args.equals("")) {
                            Utility.sendMessage("MessageHandler is missing Arguments: \n" + Utility.getCommandInfo(commandAnno), channel);
                            return;
                        }
                        if (commandAnno.type().equals(Constants.TYPE_SERVERS) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                            loadServers();
                        }
                        if (commandAnno.type().equals(Constants.TYPE_ROLE_SELECT) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                            loadCharacters();
                        }
                        if (commandAnno.type().equals(Constants.TYPE_CC) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                            loadCC();
                        }

                        //Logging Handling
                        if (commandAnno.doLogging() && guildConfig.doLogging) {
                            if (!guildConfig.getChannelTypeID(Constants.CHANNEL_LOGGING).equals(Constants.NULL_VARIABLE)) {
                                IChannel loggingChannel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_LOGGING));
                                StringBuilder builder = new StringBuilder();
                                builder.append("**" + author.getDisplayName(guild) + "** Has Used Command `" + command + "`");
                                if (!commandAnno.usage().equals(Constants.NULL_VARIABLE)) {
                                    builder.append(" with args: `" + args + "`");
                                }
                                builder.append(".");
                                Utility.sendMessage(builder.toString(), loggingChannel);
                            }
                        }

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
                                    permsCorrect = true;
                                } else permsCorrect = false;
                            } else permsCorrect = true;

                            //message sending
                            if (channelCorrect && permsCorrect) {
                                try {
                                    if (commandAnno.doResponseGeneral()) {
                                        channel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_GENERAL));
                                    }
                                    Utility.sendMessage((String) m.invoke(this), channel);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            } else
                                Utility.sendMessage("> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't do that.", channel);
                        } else {
                            Utility.sendMessage("> Command must be performed in the " + guild.getChannelByID(guildConfig.getChannelTypeID(commandAnno.channel())).mention() + " channel.", channel);
                        }
                        flushFiles();
                    }
                }
            }
        }

    }


    //---------Beginning of commands-------------

    //help commands

    //General commands

    @CommandAnnotation(name = "Hello", description = "Says Hello.",type = Constants.TYPE_GENERAL)
    public String hello() {
        return "> Hello " + author.getDisplayName(guild) + ".";
    }

    @CommandAnnotation(name = "Test", description = "Tests things", doLogging = true,type = Constants.TYPE_GENERAL)
    public String test() {
        return "> Tested";
    }

    //admin commands

    @CommandAnnotation(
            name = "ToggleLogging", description = "Toggles logging within the logging channel.",
            perms = {Permissions.MANAGE_CHANNELS}, type = Constants.TYPE_ADMIN, doLogging = true)
    public String toggleLogging() {
        guildConfig.toggleLogging();
        return "> Logging Toggled.";
    }

    @AliasAnnotation(alias = {"SetupChannel"})
    @CommandAnnotation(
            name = "ChannelHere", description = "Sets the current channel as the **General** channel.", usage = "[General, Servers, Admin, RaceSelect, Logging]",
            perms = {Permissions.MANAGE_CHANNELS}, type = Constants.TYPE_ADMIN, doLogging = true, requiresArgs = true)
    public String channelHere() {
        boolean channelUpdated = false;
        if (args.equalsIgnoreCase(Constants.CHANNEL_GENERAL)) {
            guildConfig.setUpChannel(Constants.CHANNEL_GENERAL, channel.getID());
            channelUpdated = true;
        } else if (args.equalsIgnoreCase(Constants.CHANNEL_SERVERS)) {
            guildConfig.setUpChannel(Constants.CHANNEL_SERVERS, channel.getID());
            channelUpdated = true;
        } else if (args.equalsIgnoreCase(Constants.CHANNEL_ADMIN)) {
            guildConfig.setUpChannel(Constants.CHANNEL_ADMIN, channel.getID());
            channelUpdated = true;
        } else if (args.equalsIgnoreCase(Constants.CHANNEL_RACE_SELECT)) {
            guildConfig.setUpChannel(Constants.CHANNEL_RACE_SELECT, channel.getID());
            channelUpdated = true;
        } else if (args.equalsIgnoreCase(Constants.CHANNEL_LOGGING)) {
            guildConfig.setUpChannel(Constants.CHANNEL_LOGGING, channel.getID());
            channelUpdated = true;
        } else if (args.equalsIgnoreCase(Constants.CHANNEL_INFO)) {
            guildConfig.setUpChannel(Constants.CHANNEL_INFO, channel.getID());
            channelUpdated = true;
        }
        if (channelUpdated) {
            return "> This channel is now this server's **" + WordUtils.capitalizeFully(args) + "** channel.";
        } else {
            return "> Channel type with that name not found.";
        }
    }

    //race select commands

    //servers commands

    //character commands

    //Custom command commands

    @CommandAnnotation(name = "newCC",description = "Creates a Custom Command.",usage = "[Command Name] [Contentes]",
            requiresArgs = true,type = Constants.TYPE_CC)
    public String newCC(){
        String cCName = args.split(" ")[0];
        String Content = args.replaceFirst(cCName + " ","");
        return customCommands.addCommand(new CCommand(false,author.getID(),cCName,Content));
    }
}
