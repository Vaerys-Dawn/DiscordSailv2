package Handlers;

import Annotations.AliasAnnotation;
import Annotations.CommandAnnotation;
import Annotations.DualCommandAnnotation;
import Annotations.ToggleAnnotation;
import Main.*;
import Objects.*;
import POGOs.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.Image;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

// TODO: 08/01/2017 Merge Role commands "Role", "AddRole", "DelRole"
// TODO: 08/01/2017 Merge Modifier commands "AddModif", "DelModif", "Modifier"

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

    private GuildConfig guildConfig;
    private CustomCommands customCommands;
    private Servers servers;
    private Characters characters;
    private Competition competition;

    private FileHandler handler = new FileHandler();

    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public MessageHandler(String command, String args, IMessage message) {
        if (Globals.isModifingFiles) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.command = command;
        this.args = args;
        this.message = message;
        guild = message.getGuild();
        channel = message.getChannel();
        author = message.getAuthor();
        guildID = guild.getID();
        guildConfig = Globals.getGuildContent(guildID).getGuildConfig();
        customCommands = Globals.getGuildContent(guildID).getCustomCommands();
        servers = Globals.getGuildContent(guildID).getServers();
        characters = Globals.getGuildContent(guildID).getCharacters();
        competition = Globals.getGuildContent(guildID).getCompetition();
        noAllowed = "> I'm sorry " + author.getDisplayName(guild) + ", I'm afraid I can't do that.";
        checkBlacklist();
        checkMentionCount();
        if (author.isBot()) {
            return;
        }
        if (command.toLowerCase().startsWith(guildConfig.getPrefixCommand().toLowerCase())) {
            handleCommand();
        }
        if (command.toLowerCase().startsWith(guildConfig.getPrefixCC().toLowerCase())) {
            new CCHandler(command, args, message);
        }
    }

    private void checkMentionCount() {
        if (message.toString().contains("@everyone") || message.toString().contains("@here")) {
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
    }

    //File handlers


    private boolean checkforNulls() {
        if (guildConfig == null || customCommands == null || characters == null || servers == null || competition == null) {
            Utility.sendMessage("***!!! IMPORTANT !!! A FILE ON THIS SERVER HAS EITHER CORRUPTED OR IS EMPTY PLEASE CONTACT THE BOT DEVELOPER ON THE LINKED SERVER***\n" +
                    "\nhttps://discord.gg/GQ5fUeE\n" +
                    "\n" +
                    "PLEASE SEND DETAILS OF WHAT COMMANDS WERE BEING RUN AT THE TIME OF THIS ERROR", channel);
            Utility.sendDM("***!!! A FILE ON SERVER WITH ID: " + guild.getID() + " HAS RETURNED EMPTY CHECK GUILD FILES !!!***", Globals.creatorID);
            return false;
        }
        return true;
    }

    private void handleLogging(IChannel loggingChannel, CommandAnnotation commandAnno) {
        StringBuilder builder = new StringBuilder();
        builder.append("> **" + author.getDisplayName(guild) + "** Has Used Command `" + command + "`");
        if (!commandAnno.usage().isEmpty() && !args.isEmpty()) {
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
                    if ((guildConfig.getPrefixCommand() + s).equalsIgnoreCase(command)) {

                        //test for args required
                        if (commandAnno.requiresArgs() && args.isEmpty()) {
                            Utility.sendMessage("Command is missing Arguments: \n" + Utility.getCommandInfo(commandAnno, guildConfig), channel);
                            return;
                        }
                        //init relevant files

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
        EmbedBuilder helpEmbed = new EmbedBuilder();
        StringBuilder builder = new StringBuilder();
        ArrayList<String> commands = new ArrayList<>();
        String spacer = TagSystem.tagSpacer("#spacer#");

        //setting embed colour to match Bot's Colour
        Color color = Utility.getUsersColour(Globals.getClient().getOurUser(), guild);
        if (color != null) {
            helpEmbed.withColor(color);
        }

        //getting Types of commands.
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
        //sort types
        Collections.sort(types);

        //building the embed
        if (args.isEmpty()) {
            builder.append("```\n");
            for (String s : types) {
                builder.append(s + "\n");
            }
            builder.append("```\n");
            helpEmbed.withTitle("Here are the Command Types I have available for use:");
            builder.append(">>**`" + spacer + Utility.getCommandInfo("help", guildConfig) + spacer + "`**<<\n");
            helpEmbed.withDescription(builder.toString());
            helpEmbed.appendField("Helpful Links", "[Suport Sail on Patreon](https://www.patreon.com/DawnFelstar)\n" +
                    "[Find Sail on GitHub](https://github.com/Vaerys-Dawn/DiscordSailv2)\n" +
                    "Support Discord - https://discord.gg/XSyQQrR", true);
            helpEmbed.withFooterText("Bot Version: " + Globals.version);
        } else {
            boolean isFound = false;
            for (String s : types) {
                if (args.equalsIgnoreCase(s)) {
                    isFound = true;
                    helpEmbed.withTitle("> Here are all of the " + s + " Commands I have available.");
                    for (Method m : methods) {
                        if (m.isAnnotationPresent(CommandAnnotation.class)) {
                            CommandAnnotation anno = m.getAnnotation(CommandAnnotation.class);
                            if (anno.type().equalsIgnoreCase(s)) {
                                if (m.isAnnotationPresent(DualCommandAnnotation.class)) {
                                    commands.add(guildConfig.getPrefixCommand() + anno.name() + Constants.PREFIX_INDENT + "*\n");
                                } else {
                                    commands.add(guildConfig.getPrefixCommand() + anno.name() + "\n");
                                }
                            }
                            if (m.isAnnotationPresent(DualCommandAnnotation.class)) {
                                DualCommandAnnotation dualAnno = m.getAnnotation(DualCommandAnnotation.class);
                                if (dualAnno.type().equalsIgnoreCase(s)) {
                                    commands.add(guildConfig.getPrefixCommand() + anno.name() + Constants.PREFIX_INDENT + "*\n");
                                }
                            }
                        }
                    }
                    Collections.sort(commands);
                    builder.append("```\n");
                    commands.forEach(builder::append);
                    builder.append("```\n");
                    builder.append(">>**`" + spacer + Utility.getCommandInfo("info", guildConfig) + spacer + "`**<<");
                    helpEmbed.withDescription(builder.toString());
                }
            }
            if (!isFound) {
                return "> There are no commands with the type: " + args + ".\n" + Constants.PREFIX_INDENT + Utility.getCommandInfo("help", guildConfig);
            }
        }
        Utility.sendEmbededMessage("", helpEmbed.build(), channel);
        return "";
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
                        EmbedBuilder infoEmbed = new EmbedBuilder();
                        Color color = Utility.getUsersColour(Globals.getClient().getOurUser(), guild);
                        if (color != null) {
                            infoEmbed.withColor(color);
                        }
                        infoEmbed.ignoreNullEmptyFields();
                        String primaryCommand = guildConfig.getPrefixCommand() + cAnno.name() + " " + cAnno.usage();
                        StringBuilder builder = new StringBuilder();
                        builder.append("Desc: **" + cAnno.description() + "**\n");
                        builder.append("Type: **" + cAnno.type() + "**\n");
                        if (!Arrays.equals(cAnno.perms(), new Permissions[]{Permissions.SEND_MESSAGES})) {
                            builder.append("Perms: **");
                            for (Permissions p : cAnno.perms()) {
                                builder.append(p.name() + ", ");
                            }
                            builder.delete(builder.length() - 2, builder.length());
                            builder.append("**");
                        }
                        infoEmbed.appendField(primaryCommand, builder.toString(), false);
                        if (m.isAnnotationPresent(DualCommandAnnotation.class)) {
                            DualCommandAnnotation dualAnno = m.getAnnotation(DualCommandAnnotation.class);
                            String dualCommand = guildConfig.getPrefixCommand() + cAnno.name() + " " + dualAnno.usage();
                            StringBuilder builderDual = new StringBuilder();
                            builderDual.append("Desc: **" + dualAnno.description() + "**\n");
                            builderDual.append("Type: **" + dualAnno.type() + "**\n");
                            if (!Arrays.equals(dualAnno.perms(), new Permissions[]{Permissions.SEND_MESSAGES})) {
                                Permissions[] perms;
                                if (!Arrays.equals(cAnno.perms(), new Permissions[]{Permissions.SEND_MESSAGES})) {
                                    perms = ArrayUtils.addAll(dualAnno.perms(), cAnno.perms());
                                } else {
                                    perms = dualAnno.perms();
                                }
                                builderDual.append("Perms: **");
                                for (Permissions p : perms) {
                                    builderDual.append(p.name() + ", ");
                                }
                                builderDual.delete(builderDual.length() - 2, builderDual.length());
                                builderDual.append("**");
                            }
                            infoEmbed.appendField(dualCommand, builderDual.toString(), false);
                        }
                        if (!cAnno.channel().equals(Constants.CHANNEL_ANY) && guildConfig.getChannelTypeID(cAnno.channel()) != null) {
                            infoEmbed.appendField("Channel: ", guild.getChannelByID(guildConfig.getChannelTypeID(cAnno.channel())).mention(), false);
                        }
                        if (m.isAnnotationPresent(AliasAnnotation.class)) {
                            StringBuilder aliasBuilder = new StringBuilder();
                            for (String alias : aliases) {
                                aliasBuilder.append(guildConfig.getPrefixCommand() + alias + ", ");
                            }
                            aliasBuilder.delete(aliasBuilder.length() - 2, aliasBuilder.length());
                            aliasBuilder.append(".\n");
                            infoEmbed.appendField("Aliases:", aliasBuilder.toString(), false);
                        }
                        Utility.sendEmbededMessage("", infoEmbed.build(), channel);
                        return "";
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
                StringBuilder builder = new StringBuilder();
                if (guildConfig.getRoleToMention().getRoleID() != null) {
                    builder.append(guild.getRoleByID(guildConfig.getRoleToMention().getRoleID()).mention() + "\n");
                }
                builder.append("**User Report**\nReporter: " + author.mention() + "\nReported: " + mentionee + "\nReason: `" + reason + "`");
                Utility.sendMessage(builder.toString(), guild.getChannelByID(channelID));
                return "> User Report sent.";
            } else {
                return "> Your report could not be sent as the server does not have an admin channel set up at this time.";
            }
        }
        return Utility.getCommandInfo("report", guildConfig);
    }

    @CommandAnnotation(
            name = "SilentReport", description = "Can be used to send a user report to the server staff.\n"
            + Constants.PREFIX_INDENT + " It will also remove the message used to call the command.", usage = "[@user] [Reason]",
            type = Constants.TYPE_HELP, requiresArgs = true, doAdminLogging = true)
    public String silentReport() {
        Utility.deleteMessage(message);
        report();
        return "";
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

    @DualCommandAnnotation(description = "This is another test.", usage = "[Blep]", type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_CHANNELS})
    @CommandAnnotation(
            name = "Test", description = "Tests things.", usage = "[Lol this command has no usages XD]",
            type = Constants.TYPE_GENERAL,channel = Constants.CHANNEL_BOT_COMMANDS, doAdminLogging = true)
    public String test() {
        EmbedBuilder imageEmbed = new EmbedBuilder();
        imageEmbed.withImage(author.getAvatarURL());
        Utility.sendFile("You tested the thing.",author.getAvatarURL(),channel);
        return "";
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
        return guildConfig.getInfo(guild, author);
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
                    Utility.getCommandInfo("setReminder", guildConfig);
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
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_SERVER}, doAdminLogging = true)
    public String toggles() {
        StringBuilder builder = new StringBuilder();
        if (!args.isEmpty()) {
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
            builder.append("> Could not find toggle \"" + args + "\".\n");
        }
        Method[] methods = GuildConfig.class.getMethods();
        builder.append("> Here is a list of available Guild Toggles:\n");
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
        builder.append(Constants.PREFIX_INDENT + Utility.getCommandInfo("toggles", guildConfig));
        return builder.toString();
    }

    @AliasAnnotation(alias = {"SetupChannel"})
    @CommandAnnotation(
            name = "ChannelHere", description = "Sets the current channel as the channel type you select.", usage = "[Channel Type]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_CHANNELS}, doAdminLogging = true)
    public String channelHere() {
        StringBuilder builder = new StringBuilder();
        if (!args.isEmpty()) {
            try {
                for (Field f : Constants.class.getDeclaredFields()) {
                    if (f.getName().contains("CHANNEL_") && f.getType() == String.class && !f.get(null).equals(Constants.CHANNEL_ANY)) {
                        if (args.equalsIgnoreCase((String) f.get(null))) {
                            guildConfig.setUpChannel((String) f.get(null), channel.getID());
                            return "> This channel is now the Server's **" + f.get(null) + "** channel.";
                        }
                    }
                }
                builder.append("> Could not find channel type \"" + args + "\"\n");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        builder.append("> Here is a list of available channel types:\n`");
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

    // TODO: 15/01/2017 this needs to parse into the modifier thinger
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
            return Utility.getCommandInfo("trustedRoles", guildConfig);
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

    //    @CommandAnnotation(
    //            name = "UpdateInfo", description = "Posts the contents of the Guild's Info.TXT",
    //            type = Constants.TYPE_ADMIN, channel = Constants.CHANNEL_INFO, perms = {Permissions.MANAGE_SERVER})
    public String updateInfo() {
        if (guildConfig.getChannelTypeID(Constants.CHANNEL_INFO) == null) {
            return "> No Info channel set up yet, you need to set one up in order to run this command.\n" + Utility.getCommandInfo("channelHere", guildConfig);
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
            ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
            if (Globals.doDailyAvatars == true) {
                for (DailyMessageObject d : Globals.dailyMessages) {
                    if (d.getDayOfWeek().equals(nowUTC.getDayOfWeek())) {
                        Image avatar = Image.forFile(new File(Constants.DIRECTORY_GLOBAL_IMAGES + d.getFileName()));
                        Utility.updateAvatar(avatar);
                    }
                }
            } else {
                Image avatar = Image.forFile(new File(Constants.DIRECTORY_GLOBAL_IMAGES + Globals.defaultAvatarFile));
                Utility.updateAvatar(avatar);
            }
            return "> Avatar Updated.";
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
    @DualCommandAnnotation(description = "Used to manage the selectable cosmetic roles.", usage = " +/-/add/del [Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES})
    @CommandAnnotation(
            name = "Role", description = "Sets your cosmetic role from the list of cosmetic roles.", usage = "[Role Name]/Remove",
            type = Constants.TYPE_ROLE_SELECT, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
    public String manageCosmeticRoles() {
        String response;
        SplitFirstObject splitFirst = new SplitFirstObject(args);
        if (splitFirst.getFirstWord() != null) {
            if (Utility.testModifier(splitFirst.getFirstWord()) != null && Utility.testForPerms(new Permissions[]{Permissions.MANAGE_ROLES}, author, guild)) {
                if (Utility.testModifier(splitFirst.getFirstWord())) {
                    String roleID = Utility.getRoleIDFromName(splitFirst.getRest(), guild);
                    if (roleID == null) {
                        return Constants.ERROR_ROLE_NOT_FOUND;
                    } else {
                        return guildConfig.addRole(roleID, guild.getRoleByID(roleID).getName(), true);
                    }
                } else {
                    String roleID = Utility.getRoleIDFromName(splitFirst.getRest(), guild);
                    if (roleID == null) {
                        return Constants.ERROR_ROLE_NOT_FOUND;
                    } else {
                        return guildConfig.removeRole(roleID, guild.getRoleByID(roleID).getName(), true);
                    }
                }
            } else {
                ArrayList<RoleTypeObject> roles = guildConfig.getCosmeticRoles();
                String newRoleId = null;
                List<IRole> userRoles = guild.getRolesForUser(author);
                for (RoleTypeObject role : roles) {
                    for (int i = 0; userRoles.size() > i; i++) {
                        if (role.getRoleID().equals(userRoles.get(i).getID())) {
                            userRoles.remove(i);
                        }
                        if (args.equalsIgnoreCase(guild.getRoleByID(role.getRoleID()).getName())) {
                            newRoleId = role.getRoleID();
                        }
                    }
                }
                if (splitFirst.getFirstWord().equalsIgnoreCase("remove")) {
                    response = "> Your Cosmetic role was removed.";
                } else {
                    if (newRoleId == null) {
                        if (Utility.testModifier(splitFirst.getFirstWord()) != null) {
                            return noAllowed;
                        }
                        return "> Role with name: **" + args + "** not found in **Cosmetic Role** list.";
                    } else {
                        userRoles.add(guild.getRoleByID(newRoleId));
                        response = "> You have selected the cosmetic role: **" + guild.getRoleByID(newRoleId).getName() + "**.";
                    }
                }
                if (Utility.roleManagement(author, guild, userRoles).get()) {
                    return Constants.ERROR_UPDATING_ROLE;
                } else {
                    return response;
                }
            }
        }
        return Constants.ERROR;
    }

    @AliasAnnotation(alias = {"Modif"})
    @DualCommandAnnotation(description = "Used to manage the selectable modifier roles.", usage = " +/-/add/del [Role Name]",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_ROLES})
    @CommandAnnotation(
            name = "Modifier", description = "Allows you to toggle a modifier role.", usage = "[Role Name]",
            type = Constants.TYPE_ROLE_SELECT, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
    public String manageModifiers() {
        String response;
        SplitFirstObject splitFirst = new SplitFirstObject(args);
        boolean rolefound = false;
        if (splitFirst.getFirstWord() != null) {
            if (Utility.testModifier(splitFirst.getFirstWord()) != null && Utility.testForPerms(new Permissions[]{Permissions.MANAGE_ROLES}, author, guild)) {
                if (Utility.testModifier(splitFirst.getFirstWord())) {
                    String roleID = Utility.getRoleIDFromName(splitFirst.getRest(), guild);
                    if (roleID == null) {
                        return Constants.ERROR_ROLE_NOT_FOUND;
                    } else {
                        return guildConfig.addRole(roleID, guild.getRoleByID(roleID).getName(), false);
                    }
                } else {
                    String roleID = Utility.getRoleIDFromName(splitFirst.getRest(), guild);
                    if (roleID == null) {
                        return Constants.ERROR_ROLE_NOT_FOUND;
                    } else {
                        return guildConfig.removeRole(roleID, guild.getRoleByID(roleID).getName(), false);
                    }
                }
            } else {
                ArrayList<RoleTypeObject> roles = guildConfig.getModifierRoles();
                String newRoleId = null;
                List<IRole> userRoles = guild.getRolesForUser(author);
                for (RoleTypeObject role : roles) {
                    if (args.equalsIgnoreCase(guild.getRoleByID(role.getRoleID()).getName())) {
                        newRoleId = role.getRoleID();
                    }
                }
                for (int i = 0; userRoles.size() > i; i++) {
                    if (userRoles.get(i).getID().equals(newRoleId)) {
                        userRoles.remove(i);
                        rolefound = true;
                    }
                }
                if (newRoleId == null) {
                    if (Utility.testModifier(splitFirst.getFirstWord()) != null) {
                        return noAllowed;
                    }
                    return "> Role with name: **" + args + "** not found in **Modifier Role** list.";
                } else {
                    if (!rolefound) {
                        userRoles.add(guild.getRoleByID(newRoleId));
                    }
                    response = "> You have toggled the Modifier role: **" + guild.getRoleByID(newRoleId).getName() + "**.";
                }
                if (Utility.roleManagement(author, guild, userRoles).get()) {
                    return Constants.ERROR_UPDATING_ROLE;
                } else {
                    return response;
                }
            }
        }
        return Constants.ERROR;
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

    @AliasAnnotation(alias = {"Modifiers", "Modifs"})
    @CommandAnnotation(
            name = "ListModifiers", description = "Shows the list of modifier roles you can choose from.",
            type = Constants.TYPE_ROLE_SELECT, channel = Constants.CHANNEL_BOT_COMMANDS)
    public String listModifiers() {
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
        return servers.getServerList(guildConfig);
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
        return "> Characters transferred";
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

    @AliasAnnotation(alias = {"CharList", "ListChars"})
    @CommandAnnotation(
            name = "Chars", description = "Shows you all of your characters.",
            type = Constants.TYPE_CHARACTER, channel = Constants.CHANNEL_BOT_COMMANDS)
    public String listChars() {
        return characters.listCharacters(author.getID(), guildConfig);
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
        SplitFirstObject splitfirst = new SplitFirstObject(args);
        String newContent;
        if (channel.getID().equals(guildConfig.getChannelTypeID(Constants.CHANNEL_SHITPOST))) {
            isShitpost = true;
        }
        String nameCC = splitfirst.getFirstWord();
        if (splitfirst.getRest() == null || splitfirst.getRest().isEmpty()) {
            return "> Custom command contents cannot be blank";
        }
        if (nameCC.contains("\n")) {
            return "> Command name cannot contain Newlines";
        }
        String content = splitfirst.getRest();
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
        return customCommands.addCommand(isLocked, author, nameCC, content, isShitpost, guild, guildConfig);
    }

    @CommandAnnotation(
            name = "DelCC", description = "Deletes The custom command.", usage = "[Command Name]",
            type = Constants.TYPE_CC, requiresArgs = true)
    public String delCC() {
        return customCommands.delCommand(args, author, guild);
    }

    @CommandAnnotation(
            name = "EditCC", description = "Allows you to edit a custom command.\nModes: Replace, Append, toEmbed\nMode is optional defaults to replace", usage = "[Command Name] (Mode) [New Contents]",
            type = Constants.TYPE_CC, requiresArgs = true)
    public String editCC() {
        SplitFirstObject getName = new SplitFirstObject(args);
        if (getName.getRest() == null){
            return Utility.getCommandInfo("editCC",guildConfig);
        }
        SplitFirstObject getMode = new SplitFirstObject(getName.getRest());
        return customCommands.editCC(getName.getFirstWord(),author,guild,getMode.getFirstWord(),getMode.getRest());
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
            return customCommands.getUserCommands(message.getMentions().get(0).getID(), guild, guildConfig);
        }
        if (args.contains(tagUserPrefix)) {
            tagUser = StringUtils.substringBetween(args, tagUserPrefix, tagUserSuffix);
            if (tagUser != null) {
                if (Globals.getClient().getUserByID(tagUser) != null) {
                    return customCommands.getUserCommands(tagUser, guild, guildConfig);
                }
            }
        }
        try {
            int page;
            if (args == null || args.isEmpty()) {
                page = 1;
            } else {
                page = Integer.parseInt(args.split(" ")[0]);
            }
            return customCommands.listCommands(page, guildConfig);
        } catch (NumberFormatException e) {
            return "> what are you doing, why are you trying to search for the " + args + " page... \n" +
                    Constants.PREFIX_INDENT + "pretty sure you cant do that...";
        }
    }

    @CommandAnnotation(
            name = "SearchCCs", description = "Allows you to search the custom command list.", usage = "[Search Params]",
            type = Constants.TYPE_CC, requiresArgs = true)
    public String searchCCs() {
        return customCommands.search(args, guildConfig,channel,message.getID());
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
        String filePath = Constants.DIRECTORY_OLD_FILES + guildID + "_CustomCommands.json";
        if (Paths.get(filePath).toFile().exists()) {
            BadCode.CustomCommands oldCommands = null;
            while (oldCommands == null) {
                oldCommands = (BadCode.CustomCommands) FileHandler.readFromJson(filePath, BadCode.CustomCommands.class);
            }
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
            return customCommands.addCommand(locked, Globals.getClient().getUserByID(userID), name, contents, shitpost, guild, guildConfig);
        } else {
            return "> Your Server Has no Legacy commands to transfer.";
        }
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
            type = Constants.TYPE_COMPETITION, doAdminLogging = true)
    public String competition() {
        if (guildConfig.doCompEntries()) {
            String fileName;
            String fileUrl;
            if (message.getAttachments().size() > 0) {
                List<IMessage.Attachment> attatchments = message.getAttachments();
                IMessage.Attachment a = attatchments.get(0);
                fileName = a.getFilename();
                fileUrl = a.getUrl();
            } else if (!args.isEmpty()) {
                fileName = author.getName() + "'s Entry";
                fileUrl = args;
            } else {
                return "> Missing a File or Image link to enter into the competition.";
            }
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            competition.newEntry(new PollObject(author.getDisplayName(guild), author.getID(), fileName, fileUrl, dateFormat.format(cal.getTime())));
            return "> Thank you " + author.getDisplayName(guild) + " For entering the Competition.";
        } else {
            return "> Competition Entries are closed.";
        }
    }

    @CommandAnnotation(
            name = "Vote", description = "Saves your vote.", usage = "[Vote...]",
            type = Constants.TYPE_COMPETITION, channel = Constants.CHANNEL_BOT_COMMANDS, requiresArgs = true)
    public String voting() {
        if (guildConfig.doCompVoting()) {
            return competition.addVote(author.getID(), args);
        } else {
            return "> Competition Voting is closed.";
        }
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

    @CommandAnnotation(name = "GetEntries", description = "Posts the final scores",
            type = Constants.TYPE_ADMIN, perms = {Permissions.MANAGE_MESSAGES})
    public String getEntries() {
        int i = 1;
        for (PollObject p : competition.getEntries()) {
            Utility.sendMessage("Entry " + i + " : " + guild.getUserByID(p.getUserID()).mention() + "\n" +
                    p.getFileUrl(), channel);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        return "";
    }
}