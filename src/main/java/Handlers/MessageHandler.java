package Handlers;

import Annotations.AliasAnnotation;
import Annotations.CommandAnnotation;
import Annotations.ToggleAnnotation;
import Main.Constants;
import Main.Globals;
import Main.TimedEvents;
import Main.Utility;
import Objects.BlackListObject;
import Objects.RoleTypeObject;
import POGOs.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

// TODO: 02/09/2016 Add a Buncha Stuff
// TODO: 02/09/2016 Help command, Migrate from Live Build
// TODO: 02/09/2016 Info command, Display: CommandName, Usage, Aliases, Permissions, Channel, Description  
// TODO: 02/09/2016 Role List Commands, Role Adding/Removal, Listing, Stats
// TODO: 02/09/2016 Server Listing Functionality
// TODO: 02/09/2016 Server creation e.i AddServer [Server Name] [IP] [Port] (AutoGen 4 Digit Number for UniqueID)
// TODO: 02/09/2016 Edit Server Desc Command
// TODO: 02/09/2016 Edit Server Info Command


/**
 * Created by Vaerys on 14/08/2016.
 */

/*
 * Annotation order:
 *
 * @AliasAnnotation(alias)
 * @CommandAnnotation(
 * name, description, usage,
 * type, channel, permissions, requiresArgs, doGeneralLogging, doResponseGeneral)
 */


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
        guildConfig = (GuildConfig) Utility.initFile(guildID,Constants.FILE_GUILD_CONFIG,GuildConfig.class);
        checkBlacklist();
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

    //File handlers

    private void flushFiles() {
        Utility.flushFile(guildID,Constants.FILE_GUILD_CONFIG,guildConfig,guildConfig.isProperlyInit());
        Utility.flushFile(guildID,Constants.FILE_CUSTOM,customCommands,customCommands.isProperlyInit());
        Utility.flushFile(guildID,Constants.FILE_CHARACTERS,characters,characters.isProperlyInit());
        Utility.flushFile(guildID,Constants.FILE_SERVERS,servers,servers.isProperlyInit());
        Utility.flushFile(guildID,Constants.FILE_COMPETITION,competition,competition.isProperlyInit());
    }

    private void handleLogging(IChannel loggingChannel, CommandAnnotation commandAnno) {
        StringBuilder builder = new StringBuilder();
        builder.append("> **" + author.getDisplayName(guild) + "** Has Used Command `" + command + "`");
        if (!commandAnno.usage().equals(Constants.NULL_VARIABLE)) {
            builder.append(" with args: `" + args + "`");
        }
        builder.append(" in channel " + channel.mention() + " .");
        Utility.sendMessage(builder.toString(), loggingChannel);
    }

    //BlackListed Phrase Remover
    private void checkBlacklist() {
        if (guildConfig.doBlackListing()) {
            for (BlackListObject bLP : guildConfig.getBlackList()) {
                if (message.toString().toLowerCase().contains(bLP.getPhrase().toLowerCase())) {
                    String response = bLP.getReason();
                    if (!guildConfig.getRoleToMention().getRoleID().equals(Constants.NULL_VARIABLE)) {
                        response = response.replaceAll("#mentionAdmin#", guild.getRoleByID(guildConfig.getRoleToMention().getRoleID()).mention());
                    } else {
                        response = response.replaceAll("#mentionAdmin#", "Admin");
                    }
                    if (TimedEvents.getDoAdminMention(guildID) == 0) {
                        Utility.sendMessage(response, channel);
                        TimedEvents.setDoAdminMention(guildID,60);
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
                    if ((Constants.PREFIX_COMMAND + s).equalsIgnoreCase(command)) {

                        //test for args required
                        if (commandAnno.requiresArgs() && args.equals("")) {
                            Utility.sendMessage("Command is missing Arguments: \n" + Utility.getCommandInfo(commandAnno), channel);
                            return;
                        }
                        //init relevant files
                        if (commandAnno.type().equals(Constants.TYPE_SERVERS) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                            servers = (Servers) Utility.initFile(guildID,Constants.FILE_SERVERS,Servers.class);
                        }
                        if (commandAnno.type().equals(Constants.TYPE_ROLE_SELECT) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                            characters = (Characters) Utility.initFile(guildID,Constants.FILE_CHARACTERS,Characters.class);
                        }
                        if (commandAnno.type().equals(Constants.TYPE_CC) || commandAnno.type().equals(Constants.TYPE_ADMIN)) {
                            customCommands = (CustomCommands) Utility.initFile(guildID,Constants.FILE_CUSTOM,CustomCommands.class);
                        }

                        //Logging Handling
                        if (guildConfig.doAdminLogging()) {
                            IChannel loggingChannel;
                            if (commandAnno.doAdminLogging() && guildConfig.getChannelTypeID(Constants.CHANNEL_ADMIN_LOG) != null) {
                                loggingChannel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_ADMIN_LOG));
                                handleLogging(loggingChannel, commandAnno);
                            }
                        }
                        if (guildConfig.doGeneralLogging()){
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
                        if (guild.getOwner().equals(author) || author.getID().equals(Globals.creatorID)) {
                            channelCorrect = true;
                        }

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

                            //bypasses
                            if (guild.getOwner().equals(author) || author.getID().equals(Globals.creatorID)) {
                                permsCorrect = true;
                            }

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

    //help commands

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
                    for (String s : aAnno.alias()) {
                        aliases.add(s);
                    }
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
            String reason = args.replaceAll(mentionee + " ", "");
            if (!channelID.equals(Constants.NULL_VARIABLE)) {
                Utility.sendMessage("**User Report**\nReporter: " + author.mention() + "\nReported: " + mentionee + "\nReason: `" + reason + "`", guild.getChannelByID(channelID));
                return "> User Report sent.";
            } else {
                return "> Your report could not be sent as the server does not have an admin channel set up at this time.";
            }
        }
        return Utility.getCommandInfo("report");
    }

    //General commands

    @CommandAnnotation(
            name = "Hello", description = "Says Hello.",
            type = Constants.TYPE_GENERAL)
    public String hello() {
        return "> Hello " + author.getDisplayName(guild) + ".";
    }

    @CommandAnnotation(
            name = "Test", description = "Tests things.",usage = "[Lol this command has no usages XD]",
            type = Constants.TYPE_GENERAL,channel = Constants.CHANNEL_BOT_COMMANDS,perms = {Permissions.MANAGE_MESSAGES}, doAdminLogging = true)
    public String test() {
        return "> Tested";
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

    //admin commands

    @CommandAnnotation(
            name = "Toggle", description = "Toggles logging within the logging channels.", usage = "[Toggle Type]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_CHANNELS},requiresArgs = true, doAdminLogging = true )
    public String toggles() {
        Method[] methods = GuildConfig.class.getMethods();
        for (Method m :methods){
            if (m.isAnnotationPresent(ToggleAnnotation.class)){
                ToggleAnnotation toggleAnno = m.getAnnotation(ToggleAnnotation.class);
                if (args.equalsIgnoreCase(toggleAnno.name())){
                    try {
                        m.invoke(guildConfig,new Object[]{});
                        return "> Toggled **" + toggleAnno.name() + "**.";
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
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
            name = "ToggleTypes",description = "List the toggles an admin can use for your server.",
            type = Constants.TYPE_ADMIN,perms = {Permissions.MANAGE_SERVER},doAdminLogging = true)
    public String toggleTypes(){
        StringBuilder builder = new StringBuilder();
        Method[] methods = GuildConfig.class.getMethods();
        builder.append("> Here are the Types of toggles you have at your disposal:\n");
        ArrayList<String> types = new ArrayList<>();
        for (Method m :methods){
            if (m.isAnnotationPresent(ToggleAnnotation.class)) {
                ToggleAnnotation toggleAnno = m.getAnnotation(ToggleAnnotation.class);
                types.add(toggleAnno.name());
            }
        }
        Collections.sort(types);
        for (String s: types){
            builder.append(Constants.PREFIX_INDENT + s +"\n");
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
                "by running the command `" + Constants.PREFIX_COMMAND + "ChannelTypes`.";
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
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true)
    public String setAdminRole() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID.equals(Constants.NULL_VARIABLE)) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.setRoleToMention(guild.getRoleByID(roleID).getName(), roleID);
        }
    }

    @CommandAnnotation(
            name = "AddRole", description = "Adds role to list of Cosmetic roles that can be selected.", usage = "[Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true)
    public String addRace() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID.equals(Constants.NULL_VARIABLE)) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.addRole(roleID, guild.getRoleByID(roleID).getName(), true);
        }
    }

    @CommandAnnotation(
            name = "DelRole", description = "Deletes role from list of Cosmetic roles that can be selected.", usage = "[Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true)
    public String removeRace() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID.equals(Constants.NULL_VARIABLE)) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.removeRole(roleID, guild.getRoleByID(roleID).getName(), true);
        }
    }

    @CommandAnnotation(
            name = "AddModif", description = "Adds role to list of Modifier roles that can be selected.", usage = "[Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true)
    public String addModifyerRole() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID.equals(Constants.NULL_VARIABLE)) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.addRole(roleID, guild.getRoleByID(roleID).getName(), false);
        }
    }

    @CommandAnnotation(
            name = "DelModif", description = "Deletes role from list of Modifier roles that can be selected.", usage = "[Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES}, requiresArgs = true)
    public String removeModifyerRole() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID.equals(Constants.NULL_VARIABLE)) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.removeRole(roleID, guild.getRoleByID(roleID).getName(), false);
        }
    }

    //role select commands

    @CommandAnnotation(name = "Role", description = "Sets your cosmetic role from the list of cosmetic roles.", usage = "[Role Name]",
            type = Constants.TYPE_ROLE_SELECT, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
    public String setRole() {
        ArrayList<RoleTypeObject> roles = guildConfig.getCosmeticRoles();
        String newRoleId = null;
        List<IRole> userRoles = guild.getRolesForUser(author);
        for (RoleTypeObject r: roles){
            for (int i = 0; i < userRoles.size();i++) {
                if (userRoles.get(i).getID().equals(r.getRoleID())){
                    userRoles.remove(i);
                }
                if (args.equalsIgnoreCase(guild.getRoleByID(r.getRoleID()).getName())) {
                    newRoleId = Utility.getRoleIDFromName(args, guild);
                }
            }
        }
        if (newRoleId != null) {
            if (Utility.roleManagement(author,guild,userRoles).get()){
                return Constants.ERROR_UPDATING_ROLE;
            }
            if (Utility.roleManagement(author, guild, newRoleId, true).get()){
                return Constants.ERROR_UPDATING_ROLE;
            }
            return "> You have selected the cosmetic role: **" + guild.getRoleByID(newRoleId).getName() + "**.";
        }else {
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
            if (newRoleID.equals(Constants.NULL_VARIABLE)) {
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
                        if (Utility.roleManagement(author, guild, newRoleID, true).get()){
                            return Constants.ERROR_UPDATING_ROLE;
                        }
                        response = "> Modifier Added: `" + guild.getRoleByID(newRoleID).getName() + "`.";
                    } else if (modifier.equalsIgnoreCase("remove")) {
                        if (Utility.roleManagement(author, guild, newRoleID, false).get()){
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

    @CommandAnnotation(name = "ListRoles", description = "Shows the list of cosmetic roles you can choose from.",
            type = Constants.TYPE_ROLE_SELECT, channel = Constants.CHANNEL_BOT_COMMANDS)
    public String listRoles() {
        StringBuilder builder = new StringBuilder();
        builder.append("> Here are the **Cosmetic** roles you can choose from:\n");
        int i = 0;
        int counter = 0;
        for (RoleTypeObject r: guildConfig.getCosmeticRoles()){
            counter++;
            if (counter == guildConfig.getCosmeticRoles().size()){
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ".\n");
            }else if (i == 0){
                builder.append(Constants.PREFIX_INDENT + guild.getRoleByID(r.getRoleID()).getName() + ", ");
            } else if (i == 7){
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ",\n");
                i = -1;
            }else {
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ", ");
            }
            i++;
        }
        return builder.toString();
    }

    @CommandAnnotation(name = "ListModifiers", description = "Shows the list of modifier roles you can choose from.",
            type = Constants.TYPE_ROLE_SELECT, channel = Constants.CHANNEL_BOT_COMMANDS)
    public String listModif() {
        StringBuilder builder = new StringBuilder();
        builder.append("> Here are the **Modifier** roles you can choose from:\n");
        int i = 0;
        int counter = 0;
        for (RoleTypeObject r: guildConfig.getModifierRoles()){
            counter++;
            if (counter == guildConfig.getModifierRoles().size()){
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ".\n");
            }else if (i == 0){
                builder.append(Constants.PREFIX_INDENT + guild.getRoleByID(r.getRoleID()).getName() + ", ");
            } else if (i == 7){
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ",\n");
                i = -1;
            }else {
                builder.append(guild.getRoleByID(r.getRoleID()).getName() + ", ");
            }
            i++;
        }
        return builder.toString();
    }

    //servers commands

    //character commands

    //Custom command commands

    //@CommandAnnotation(
    //        name = "newCC",description = "Creates a Custom Command.",usage = "[Command Name] [Contentes]",
    //        type = Constants.TYPE_CC, requiresArgs = true)
    public String newCC() {
        boolean isShitpost = false;
        if (channel.getID().equals(guildConfig.getChannelTypeID(Constants.CHANNEL_SHITPOST))) {
            isShitpost = true;
        }
        String cCName = args.split(" ")[0];
        String Content = args.replaceFirst(Pattern.quote(cCName) + " ", "");
        return customCommands.addCommand(false, author.getID(), cCName, Content, isShitpost);
    }

    @AliasAnnotation(alias = {"Echo", "TS"})
    @CommandAnnotation(name = "TagSystem", description = "Tests the tag system.", usage = "[args]",
            type = Constants.TYPE_ADMIN, channel = Constants.CHANNEL_SHITPOST, requiresArgs = true)
    public String testtags() {
        String response = args;
        String tagRandom;
        String tagRegex;
        String prefixRandom = "#random#{";
        String prefixRegex = "#regex#{";
        String lastAttepmt;
        try {
            if (response.contains(prefixRandom)) {
                do {
                    lastAttepmt = response;
                    tagRandom = StringUtils.substringBetween(response, prefixRandom, "}");
                    if (tagRandom != null) {
                        ArrayList<String> splitRandom = new ArrayList<>(Arrays.asList(tagRandom.split(";")));
                        Random random = new Random();
                        String toRegex = "#random#{" + tagRandom + "}";
                        response = response.replaceFirst(Pattern.quote(toRegex), splitRandom.get(random.nextInt(splitRandom.size())));
                    }
                } while (StringUtils.countMatches(response, prefixRandom) > 0 && (!lastAttepmt.equals(response)));
            }
            if (response.contains(prefixRegex)) {
                do {
                    lastAttepmt = response;
                    tagRegex = StringUtils.substringBetween(response, prefixRegex, "}");
                    if (tagRegex != null) {
                        ArrayList<String> splitRegex = new ArrayList<>(Arrays.asList(tagRegex.split(";")));
                        String toRegex = "#regex#{" + tagRegex + "}";
                        if (splitRegex.size() == 2) {
                            response = response.replaceAll(Pattern.quote(toRegex), "");
                            response = response.replaceAll(Pattern.quote(splitRegex.get(0)), splitRegex.get(1));
                        } else {
                            response = response.replaceAll(Pattern.quote(tagRegex), "#ERROR#");
                        }
                    }
                } while (StringUtils.countMatches(response, prefixRegex) > 0 && (!lastAttepmt.equals(response)));
            }
        } catch (PatternSyntaxException ex) {
            return "> An Error occurred while attempting to run this command.";
        }
        response = response.replaceAll("#author#", author.getDisplayName(guild));
        response = response.replaceAll("#channel#", channel.mention());
        response = response.replaceAll("#guild#", guild.getName());
        response = response.replaceAll("#authorID#", author.getID());
        response = response.replaceAll("#channelID#", channel.getID());
        response = response.replaceAll("#guildID#", guild.getID());
        response = response.replaceAll("@everyone,", "**[REDACTED]**");
        response = response.replaceAll("@here", "**[REDACTED]**");
        return response;
    }
}


