package Handlers;

import Annotations.AliasAnnotation;
import Annotations.CommandAnnotation;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.BlackListObject;
import Objects.CompetitionObject;
import Objects.RoleTypeObject;
import POGOs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.obj.Embedded;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
    private boolean loadedCompetiton = false;

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
        loadConfig();
        checkBlacklist();
        if (author.isBot()) {
            return;
        }
        if (command.toLowerCase().startsWith(Constants.PREFIX_COMMAND.toLowerCase())) {
            handleCommand();
        }
        // TODO: 14/08/2016 ccs must also be handled here
        if (command.toLowerCase().startsWith(Constants.PREFIX_CC.toLowerCase())) {
            new CCHandler(command, args, message);
        }
    }

    //File handlers
    private void loadConfig() {
        guildConfig = (GuildConfig) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), GuildConfig.class);
        loadedConfig = true;
    }

    private void loadServers() {
        servers = (Servers) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_SERVERS), Servers.class);
        loadedServers = true;
    }

    private void loadCharacters() {
        characters = (Characters) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_CHARACTERS), Characters.class);
        loadedCharacters = true;
    }

    private void loadCC() {
        customCommands = (CustomCommands) handler.readfromJson(Utility.getFilePath(guildID, Constants.FILE_CUSTOM), CustomCommands.class);
        loadedCC = true;
    }

    private void loadCompetition() {
        competition = (Competition) handler.readfromJson(Constants.FILE_COMPETITION, Competition.class);
        loadedCompetiton = true;
    }

    private void flushFiles() {
        if (loadedConfig && guildConfig.properlyInit)
            handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), guildConfig);
        else if (loadedConfig && !guildConfig.properlyInit)
            logger.error("Null Suppressed - Guild Config, Guild ID:" + guildID);
        if (loadedCC && customCommands.properlyInit)
            handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_CUSTOM), customCommands);
        else if (loadedCC && !customCommands.properlyInit)
            logger.error("Null Suppressed - Custom Commands, Guild ID:" + guildID);
        if (loadedServers && servers.properlyInit)
            handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_SERVERS), servers);
        else if (loadedServers && !servers.properlyInit) logger.error("Null Suppressed - Servers, Guild ID:" + guildID);
        if (loadedCharacters && characters.properlyInit)
            handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_CHARACTERS), characters);
        else if (loadedCharacters && !characters.properlyInit)
            logger.error("Null Suppressed - Characters, Guild ID:" + guildID);
        if (loadedCompetiton && competition.properlyInit) handler.writetoJson(Constants.FILE_COMPETITION, competition);
        else if (loadedCompetiton && !competition.properlyInit)
            logger.error("Null Suppressed - Competition, Guild ID:" + guildID);
    }

    //BlackListed Phrase Remover
    private void checkBlacklist() {
        if (guildConfig.doBlackListing) {
            for (BlackListObject bLP : guildConfig.getBlackList()) {
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
                    if ((Constants.PREFIX_COMMAND + s).equalsIgnoreCase(command)) {
                        //init files
                        if (commandAnno.requiresArgs() && args.equals("")) {
                            Utility.sendMessage("Command is missing Arguments: \n" + Utility.getCommandInfo(commandAnno), channel);
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
                            if (!guildConfig.getChannelTypeID(Constants.CHANNEL_SERVER_LOG).equals(Constants.NULL_VARIABLE)) {
                                IChannel loggingChannel = guild.getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_SERVER_LOG));
                                StringBuilder builder = new StringBuilder();
                                builder.append("**" + author.getDisplayName(guild) + "** Has Used Command `" + command + "`");
                                if (!commandAnno.usage().equals(Constants.NULL_VARIABLE)) {
                                    builder.append(" with args: `" + args + "`");
                                }
                                builder.append(" in channel " + channel.mention() + " .");
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

    @CommandAnnotation(name = "Hello", description = "Says Hello.", type = Constants.TYPE_GENERAL)
    public String hello() {
        return "> Hello " + author.getDisplayName(guild) + ".";
    }

    @CommandAnnotation(name = "Test", description = "Tests things.", doLogging = true, type = Constants.TYPE_GENERAL)
    public String test() {
        return "> Tested";
    }

    @CommandAnnotation(name = "UserImage", description = "Gets the Mentionee's Profile Image.", usage = "[@Users]", type = Constants.TYPE_GENERAL)
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

    //race select commands
    @CommandAnnotation(
            name = "AddRole", description = "Adds role to list of Cosmetic roles that can be selected.", usage = "[Role Name]",
            requiresArgs = true, type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES})
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
            requiresArgs = true, type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES})
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
            requiresArgs = true, type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES})
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
            requiresArgs = true, type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES})
    public String removeModifyerRole() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID.equals(Constants.NULL_VARIABLE)) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.removeRole(roleID, guild.getRoleByID(roleID).getName(), false);
        }
    }

    @CommandAnnotation(
            name = "Modifier", description = "Grants or Removes a modifier.", usage = "[add,remove] [Role Name]",
            requiresArgs = true, type = Constants.TYPE_ROLE_SELECT, channel = Constants.CHANNEL_RACE_SELECT)
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
                        author.addRole(guild.getRoleByID(newRoleID));
                        response = "> Modifier Added: `" + guild.getRoleByID(newRoleID).getName() + "`.";
                    } else if (modifier.equalsIgnoreCase("remove")) {
                        author.removeRole(guild.getRoleByID(newRoleID));
                        response = "> Modifier Removed: `" + guild.getRoleByID(newRoleID).getName() + "`.";
                    } else {
                        Method m = this.getClass().getMethod("modifiers");
                        response = Utility.getCommandInfo(m.getAnnotation(CommandAnnotation.class));
                    }
                }else {
                    response = "> You cannot that role as it is not a modifier.";
                }
            }
        } catch (DiscordException e) {
            e.printStackTrace();
        } catch (RateLimitException e) {
            e.printStackTrace();
        } catch (MissingPermissionsException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return response;
    }

    @CommandAnnotation(
            name = "SetRoleToMention", description = "Sets the admin role that will be mentioned when the tag #admin# is used in the blacklisting process.", usage = "[Role Name]",
            requiresArgs = true, type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES})
    public String setAdminRole() {
        String roleID = Utility.getRoleIDFromName(args, guild);
        if (roleID.equals(Constants.NULL_VARIABLE)) {
            return Constants.ERROR_ROLE_NOT_FOUND;
        } else {
            return guildConfig.setRoleToMention(guild.getRoleByID(roleID).getName(), roleID);
        }
    }
    //servers commands

    //character commands

    //Custom command commands

    //@CommandAnnotation(
    //        name = "newCC",description = "Creates a Custom Command.",usage = "[Command Name] [Contentes]",
    //        requiresArgs = true,type = Constants.TYPE_CC)
    public String newCC() {
        String cCName = args.split(" ")[0];
        String Content = args.replaceFirst(cCName + " ", "");
        return customCommands.addCommand(false, author.getID(), cCName, Content);
    }

    //Competition Command

    @AliasAnnotation(alias = {"Comp", "Enter"})
    @CommandAnnotation(name = "Competition", description = "Enters your image into the Sail Competition", type = Constants.TYPE_GENERAL, doLogging = true, usage = "[Image Link or Image File]", perms = {Permissions.ADMINISTRATOR})
    public String competition() {
        loadCompetition();
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
        competition.newEntry(new CompetitionObject(author.getDisplayName(guild), author.getID(), fileName, fileUrl, dateFormat.format(cal.getTime())));
        return "> Thank you " + author.getDisplayName(guild) + " For entering the Competition.";
    }

    @CommandAnnotation(name = "Vote", description = "Saves your vote.", type = Constants.TYPE_GENERAL, channel = Constants.CHANNEL_RACE_SELECT, usage = "(x) (y) (z) ...", requiresArgs = true)
    public String voting() {
        loadCompetition();
        return competition.addVote(author.getID(), args);
    }
}
