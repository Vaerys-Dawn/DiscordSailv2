package Handlers;

import Annotations.AliasAnnotation;
import Annotations.CommandAnnotation;
import Annotations.ToggleAnnotation;
import Main.*;
import Objects.*;
import POGOs.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

// TODO: 02/09/2016 Add a Buncha Stuff

/**
 * This Class Handles all of the commands that the bot can run not incluting custom commands.
 */

/*
 * Annotation order:
 *
 * @AliasAnnotation(alias)
 * @CommandAnnotation(
 * name, description, usage,
 * type, channel, permissions, requiresArgs, doGeneralLogging, doResponseGeneral)
 */

@SuppressWarnings({"unused", "StringConcatenationInsideStringBufferAppend"})
public class MessageHandler {

    private IMessage message;
    private IGuild guild;
    private IChannel channel;
    private IUser author;
    private String guildID;
    private String command;
    private String args;
    private String noAllowed;

    private GuildConfig guildConfig = new GuildConfig();
    private CustomCommands customCommands = new CustomCommands();
    private Servers servers = new Servers();
    private Characters characters = new Characters();
    private Competition competition = new Competition();

    private FileHandler handler = new FileHandler();

    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public MessageHandler(String command, String args, IMessage message) {
        this.command = command;
        this.args = args;
        this.message = message;
        guild = message.getGuild();
        channel = message.getChannel();
        author = message.getAuthor();
        guildID = guild.getID();
        noAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't do that.";
        guildConfig = (GuildConfig) Utility.initFile(guildID, Constants.FILE_GUILD_CONFIG, GuildConfig.class);
        if (guildConfig == null) {
            logger.error("Guild config for guild with id : " + guildID + " returned null, sending data to Error files.");
            MessageErrorObject messageError = new MessageErrorObject(message);
            ZonedDateTime now = ZonedDateTime.now();
            String timeNow = now.getYear() + "-" + now.getMonth() + "-" + now.getDayOfMonth() + "_" + now.getHour() + "-" + now.getMinute() + "-" + now.getSecond() + "-" + now.getNano();
            FileHandler.writeToJson(Constants.DIRECTORY_ERROR + timeNow + "_MSG_" + guildID + "_" + ".json", messageError);
            FileHandler.writeToJson(Constants.DIRECTORY_ERROR + timeNow + "_GCF_" + guildID + "_" + ".json", guildConfig);
            return;
        }
        checkBlacklist();
        checkMentionCount();
        if (author.isBot()) {
            return;
        }
        if (command.toLowerCase().startsWith(Constants.PREFIX_COMMAND.toLowerCase())) {
            handleCommand();
        }
        if (command.toLowerCase().startsWith(Constants.PREFIX_CC.toLowerCase())) {
            new CCHandler(command, args, message);
        }
    }

    private void checkMentionCount() {
        if (Utility.testForPerms(new Permissions[]{Permissions.MENTION_EVERYONE}, author, guild)) {
            return;
        }
        if (guildConfig.doMaxMentions()) {
            if (message.getMentions().size() > 8) {
                Utility.deleteMessage(message);
                int i = 0;
                boolean offenderFound = false;
                for (OffenderObject o : guildConfig.getRepeatOffenders()) {
                    if (author.getID().equals(o.getID())) {
                        guildConfig.addOffence(o.getID());
                        offenderFound = true;
                        i++;
                        if (o.getCount() > Globals.maxWarnings) {
                            Utility.roleManagement(author, guild, guildConfig.getMutedRole().getRoleID(), true);
                            Utility.sendMessage("> " + author.mention() + " Has been Muted for repeat offences of spamming Mentions.", channel);
                        }
                    }
                }
                if (!offenderFound) {
                    guildConfig.addOffender(new OffenderObject(author.getID()));
                }
                String response = "> #mentionAdmin# " + author.mention() + "  has attempted to post more than " + guildConfig.getMaxMentionLimit() + " Mentions in a single message.";
                if (guildConfig.getRoleToMention().getRoleID() != null) {
                    response = response.replaceAll("#mentionAdmin#", guild.getRoleByID(guildConfig.getRoleToMention().getRoleID()).mention());
                } else {
                    response = response.replaceAll("#mentionAdmin#", "Admin");
                }
                if (TimedEvents.getDoAdminMention(guildID) == 0) {
                    Utility.sendMessage(response, channel);
                    TimedEvents.setDoAdminMention(guildID, 60);
                }
            }
        }
        flushFiles();
    }

    //File handlers

    private void flushFiles() {
        Utility.flushFile(guildID, Constants.FILE_GUILD_CONFIG, guildConfig, guildConfig.isProperlyInit());
        Utility.flushFile(guildID, Constants.FILE_CUSTOM, customCommands, customCommands.isProperlyInit());
        Utility.flushFile(guildID, Constants.FILE_CHARACTERS, characters, characters.isProperlyInit());
        Utility.flushFile(guildID, Constants.FILE_SERVERS, servers, servers.isProperlyInit());
        Utility.flushFile(guildID, Constants.FILE_COMPETITION, competition, competition.isProperlyInit());
    }

    private void handleLogging(IChannel loggingChannel, CommandAnnotation commandAnno) {
        StringBuilder builder = new StringBuilder();
        builder.append("> **" + author.getDisplayName(guild) + "** Has Used Command `" + command + "`");
        if (!commandAnno.usage().equals("")) {
            builder.append(" with args: `" + args + "`");
        }
        builder.append(" in channel " + channel.mention() + " .");
        Utility.sendMessage(builder.toString(), loggingChannel);
    }