//Competition Commands
//
//    @AliasAnnotation(alias = {"Comp", "Enter"})
//    @CommandAnnotation(
//            name = "Competition", description = "Enters your image into the Sail Competition", usage = "[Image Link or Image File]",
//            type = Constants.TYPE_GENERAL, doGeneralLogging = true, perms = {Permissions.ADMINISTRATOR})
//    public String competition() {
//        loadCompetition();
//        String fileName;
//        String fileUrl;
//        if (message.getAttachments().size() > 0) {
//            List<IMessage.Attachment> attatchments = message.getAttachments();
//            IMessage.Attachment a = attatchments.get(0);
//            fileName = a.getFilename();
//            fileUrl = a.getUrl();
//        } else if (!args.equals("")) {
//            fileName = author.getName() + "'s Entry";
//            fileUrl = args;
//        } else {
//            return "> Missing a File or Image link to enter into the competition.";
//        }
//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");
//        Calendar cal = Calendar.getInstance();
//        competition.newEntry(new Objects.PollObject(author.getDisplayName(guild), author.getID(), fileName, fileUrl, dateFormat.format(cal.getTime())));
//        return "> Thank you " + author.getDisplayName(guild) + " For entering the Competition.";
//    }
//
//    @CommandAnnotation(
//            name = "Vote", description = "Saves your vote.", usage = "[Vote...]",
//            type = Constants.TYPE_GENERAL, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
//    public String voting() {
//        loadCompetition();
//        return competition.addVote(author.getID(), args);
//    }
//
//    @CommandAnnotation(name = "FinalTally", description = "Posts the final scores",
//            type = Constants.TYPE_ADMIN, perms = {Permissions.ADMINISTRATOR})
//    public String finalVotes() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("> tally being performed.\n");
//        loadCompetition();
//        ArrayList<String> votes = competition.getVotes();
//        int[] tally = new int[10];
//        for (int i = 0; i < tally.length; i++) {
//            tally[i] = 0;
//        }
//        int userVoteClusters = 0;
//        int totalvotes = 0;
//        for (String s : votes) {
//            userVoteClusters++;
//            String[] splitSting = s.split(",");
//            for (int i = 1; i < splitSting.length; i++) {
//                int position = Integer.parseInt(splitSting[i]);
//                tally[position - 1]++;
//                totalvotes++;
//            }
//        }
//        builder.append("total of users that voted: " + userVoteClusters + "\n");
//        builder.append("total number of votes: " + totalvotes + "\n");
//        int entry = 0;
//        for (int i : tally) {
//            entry++;
//            builder.append("Entry " + entry + ": " + i + "\n");
//        }
//        return builder.toString();
//    }
//}