    //BlackListed Phrase Remover
    private void checkBlacklist() {
        if (guildConfig == null) {
            return;
        }
        if (guildConfig.getBlackList() == null) {
            return;
        }
        if (guildConfig.doBlackListing()) {
            for (BlackListObject bLP : guildConfig.getBlackList()) {
                if (message.toString().toLowerCase().contains(bLP.getPhrase().toLowerCase())) {
                    if (guildConfig.testIsTrusted(author, guild)) {
                        return;
                    }
                    String response = bLP.getReason();
                    if (response.contains("#mentionAdmin#")) {
                        if (guildConfig.getRoleToMention().getRoleID() != null) {
                            response = response.replaceAll("#mentionAdmin#", guild.getRoleByID(guildConfig.getRoleToMention().getRoleID()).mention());
                        } else {
                            response = response.replaceAll("#mentionAdmin#", "Admin");
                        }
                        if (TimedEvents.getDoAdminMention(guildID) == 0) {
                            Utility.sendMessage(response, channel);
                            TimedEvents.setDoAdminMention(guildID, 60);
                        }
                        Utility.deleteMessage(message);
                    } else {
                        Utility.deleteMessage(message);
                        Utility.sendMessage(response, channel);
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
                    if ((Constants.PREFIX_COMMAND + s).equalsIgnoreCase(command)) {

                        //test for args required
                        if (commandAnno.requiresArgs() && args.equals("")) {
                            Utility.sendMessage("Command is missing Arguments: \n" + Utility.getCommandInfo(commandAnno), channel);
                            return;
                        }
                        //init relevant files
                        if (commandAnno.type().equals(Constants.TYPE_SERVERS) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                            servers = (Servers) Utility.initFile(guildID, Constants.FILE_SERVERS, Servers.class);
                        }
                        if (commandAnno.type().equals(Constants.TYPE_CHARACTER) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                            characters = (Characters) Utility.initFile(guildID, Constants.FILE_CHARACTERS, Characters.class);
                        }
                        if (commandAnno.type().equals(Constants.TYPE_CC) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                            customCommands = (CustomCommands) Utility.initFile(guildID, Constants.FILE_CUSTOM, CustomCommands.class);
                        }
                        if (commandAnno.type().equals(Constants.TYPE_COMPETITION) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                            competition = (Competition) Utility.initFile(guildID, Constants.FILE_COMPETITION, Competition.class);
                        }

                        //Logging Handling
                        if (guildConfig.doAdminLogging()) {
                            IChannel loggingChannel;
                            if (commandAnno.doAdminLogging() && guildConfig.getChannelTypeID(Constants.CHANNEL_ADMIN_LOG) != null) {
                                loggingChannel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_ADMIN_LOG));
                                handleLogging(loggingChannel, commandAnno);
                            }
                        }
                        if (guildConfig.doGeneralLogging()) {
                            IChannel loggingChannel;
                            if (!commandAnno.doAdminLogging() && guildConfig.getChannelTypeID(Constants.CHANNEL_SERVER_LOG) != null) {
                                loggingChannel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_SERVER_LOG));
                                handleLogging(loggingChannel, commandAnno);
                            }
                        }

                        //Channel Validation
                        if (!commandAnno.channel().equals(Constants.CHANNEL_ANY)) {
                            if (guildConfig.getChannelTypeID(commandAnno.channel()) == null || channel.getID().equals(guildConfig.getChannelTypeID(commandAnno.channel()))) {
                                channelCorrect = true;
                            } else channelCorrect = false;
                        } else channelCorrect = true;

                        //Owner bypass
                        if (guild.getOwner().equals(author) || author.getID().equals(Globals.creatorID)) {
                            channelCorrect = true;
                        }

                        //Permission Validation
                        if (channelCorrect) {
                            if (!Arrays.equals(commandAnno.perms(), new Permissions[]{Permissions.SEND_MESSAGES})) {
                                permsCorrect = Utility.testForPerms(commandAnno.perms(), author, guild);
                            } else permsCorrect = true;

                            //Owner bypass
                            if (guild.getOwner().equals(author) || author.getID().equals(Globals.creatorID)) {
                                permsCorrect = true;
                            }

                            //message sending
                            if (permsCorrect) {
                                try {
                                    if (commandAnno.doResponseGeneral()) {
                                        channel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_GENERAL));
                                    }
                                    Utility.sendMessage((String) m.invoke(this), channel);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            } else
                                Utility.sendMessage(noAllowed, channel);
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

    //
    //
    //
    //
    //
    //Help commands
    @CommandAnnotation(
            name = "Help", description = "Gives information about Sail, including the commands it can run.", usage = "[Command Type]",
            type = Constants.TYPE_HELP)
    public String help() {
        Method[] methods = this.getClass().getMethods();
        ArrayList<String> types = new ArrayList<>();
        for (Method m : methods) {
            if (m.isAnnotationPresent(CommandAnnotation.class)) {
                boolean typeFound = false;
                CommandAnnotation anno = m.getAnnotation(CommandAnnotation.class);
                for (String s : types) {
                    if (s.equalsIgnoreCase(anno.type())) {
                        typeFound = true;
                    }
                }
                if (!typeFound) {
                    types.add(anno.type());
                }
            }
        }
        Collections.sort(types);
        StringBuilder builder = new StringBuilder();
        if (args.equals("")) {
            builder.append("> Here are the command types you can search from:\n");
            for (String s : types) {
                builder.append(Constants.PREFIX_INDENT + s + "\n");
            }
            builder.append("> You can search for commands with those types by doing:\n");
            builder.append(Constants.PREFIX_INDENT + Utility.getCommandInfo("help") + "\n");
            builder.append("> For more infomation about Sail and its commands head over to:\n");
            builder.append(Constants.PREFIX_INDENT + "<https://github.com/Vaerys-Dawn/DiscordSailv2>\n");
            builder.append("> If you would like to report a bug or suggest a feature for this bot\n");
            builder.append(Constants.PREFIX_INDENT + "Please DM " + message.getClient().getOurUser().mention() + ".");
        } else {
            boolean isFound = false;
            ArrayList<String> commands = new ArrayList<>();
            for (String s : types) {
                if (args.equalsIgnoreCase(s)) {
                    isFound = true;
                    builder.append("> Here are all of the " + s + " Commands you can use.\n");
                    for (Method m : methods) {
                        if (m.isAnnotationPresent(CommandAnnotation.class)) {
                            CommandAnnotation anno = m.getAnnotation(CommandAnnotation.class);
                            if (anno.type().equalsIgnoreCase(s)) {
                                commands.add(Constants.PREFIX_INDENT + Constants.PREFIX_COMMAND + anno.name() + "\n");
                            }
                        }
                    }
                    Collections.sort(commands);
                    commands.forEach(builder::append);
                    builder.append("> If you would like to know more about each command you can run:\n");
                    builder.append(Constants.PREFIX_INDENT + Utility.getCommandInfo("info"));
                }
            }
            if (!isFound) {
                return "> There are no commands with the type: " + args + ".\n" + Constants.PREFIX_INDENT + Utility.getCommandInfo("help");
            }
        }
        return builder.toString();
    }

    @CommandAnnotation(name = "Info", description = "Gives information about a command.", usage = "[Command Name]",
            type = Constants.TYPE_HELP, requiresArgs = true)
    public String info() {
        Method[] methods = this.getClass().getMethods();
        StringBuilder commandAliases = new StringBuilder();
        for (Method m : methods) {
            if (m.isAnnotationPresent(CommandAnnotation.class)) {
                ArrayList<String> aliases = new ArrayList<>();
                CommandAnnotation cAnno = m.getAnnotation(CommandAnnotation.class);
                aliases.add(cAnno.name());
                if (m.isAnnotationPresent(AliasAnnotation.class)) {
                    AliasAnnotation aAnno = m.getAnnotation(AliasAnnotation.class);
                    Collections.addAll(aliases, aAnno.alias());
                }
                for (String s : aliases) {
                    if (args.equalsIgnoreCase(s)) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("> **" + Constants.PREFIX_COMMAND + cAnno.name() + " " + cAnno.usage() + "**\n");
                        builder.append(Constants.PREFIX_INDENT + cAnno.description() + "\n");
                        builder.append(Constants.PREFIX_INDENT + "Type: " + cAnno.type() + "\n");
                        if (!cAnno.channel().equals(Constants.CHANNEL_ANY) && guildConfig.getChannelTypeID(cAnno.channel()) != null) {
                            builder.append(Constants.PREFIX_INDENT + "Channel: " + guild.getChannelByID(guildConfig.getChannelTypeID(cAnno.channel())).mention() + ".\n");
                        }
                        if (!Arrays.equals(cAnno.perms(), new Permissions[]{Permissions.SEND_MESSAGES})) {
                            builder.append(Constants.PREFIX_INDENT + "Permissions: ");
                            for (Permissions p : cAnno.perms()) {
                                builder.append(p.name() + ", ");
                            }
                            builder.delete(builder.length() - 2, builder.length());
                            builder.append(".\n");
                        }
                        if (m.isAnnotationPresent(AliasAnnotation.class)) {
                            builder.append(Constants.PREFIX_INDENT + "Aliases: ");
                            for (String alias : aliases) {
                                builder.append(alias + ", ");
                            }
                            builder.delete(builder.length() - 2, builder.length());
                            builder.append(".\n");
                        }
                        return builder.toString();
                    }
                }
            }
        }
        return "> Command with the name " + args + " not found.";
    }

    @CommandAnnotation(
            name = "Report", description = "Can be used to send a user report to the server staff.", usage = "[@user] [Reason]",
            type = Constants.TYPE_HELP, requiresArgs = true, doAdminLogging = true)
    public String report() {
        if (message.getMentions().size() > 0) {
            String mentionee = message.getMentions().get(0).mention();
            String channelID = guildConfig.getChannelTypeID(Constants.CHANNEL_ADMIN);
            String reason = args.replace("<@" + message.getMentions().get(0).getID() + ">", "");
            reason = reason.replace("<@!" + message.getMentions().get(0).getID() + ">", "");
            if (channelID != null) {
                Utility.sendMessage("**User Report**\nReporter: " + author.mention() + "\nReported: " + mentionee + "\nReason: `" + reason + "`", guild.getChannelByID(channelID));
                return "> User Report sent.";
            } else {
                return "> Your report could not be sent as the server does not have an admin channel set up at this time.";
            }
        }
        return Utility.getCommandInfo("report");
    }

    //
    //
    //
    //
    //
    //General commands
    @CommandAnnotation(
            name = "Hello", description = "Says Hello.",
            type = Constants.TYPE_GENERAL)
    public String hello() {
        return "> Hello " + author.getDisplayName(guild) + ".";
    }

    @CommandAnnotation(
            name = "Test", description = "Tests things.", usage = "[Lol this command has no usages XD]",
            type = Constants.TYPE_GENERAL, channel = Constants.CHANNEL_BOT_COMMANDS, perms = {Permissions.MANAGE_MESSAGES}, doAdminLogging = true)
    public String test() {
        return "> tested";
    }

    @CommandAnnotation(
            name = "UserImage", description = "Gets the Mentionee's Profile Image.", usage = "[@Users...]",
            type = Constants.TYPE_GENERAL, requiresArgs = true)
    public String getUserAvatar() {
        List<IUser> mentions = message.getMentions();
        StringBuilder builder = new StringBuilder();
        for (IUser u : mentions) {
            builder.append(u.getDisplayName(guild) + ": " + u.getAvatarURL() + "\n");
        }
        return builder.toString();
    }

    @CommandAnnotation(
            name = "GetGuildInfo", description = "Sends Information about the server to your Direct Messages.",
            type = Constants.TYPE_GENERAL)
    public String getGuildInfo() {
        String guildName = guild.getName();
        LocalDateTime creationDate = guild.getCreationDate();
        IUser guildOwner = guild.getOwner();
        IRegion region = guild.getRegion();
        List<IRole> roles = guild.getRoles();
        StringBuilder builder = new StringBuilder();
        ArrayList<String> cosmeticRoleStats = new ArrayList<>();
        ArrayList<String> modifierRoleStats = new ArrayList<>();
        int totalCosmetic = 0;
        int totalModified = 0;
        builder.append("***[" + guildName.toUpperCase() + "]***");
        builder.append("\n\n> Guild ID : **" + guildID);
        builder.append("**\n> Creation Date : **" + creationDate.getYear() + " " + creationDate.getMonth() + " " + creationDate.getDayOfMonth() + " - " + creationDate.getHour() + ":" + creationDate.getMinute());
        builder.append("**\n> Guild Owner : **@" + guildOwner.getName() + "#" + guildOwner.getDiscriminator() + "**");
        if (region != null) {
            builder.append("\n> Region : **" + region.getName() + "**");
        }
        builder.append("\n> Total Members: **" + guild.getUsers().size()+ "**");
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_SERVER}, author, guild) || author.getID().equals(Globals.creatorID)) {
            builder.append("\n\n***[GUILD CONFIG OPTIONS]***");
            builder.append("\n> LoginMessage = **" + guildConfig.doLoginMessage());
            builder.append("**\n> GeneralLogging = **" + guildConfig.doGeneralLogging());
            builder.append("**\n> AdminLogging = **" + guildConfig.doAdminLogging());
            builder.append("**\n> BlackListing = **" + guildConfig.doBlackListing());
            builder.append("**\n> MaxMentions = **" + guildConfig.doMaxMentions());
            builder.append("**\n> ShitPostFiltering = **" + guildConfig.doShitPostFiltering());
            builder.append("**\n> MuteRepeatOffenders = **" + guildConfig.doMuteRepeatOffenders());
            builder.append("**\n> Muted Role : **@" + guildConfig.getMutedRole().getRoleName());
            builder.append("**\n> RoleToMention : **@" + guildConfig.getRoleToMention().getRoleName() +"**");
        }
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_ROLES}, author, guild) || author.getID().equals(Globals.creatorID)) {
            builder.append("\n\n***[ROLES]***");
            ArrayList<RoleStatsObject> statsObjects = new ArrayList<>();
            for (IRole r : roles) {
                if (!r.isEveryoneRole()){
                    statsObjects.add(new RoleStatsObject(r, guildConfig));
                }
            }
            for (RoleStatsObject roleObject : statsObjects) {
                for (IUser user : guild.getUsers()) {
                    for (IRole r :user.getRolesForGuild(guild)){
                        if(r.getID().equals(roleObject.getRoleID())){
                            roleObject.addUser();
                        }
                    }
                }
            }
            for (RoleStatsObject rso: statsObjects ){
                String formated = "\n> **" + rso.getRoleName() + "** Colour : \"**" + rso.getColour() + "**\", Total Users: \"**"+ rso.getTotalUsers() + "**\"";
                if (rso.isCosmetic()){
                    cosmeticRoleStats.add(formated);
                    totalCosmetic += rso.getTotalUsers();
                }
                if (rso.isModifier()){
                    modifierRoleStats.add(formated);
                    totalModified += rso.getTotalUsers();
                }
            }
            Collections.sort(cosmeticRoleStats);
            Collections.sort(modifierRoleStats);
            builder.append("\n\n**COSMETIC ROLES**");
            for (String s: cosmeticRoleStats){
                if (builder.length() > 1800){
                    Utility.sendDM(builder.toString(),author.getID());
                    builder.delete(0,builder.length());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                builder.append(s);
            }
            builder.append("\n > total users : \"**" + totalCosmetic + "**\"");
            builder.append("\n\n**MODIFIER ROLES**");
            for (String s: modifierRoleStats){
                if (builder.length() > 1800){
                    Utility.sendDM(builder.toString(),author.getID());
                    builder.delete(0,builder.length());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                builder.append(s);
            }
            builder.append("\n > total users : \"**" + totalModified +"**\"");
        }
        builder.append("\n\n------{END OF INFO}------");
        Utility.sendDM(builder.toString(),author.getID());
        return "> Info sent to you via Direct Message.";
    }

    @CommandAnnotation(
            name = "RemindMe", description = "Sets a Reminder for yourself, Limit 1 per user.", usage = "[Time Minutes] [Reminder Message]",
            type = Constants.TYPE_GENERAL, requiresArgs = true)
    public String setReminder() {
        String timeString = args.split(" ")[0];
        try {
            long timeMins = Long.parseLong(timeString);
            if (timeMins > 1440) {
                return "> Max reminder time is 1 day and your time is to large.";
            }
            String reminderMessage = args.replaceFirst(Pattern.quote(timeString + " "), "");
            if (!TimedEvents.addReminder(guildID, author.getID(), channel.getID(), timeMins, reminderMessage)) {
                return "> Reminder set for " + timeString + " Minute(s) from now.";
            } else {
                return "> You already have a reminder set.";
            }
        } catch (NumberFormatException e) {
            return "> Error Trying to set reminder.\n" +
                    Utility.getCommandInfo("setReminder");
        }
    }

    //
    //
    //
    //
    //
    //admin commands
    @CommandAnnotation(
            name = "Toggle", description = "Toggles Certain Parts of the Guild Config", usage = "[Toggle Type]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_SERVER}, requiresArgs = true, doAdminLogging = true)
    public String toggles() {
        Method[] methods = GuildConfig.class.getMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(ToggleAnnotation.class)) {
                ToggleAnnotation toggleAnno = m.getAnnotation(ToggleAnnotation.class);
                if (args.equalsIgnoreCase(toggleAnno.name())) {
                    try {
                        m.invoke(guildConfig);
                        return "> Toggled **" + toggleAnno.name() + "**.";
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "> You cannot toggle " + args + " as that is not a valid toggle.\n" + Constants.PREFIX_INDENT +
                "A list of toggles you can use can be found by performing the following command:\n" + Constants.PREFIX_INDENT +
                Utility.getCommandInfo("toggleTypes");
    }

    @CommandAnnotation(
            name = "ToggleTypes", description = "List the toggles an admin can use for your server.",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_SERVER})
    public String toggleTypes() {
        StringBuilder builder = new StringBuilder();
        Method[] methods = GuildConfig.class.getMethods();
        builder.append("> Here are the Types of toggles you have at your disposal:\n");
        ArrayList<String> types = new ArrayList<>();
        for (Method m : methods) {
            if (m.isAnnotationPresent(ToggleAnnotation.class)) {
                ToggleAnnotation toggleAnno = m.getAnnotation(ToggleAnnotation.class);
                types.add(toggleAnno.name());
            }
        }
        Collections.sort(types);
        for (String s : types) {
            builder.append(Constants.PREFIX_INDENT + s + "\n");
        }
        builder.append("> You can Toggle those types by using the command\n");
        builder.append(Constants.PREFIX_INDENT + Utility.getCommandInfo("toggles"));
        return builder.toString();
    }

    @AliasAnnotation(alias = {"SetupChannel"})
    @CommandAnnotation(
            name = "ChannelHere", description = "Sets the current channel as the channel type you select.", usage = "[Channel Type]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_CHANNELS}, requiresArgs = true, doAdminLogging = true)
    public String channelHere() {
        try {
            for (Field f : Constants.class.getDeclaredFields()) {
                if (f.getName().contains("CHANNEL_") && f.getType() == String.class && !f.get(null).equals(Constants.CHANNEL_ANY)) {
                    try {
                        if (args.equalsIgnoreCase((String) f.get(null))) {
                            guildConfig.setUpChannel((String) f.get(null), channel.getID());
                            return "> This channel is now the Server's **" + f.get(null) + "** channel.";
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "> Channel type with that name not found, you can see the channel types you can choose from " +
                "by running the command\n" + Utility.getCommandInfo("channelTypes");
    }

    @CommandAnnotation(
            name = "ChannelTypes", description = "Lists all of the Channel types that can be used in the command." + Constants.PREFIX_COMMAND + "ChannelHere.",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_CHANNELS})
    public String channelTypes() {
        StringBuilder builder = new StringBuilder();
        builder.append("> Here are the channel types you can set up.\n`");
        try {
            for (Field f : Constants.class.getDeclaredFields()) {
                if (f.getName().contains("CHANNEL_") && f.getType() == String.class && !f.get(null).equals(Constants.CHANNEL_ANY)) {
                    builder.append(f.get(null) + ", ");
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append("`.");
        return builder.toString();
    }

    @CommandAnnotation(
            name = "SetRoleToMention", description = "Sets the admin role that will be mentioned when the tag #admin# is used in the blacklisting process.", usage = "[Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true, doAdminLogging = true)
    public String setAdminRole() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.setRoleToMention(guild.getRoleByID(roleID).getName(), roleID);
        }
    }

    @CommandAnnotation(
            name = "SetMutedRole", description = "Sets the Muted role", usage = "[Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true, doAdminLogging = true)
    public String setMutedRole() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            guildConfig.setMutedRole(new RoleTypeObject(guild.getRoleByID(roleID).getName(), roleID));
            return "> Role: " + guild.getRoleByID(roleID).getName() + " Has been set as the Muted Role.";
        }
    }

    @CommandAnnotation(
            name = "AddRole", description = "Adds role to list of Cosmetic roles that can be selected.", usage = "[Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true, doAdminLogging = true)
    public String addRace() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.addRole(roleID, guild.getRoleByID(roleID).getName(), true);
        }
    }

    @CommandAnnotation(
            name = "DelRole", description = "Deletes role from list of Cosmetic roles that can be selected.", usage = "[Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true, doAdminLogging = true)
    public String removeRace() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.removeRole(roleID, guild.getRoleByID(roleID).getName(), true);
        }
    }

    @CommandAnnotation(
            name = "AddModif", description = "Adds role to list of Modifier roles that can be selected.", usage = "[Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true, doAdminLogging = true)
    public String addModifyerRole() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.addRole(roleID, guild.getRoleByID(roleID).getName(), false);
        }
    }

    @CommandAnnotation(
            name = "DelModif", description = "Deletes role from list of Modifier roles that can be selected.", usage = "[Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true, doAdminLogging = true)
    public String removeModifyerRole() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID == null) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.removeRole(roleID, guild.getRoleByID(roleID).getName(), false);
        }
    }

    @CommandAnnotation(
            name = "TrustedRoles", description = "add or remove a Trusted role.", usage = "+/- [Role name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true, doAdminLogging = true)
    public String trustedRoles() {
        String modif = args.split(" ")[0];
        if (modif.equals("+") || modif.equals("-")) {
            String roleID = Utility.getRoleIDFromName(args.replaceFirst(Pattern.quote(modif + " "), ""), guild);
            if (roleID == null) {
                return Constants.ERROR_ROLE_NOT_FOUND;
            }
            if (modif.equals("+")) {
                guildConfig.addTrusted(roleID);
                return "> Added role to Trusted Roles.";
            } else {
                guildConfig.delTrusted(roleID);
                return "> Removed role from Trusted Roles.";
            }
        } else {
            return Utility.getCommandInfo("trustedRoles");
        }
    }

    @CommandAnnotation(
            name = "ToggleShitpost", description = "Toggles the isShitpost tag of a custom command", usage = "[Command Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_MESSAGES}, requiresArgs = true)
    public String toggleShitpost() {
        return customCommands.toggleShitPost(args);
    }

    @CommandAnnotation(
            name = "ToggleLock", description = "Toggles the Locked tag of a custom command", usage = "[Command Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_MESSAGES}, requiresArgs = true)
    public String toggleLock() {
        return customCommands.toggleLock(args);
    }

    @CommandAnnotation(
            name = "AddBlacklist", description = "Adds a phrase to a blacklist of a certain type.", usage = "[Type] [Phrase] [Reason]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_SERVER}, requiresArgs = true, doAdminLogging = true)
    public String addToBlackList() {
        return "> Nothing interesting happens.";
    }

    @CommandAnnotation(
            name = "RemoveBlacklist", description = "Removes a phrase from a blacklist of a certain type.", usage = "[Type] [Phrase]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_SERVER}, requiresArgs = true, doAdminLogging = true)
    public String removefromBlackList() {
        return "> Nothing interesting happens.";
    }

    @CommandAnnotation(
            name = "Blacklist", description = "Tells you the blacklisted phrases of a certain type.", usage = "[Type]",
            type = Constants.TYPE_ADMIN, requiresArgs = true)
    public String blacklist() {
        return "> Nothing interesting happens.";
    }

    @CommandAnnotation(
            name = "UpdateInfo", description = "Posts the contents of the Guild's Info.TXT",
            type = Constants.TYPE_ADMIN, channel = Constants.CHANNEL_INFO, perms = {Permissions.MANAGE_SERVER})
    public String updateInfo() {
        if (guildConfig.getChannelTypeID(Constants.CHANNEL_INFO) == null) {
            return "> No Info channel set up yet, you need to set one up in order to run this command.\n" + Utility.getCommandInfo("channelHere");
        } else {
            new InfoHandler(channel, guild);
            return null;
        }
    }

    @CommandAnnotation(name = "Shutdown", description = "Shuts the bot down safely.",
            type = Constants.TYPE_ADMIN, doAdminLogging = true)
    public String logoff() {
        if (author.getID().equals(Globals.creatorID)) {
            Utility.sendMessage("> Shutting Down.", channel);
            try {
                Thread.sleep(4000);
                Globals.getClient().logout();
                Runtime.getRuntime().exit(0);
            } catch (DiscordException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return noAllowed;
        }
    }

    @CommandAnnotation(name = "UpdateAvatar", description = "Shuts the bot down safely.",
            type = Constants.TYPE_ADMIN, doAdminLogging = true)
    public String updateAvatar() {
        if (author.getID().equals(Globals.creatorID)) {
            ZonedDateTime midnightUTC = ZonedDateTime.now(ZoneOffset.UTC);
            midnightUTC = midnightUTC.withHour(0).withSecond(0).withMinute(0).withNano(0).plusDays(1);
            int day = midnightUTC.getDayOfWeek().getValue();
            logger.info(midnightUTC.toString() + "   DOW" + day);
            final Image avatar = Image.forFile(new File(Constants.DIRECTORY_GLOBAL_IMAGES + "avatarForDay_" + day + ".png"));
            Utility.updateAvatar(avatar);
            return "> Avatar updated.";
        } else {
            return noAllowed;
        }
    }

    //
    //
    //
    //
    //
    //role select commands
    @CommandAnnotation(
            name = "Role", description = "Sets your cosmetic role from the list of cosmetic roles.", usage = "[Role Name]",
            type = Constants.TYPE_ROLE_SELECT, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
    public String setRole() {
        ArrayList<RoleTypeObject> roles = guildConfig.getCosmeticRoles();
        String newRoleId = null;
        List<IRole> userRoles = guild.getRolesForUser(author);
        for (RoleTypeObject r : roles) {
            for (int i = 0; i < userRoles.size(); i++) {
                if (userRoles.get(i).getID().equals(r.getRoleID())) {
                    userRoles.remove(i);
                }
                if (args.equalsIgnoreCase(guild.getRoleByID(r.getRoleID()).getName())) {
                    newRoleId = Utility.getRoleIDFromName(args, guild);
                }
            }
        }
        if (newRoleId != null) {
            userRoles.add(guild.getRoleByID(newRoleId));
            if (Utility.roleManagement(author, guild, userRoles).get()) {
                return Constants.ERROR_UPDATING_ROLE;
            }
            return "> You have selected the cosmetic role: **" + guild.getRoleByID(newRoleId).getName() + "**.";
        } else {
            return "> Role with name: **" + args + "** not found in cosmetic roles list.";
        }
    }

    @CommandAnnotation(
            name = "Modifier", description = "Allows you to add or remove a modifier role from the list of modifier roles.", usage = "[add,remove] [Role Name]",
            type = Constants.TYPE_ROLE_SELECT, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
    public String modifiers() {
        String response = "> " + Constants.ERROR;
        try {
            String[] splitArgs = args.split(" ");
            String modifier = splitArgs[0];
            String roleName = args.replaceFirst(modifier + " ", "");
            String newRoleID = Utility.getRoleIDFromName(roleName, guild);
            if (newRoleID == null) {
                response = Constants.ERROR_ROLE_NOT_FOUND;
            } else {
                boolean isfound = false;
                for (RoleTypeObject r : guildConfig.getModifierRoles()) {
                    if (r.getRoleID().equals(newRoleID)) {
                        isfound = true;
                    }
                }
                if (isfound) {
                    if (modifier.equalsIgnoreCase("add")) {
                        if (Utility.roleManagement(author, guild, newRoleID, true).get()) {
                            return Constants.ERROR_UPDATING_ROLE;
                        }
                        response = "> Modifier Added: `" + guild.getRoleByID(newRoleID).getName() + "`.";
                    } else if (modifier.equalsIgnoreCase("remove")) {
                        if (Utility.roleManagement(author, guild, newRoleID, false).get()) {
                            return Constants.ERROR_UPDATING_ROLE;
                        }
                        response = "> Modifier Removed: `" + guild.getRoleByID(newRoleID).getName() + "`.";
                    } else {
                        Method m = this.getClass().getMethod("modifiers");
                        response = Utility.getCommandInfo(m.getAnnotation(CommandAnnotation.class));
                    }
                } else {
                    response = "> You cannot have that role as it is not a modifier.";
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return response;
    }

    @AliasAnnotation(alias = {"RoleList", "Roles"})
    @CommandAnnotation(
            name = "ListRoles", description = "Shows the list of cosmetic roles you can choose from.",
            type = Constants.TYPE_ROLE_SELECT, channel = Constants.CHANNEL_BOT_COMMANDS)
    public String listRoles() {
        StringBuilder builder = new StringBuilder();
        builder.append("> Here are the **Cosmetic** roles you can choose from:\n");
        int i = 0;
        int counter = 0;
        for (RoleTypeObject r : guildConfig.getCosmeticRoles()) {
            counter++;
            if (counter == guildConfig.getCosmeticRoles().size()) {
                if (i != 0) {
                    builder.append(guild.getRoleByID(r.getRoleID()).getName() + ".\n");
                } else {
                    builder.append(Constants.PREFIX_INDENT + guild.getRoleByID(r.getRoleID()).getName() + ".\n");
                }
            } else if (i == 0) {
                builder.append(Constants.PREFIX_INDENT + guild.getRoleByID(r.getRoleID()).getName() + ", ");
            } else if (i == 7) {
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ",\n");
                i = -1;
            } else {
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ", ");
            }
            i++;
        }
        return builder.toString();
    }

    @AliasAnnotation(alias = {"Modifiers"})
    @CommandAnnotation(
            name = "ListModifiers", description = "Shows the list of modifier roles you can choose from.",
            type = Constants.TYPE_ROLE_SELECT, channel = Constants.CHANNEL_BOT_COMMANDS)
    public String listModif() {
        StringBuilder builder = new StringBuilder();
        builder.append("> Here are the **Modifier** roles you can choose from:\n");
        int i = 0;
        int counter = 0;
        for (RoleTypeObject r : guildConfig.getModifierRoles()) {
            counter++;
            if (counter == guildConfig.getModifierRoles().size()) {
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ".\n");
            } else if (i == 0) {
                builder.append(Constants.PREFIX_INDENT + guild.getRoleByID(r.getRoleID()).getName() + ", ");
            } else if (i == 7) {
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ",\n");
                i = -1;
            } else {
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ", ");
            }
            i++;
        }
        return builder.toString();
    }

    //
    //
    //
    //
    //servers commands

    @CommandAnnotation(
            name = "AddServer", description = "Adds a server to the guild's server list.", usage = "[Server Name] [IP] (Port)",
            type = Constants.TYPE_SERVERS, channel = Constants.CHANNEL_SERVERS, requiresArgs = true)
    public String addServer() {
        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
        String port = "N/a";
        if (splitArgs.size() > 2) {
            port = splitArgs.get(2);
        }
        if (splitArgs.size() < 2) {
            return "> Cannot create server, Missing IP";
        }
        return servers.addServer(author.getID(), splitArgs.get(0), splitArgs.get(1), port);
    }

    @CommandAnnotation(
            name = "DeleteServer", description = "Removes a server from the guild's server list.", usage = "[Server Name]",
            type = Constants.TYPE_SERVERS, channel = Constants.CHANNEL_SERVERS, requiresArgs = true)
    public String delServer() {
        return servers.deleteServer(author.getID(), args, guild);
    }

    @CommandAnnotation(
            name = "EditServerIP", description = "Allows you to edit your server IP and Port.", usage = "[Server Name] [IP] (Port)",
            type = Constants.TYPE_SERVERS, channel = Constants.CHANNEL_SERVERS, requiresArgs = true)
    public String editServerIP() {
        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
        String port = "N/a";
        if (splitArgs.size() > 2) {
            port = splitArgs.get(2);
        }
        if (splitArgs.size() < 2) {
            return "> Cannot edit server IP, missing arguments.";
        }
        return servers.editIP(author.getID(), splitArgs.get(0), splitArgs.get(1), port, guild);
    }

    @CommandAnnotation(
            name = "EditServerDesc", description = "Allows you to edit your server description.", usage = "[Server Name] [Description]",
            type = Constants.TYPE_SERVERS, channel = Constants.CHANNEL_SERVERS, requiresArgs = true)
    public String editServeDesc() {
        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
        String desc;
        if (splitArgs.size() < 2) {
            return "> Cannot edit server description, missing arguments.";
        }
        desc = args.replaceFirst(Pattern.quote(splitArgs.get(0) + " "), "");
        return servers.editServerDesc(author.getID(), splitArgs.get(0), desc, guild);
    }

    @CommandAnnotation(
            name = "EditServerName", description = "Allows you to edit your server name.", usage = "[Server Name] [New Server Name]",
            type = Constants.TYPE_SERVERS, channel = Constants.CHANNEL_SERVERS, requiresArgs = true)
    public String editServeName() {
        ArrayList<String> splitArgs = new ArrayList<>(Arrays.asList(args.split(" ")));
        if (splitArgs.size() < 2) {
            return "> Cannot Edit Server Name as no new name was specified.";
        }
        return servers.editServerName(author.getID(), splitArgs.get(0), splitArgs.get(1), guild);
    }

    @CommandAnnotation(
            name = "Server", description = "Lists the information about a specific server.", usage = "[Server Name]",
            type = Constants.TYPE_SERVERS, channel = Constants.CHANNEL_SERVERS, requiresArgs = true)
    public String server() {
        return servers.getServer(args, guild);
    }

    @CommandAnnotation(
            name = "Servers", description = "List this guild's Servers.",
            type = Constants.TYPE_SERVERS, channel = Constants.CHANNEL_SERVERS)
    public String serverList() {
        return servers.getServerList();
    }


    //
    //
    //
    //
    //character commands

    @CommandAnnotation(name = "CharTransfer", description = "Required cus whatever",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_SERVER})
    public String transferChars() {
        BadCode.Characters oldchars = (BadCode.Characters) FileHandler.readFromJson(Constants.DIRECTORY_OLD_FILES + guildID + "_CharList.json", BadCode.Characters.class);
        ArrayList<CharacterObject> toTransfer = oldchars.transferCharacters();
        StringBuilder response = new StringBuilder();
        for (CharacterObject c : toTransfer) {
            response.append(characters.updateChar(c) + "\n");
        }
        System.out.println(response);
        return "> Characters transfered";
    }

    @CommandAnnotation(
            name = "UpdateChar", description = "Updates/Creates a Character.", usage = "[Character Name]",
            type = Constants.TYPE_CHARACTER, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
    public String updateChar() {
        ArrayList<RoleTypeObject> charRoles = new ArrayList<>();
        for (IRole r : author.getRolesForGuild(guild)) {
            for (RoleTypeObject ro : guildConfig.getCosmeticRoles()) {
                if (r.getID().equals(ro.getRoleID())) {
                    charRoles.add(ro);
                }
            }
        }
        return characters.updateChar(new CharacterObject(args.split(" ")[0], author.getID(), author.getDisplayName(guild), charRoles));
    }

    @CommandAnnotation(
            name = "DelChar", description = "Deletes a Character.", usage = "[Character Name]",
            type = Constants.TYPE_CHARACTER, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
    public String delChar() {
        boolean bypass = false;
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_ROLES}, author, guild)) {
            bypass = true;
        }
        return characters.delChar(args.split(" ")[0], author, guild, bypass);
    }

    @CommandAnnotation(
            name = "Char", description = "Selects a Character.", usage = "[Character Name]",
            type = Constants.TYPE_CHARACTER, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
    public String selChar() {
        return characters.selChar(args.split(" ")[0], author, guild, guildConfig);
    }

    //
    //
    //
    //
    //Custom command commands

    @CommandAnnotation(
            name = "NewCC", description = "Creates a Custom Command.", usage = "[Command Name] [Contents]",
            type = Constants.TYPE_CC, requiresArgs = true)
    public String newCC() {
        boolean isShitpost = false;
        boolean isLocked = false;
        String newContent;
        if (channel.getID().equals(guildConfig.getChannelTypeID(Constants.CHANNEL_SHITPOST))) {
            isShitpost = true;
        }
        String nameCC = args.split(" ")[0];
        String content = args.replaceFirst(Pattern.quote(nameCC) + " ", "");
        newContent = TagSystem.testForShit(content);
        if (!newContent.equals(content)) {
            isShitpost = true;
        }
        content = newContent;
        newContent = TagSystem.testForLock(content, author, guild);
        if (!newContent.equals(content)) {
            isLocked = true;
        }
        content = newContent;
        boolean isTrusted = guildConfig.testIsTrusted(author, guild);
        return customCommands.addCommand(isLocked, author.getID(), nameCC, content, isShitpost, guild, isTrusted);
    }

    @CommandAnnotation(
            name = "DelCC", description = "Deletes The custom command.", usage = "[Command Name]",
            type = Constants.TYPE_CC, requiresArgs = true)
    public String delCC() {
        return customCommands.delCommand(args, author, guild);
    }

    @CommandAnnotation(
            name = "EditCC", description = "Allows you to edit a custom command.", usage = "[Command Name] [New Contents]",
            type = Constants.TYPE_CC, requiresArgs = true)
    public String editCC() {
        return Constants.ERROR_NIH;
    }

    @AliasAnnotation(alias = {"ListCCs"})
    @CommandAnnotation(
            name = "CCList", description = "Lists all of the custom commands 16 at a time.", usage = "[Page Number]/@mention/#user#{[UserID]}",
            type = Constants.TYPE_CC)
    public String listCCs() {
        String tagUserPrefix = "#user#{";
        String tagUserSuffix = "}";
        String tagUser;
        if (message.getMentions().size() > 0) {
            return customCommands.getUserCommands(message.getMentions().get(0).getID());
        }
        if (args.contains(tagUserPrefix)) {
            tagUser = StringUtils.substringBetween(args, tagUserPrefix, tagUserSuffix);
            if (tagUser != null) {
                if (Globals.getClient().getUserByID(tagUser) != null) {
                    return customCommands.getUserCommands(tagUser);
                }
            }
        }
        try {
            int page;
            if (args == null || args.equals("")) {
                page = 1;
            } else {
                page = Integer.parseInt(args.split(" ")[0]);
            }
            return customCommands.listCommands(page);
        } catch (NumberFormatException e) {
            return "> what are you doing, why are you trying to search for the " + args + " page... \n" +
                    Constants.PREFIX_INDENT + "pretty sure you cant do that...";
        }
    }

    @CommandAnnotation(
            name = "SearchCCs", description = "Allows you to search the custom command list.", usage = "[Search Params]",
            type = Constants.TYPE_CC, requiresArgs = true)
    public String searchCCs() {
        return customCommands.search(args);
    }

    @CommandAnnotation(
            name = "CCInfo", description = "Gives you a bit of information about a custom command.", usage = "[Command Name]",
            type = Constants.TYPE_CC, requiresArgs = true)
    public String infoCC() {
        return customCommands.getCommandInfo(args);
    }

    @CommandAnnotation(
            name = "HelpTags", description = "Gives you information about tags that you can use with S.A.I.L.",
            type = Constants.TYPE_HELP)
    public String testTags() {
        return "> https://github.com/Vaerys-Dawn/DiscordSailv2/wiki/Custom-Command-Guide";
    }

    @CommandAnnotation(
            name = "GetCCData", description = "Sends a Json File with all of the Custom command's data.", usage = "[Command Name]",
            type = Constants.TYPE_CC, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
    public String getCCData() {
        customCommands.sendCCasJSON(channel.getID(), args);
        return null;
    }

    @CommandAnnotation(
            name = "TransferCC", description = "Transfers a legacy command to the new system", usage = "[Command Name]",
            type = Constants.TYPE_CC, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
    public String transferCommand() {
        BadCode.CustomCommands oldCommands = (BadCode.CustomCommands) FileHandler.readFromJson(Constants.DIRECTORY_OLD_FILES + guildID + "_CustomCommands.json", BadCode.CustomCommands.class);
        CCommandObject transfering = oldCommands.convertCommand(args);
        if (transfering == null) {
            return Constants.ERROR_CC_NOT_FOUND;
        }
        boolean locked = transfering.isLocked();
        String userID = transfering.getUserID();
        if (guild.getUserByID(userID) == null) {
            Utility.sendMessage("> This command's old owner no longer is part of this server.\n" + Constants.PREFIX_INDENT +
                    author.getDisplayName(guild) + " will become the new owner of this command.\n" +
                    "> I am now attempting to transfer the command over.", channel);
            userID = author.getID();
        } else {
            Utility.sendMessage("> I am now attempting to transfer " + guild.getUserByID(userID).getDisplayName(guild) + "'s command.", channel);
        }
        String name = transfering.getName();
        String contents = transfering.getContents(false);
        contents = contents.replace("#author!#", "#username#");
        boolean shitpost = transfering.isShitPost();
        boolean trusted;
        if (userID.equals(Globals.getClient().getOurUser().getID())) {
            trusted = true;
        } else {
            trusted = guildConfig.testIsTrusted(Globals.getClient().getUserByID(userID), guild);
        }
        return customCommands.addCommand(locked, userID, name, contents, shitpost, guild, trusted);
    }

    //
    //
    //
    //
    //
    //Competition Commands

    @AliasAnnotation(alias = {"Comp", "Enter"})
    @CommandAnnotation(
            name = "Competition", description = "Enters your image into the Sail Competition", usage = "[Image Link or Image File]",
            type = Constants.TYPE_COMPETITION, doAdminLogging = true, perms = {Permissions.MANAGE_SERVER})
    public String competition() {
        String fileName;
        String fileUrl;
        if (message.getAttachments().size() > 0) {
            List<IMessage.Attachment> attatchments = message.getAttachments();
            IMessage.Attachment a = attatchments.get(0);
            fileName = a.getFilename();
            fileUrl = a.getUrl();
        } else if (!args.equals("")) {
            fileName = author.getName() + "'s Entry";
            fileUrl = args;
        } else {
            return "> Missing a File or Image link to enter into the competition.";
        }
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        competition.newEntry(new PollObject(author.getDisplayName(guild), author.getID(), fileName, fileUrl, dateFormat.format(cal.getTime())));
        return "> Thank you " + author.getDisplayName(guild) + " For entering the Competition.";
    }

    @CommandAnnotation(
            name = "Vote", description = "Saves your vote.", usage = "[Vote...]",
            type = Constants.TYPE_COMPETITION, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true, perms = {Permissions.ADMINISTRATOR})
    public String voting() {
        return competition.addVote(author.getID(), args);
    }

    @CommandAnnotation(name = "FinalTally", description = "Posts the final scores",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_MESSAGES})
    public String finalVotes() {
        StringBuilder builder = new StringBuilder();
        builder.append("> tally being performed.\n");
        ArrayList<String> votes = competition.getVotes();
        int[] tally = new int[competition.getEntries().size()];
        for (int i = 0; i < tally.length; i++) {
            tally[i] = 0;
        }
        int userVoteClusters = 0;
        int totalVotes = 0;
        for (String s : votes) {
            userVoteClusters++;
            String[] splitSting = s.split(",");
            for (int i = 1; i < splitSting.length; i++) {
                int position = Integer.parseInt(splitSting[i]);
                tally[position - 1]++;
                totalVotes++;
            }
        }
        builder.append("total of users that voted: " + userVoteClusters + "\n");
        builder.append("total number of votes: " + totalVotes + "\n");
        int entry = 0;
        for (int i : tally) {
            entry++;
            builder.append("Entry " + entry + ": " + i + "\n");
        }
        return builder.toString();
    }
}