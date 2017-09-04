package com.github.vaerys.main;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.*;
import com.github.vaerys.pogos.DailyMessages;
import com.github.vaerys.pogos.GuildConfig;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.validator.ValidatorException;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.*;
import sx.blah.discord.util.Image;

import javax.net.ssl.SSLHandshakeException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class Utility {

    static FileHandler handler = new FileHandler();

    //Logger
    final static Logger logger = LoggerFactory.getLogger(Utility.class);

    //Discord Utils
    public static IRole getRoleFromName(String roleName, IGuild guild) {
        IRole role = null;
        for (IRole r : guild.getRoles()) {
            if (r.getName().equalsIgnoreCase(roleName)) {
                role = r;
            }
        }
        return role;
    }

    public static boolean testForPerms(Permissions[] perms, IUser user, IGuild guild) {
        if (perms.length == 0) {
            return true;
        }
        if (guild == null) {
            return true;
        }
        if (user.getStringID().equals(Globals.creatorID)) {
//            logger.trace("User is Creator, BYPASSING.");
            return true;
        }
        if (user.getLongID() == guild.getOwnerLongID()) {
//            logger.trace("User is Guild Owner, GUILD : \"" + guild.getStringID() + "\", BYPASSING.");
            return true;
        }
        if (user.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)) {
            return true;
        }
        EnumSet<Permissions> toMatch = EnumSet.noneOf(Permissions.class);
        toMatch.addAll(Arrays.asList(perms));
        //Debug code.
        List<String> toMatchList = new ArrayList<String>() {{
            addAll(toMatch.stream().map(Enum::toString).collect(Collectors.toList()));
        }};
        List<String> userList = new ArrayList<String>() {{
            addAll(user.getPermissionsForGuild(guild).stream().map(Enum::toString).collect(Collectors.toList()));
        }};
//        if (true) {
//            logger.trace("To Match : " + Utility.listFormatter(toMatchList, true));
//            logger.trace("User Perms : " + Utility.listFormatter(userList, true));
//            logger.trace("Result : " + user.getPermissionsForGuild(guild).containsAll(toMatch));
//        }
        //end Debug
        return user.getPermissionsForGuild(guild).containsAll(toMatch);
    }

    //Command Utils
    public static String getCommandInfo(Command command, CommandObject commandObject) {
        StringBuilder response = new StringBuilder(">> **" + commandObject.guild.config.getPrefixCommand() + command.names()[0]);
        if (command.usage() != null) {
            response.append(" " + command.usage());
        }
        response.append("** <<");
        return response.toString();
    }

    public static String getCommandInfo(Command command) {
        StringBuilder response = new StringBuilder(">> **" + Globals.defaultPrefixCommand + command.names()[0]);
        if (command.usage() != null) {
            response.append(" " + command.usage());
        }
        response.append("** <<");
        return response.toString();
    }

    public static String checkBlacklist(String message, List<BlackListObject> blacklist) {
        for (BlackListObject b : blacklist) {
            if (message.toLowerCase().contains(b.getPhrase().toLowerCase())) {
                return b.getReason();
            }
        }
        return null;
    }

    //File Utils
    public static String getFilePath(String guildID, String type) {
        return Constants.DIRECTORY_STORAGE + guildID + "/" + type;
    }

    public static String getFilePath(String guildID, String type, boolean isBackup) {
        return Constants.DIRECTORY_BACKUPS + guildID + "/" + type;
    }

    public static String getDirectory(String guildID) {
        return Constants.DIRECTORY_STORAGE + guildID + "/";
    }

    public static String getGuildImageDir(String guildID) {
        return getDirectory(guildID) + Constants.DIRECTORY_GUILD_IMAGES;
    }

    public static String getDirectory(String guildID, boolean isBackup) {
        return Constants.DIRECTORY_BACKUPS + guildID + "/";
    }

    public static Object initFile(String guildID, String filePath, Class<?> objClass) {
        List<String> fileContents = FileHandler.readFromFile(Utility.getFilePath(guildID, filePath));
        if (fileContents == null || fileContents.size() == 0 || fileContents.get(0).equals("null")) {
            logger.error(Utility.getFilePath(guildID, filePath) + ". FILE EMPTY PLEASE CHECK FILE OR LOAD BACKUP.");
            return null;
        }
        Object object = null;
        int counter = 0;
        while (object == null) {
            object = handler.readFromJson(Utility.getFilePath(guildID, filePath), objClass);
            counter++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                sendStack(e);
            }
        }
        if (counter > 1) {
            logger.debug(filePath + " File for guild with id " + guildID + " took " + counter + " tries to Init");
        }
        return object;
    }

    public static void flushFile(String guildID, String filePath, Object object, boolean wasInit) {
        if (wasInit) {
            logger.trace(filePath + " - Saved.");
            handler.writeToJson(Utility.getFilePath(guildID, filePath), object);
        } else ;
    }

    public static void backupFile(String guildID, String type) {
        try {
            File backup1 = new File(getFilePath(guildID, type, true) + 1);
            File backup2 = new File(getFilePath(guildID, type, true) + 2);
            File backup3 = new File(getFilePath(guildID, type, true) + 3);
            File toBackup = new File(getFilePath(guildID, type));
            if (backup3.exists()) backup3.delete();
            if (backup2.exists()) backup2.renameTo(backup3);
            if (backup1.exists()) backup1.renameTo(backup2);
            if (toBackup.exists())
                Files.copy(Paths.get(toBackup.getPath()), backup1.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            sendStack(e);
        }
    }

    public static void backupConfigFile(String file, String fileBackup) {
        try {
            File backup1 = new File(fileBackup + 1);
            File backup2 = new File(fileBackup + 2);
            File backup3 = new File(fileBackup + 3);
            File toBackup = new File(file);
            if (backup3.exists()) backup3.delete();
            if (backup2.exists()) backup2.renameTo(backup3);
            if (backup1.exists()) backup1.renameTo(backup2);
            if (toBackup.exists())
                Files.copy(Paths.get(toBackup.getPath()), backup1.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            sendStack(e);
        }
    }

    //Discord Request Processors
    public static XRequestBuffer.RequestFuture<IMessage> sendMessage(String message, IChannel channel) {
        return XRequestBuffer.request(() -> {
            IMessage error = null;
            if (message == null) {
                return error;
            }
            if (message.length() < 2000) {
                try {
                    if (channel == null) {
                        return error;
                    }
                    if (StringUtils.containsOnly(message, "\n")) {
                        return error;
                    }
                    if (message != null || !message.equals("")) {
                        return channel.sendMessage(removeMentions(message));
                    }
                } catch (MissingPermissionsException e) {
                    logger.debug("Error sending message to channel with id: " + channel.getStringID() + " on guild with id: " + channel.getGuild().getStringID() +
                            ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Missing permissions.");
                    return error;
                } catch (DiscordException e) {
                    if (e.getMessage().contains("CloudFlare")) {
                        return sendMessage(message, channel).get();
                    } else {
                        sendStack(e);
                        logger.error(message);
                        return error;
                    }
                }
            } else {
                logger.debug("Message to be sent to channel with id: " + channel.getStringID() + "on guild with id: " + channel.getGuild().getStringID() +
                        ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Message to large.");
                return error;
            }
            return error;
        });
    }

    public static XRequestBuffer.RequestFuture<Boolean> sendFile(String message, File file, IChannel channel) {
        return XRequestBuffer.request(() -> {
            try {
                if (StringUtils.containsOnly(message, "\n") || (message == null) || message.equals("")) {
                    if (file != null) {
                        channel.sendFile(file);
                    } else {
                        logger.debug("Error sending File to channel with id: " + channel.getStringID() + " on guild with id: " + channel.getGuild().getStringID() +
                                ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: No file to send");
                        return true;
                    }
                } else {
                    if (message != null) {
                        channel.sendFile(removeMentions(message), file);
                    } else {
                        sendMessage(message, channel);
                        return true;
                    }
                }
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    sendFile(message, file, channel);
                } else {
                    sendStack(e);
                    return true;
                }
            } catch (IOException e) {
                sendStack(e);
            } catch (MissingPermissionsException e) {
                sendMessage("> Could not send File, missing permissions.", channel);
                logger.debug("Error sending File to channel with id: " + channel.getStringID() + " on guild with id: " + channel.getGuild().getStringID() +
                        ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Missing permissions.");
                return true;
            }
            return false;
        });
    }

    public static IMessage sendFileURL(String message, String imageURL, IChannel channel, boolean loadMessage) {
        IMessage toDelete = null;
        if (loadMessage) {
            toDelete = sendMessage("`Loading...`", channel).get();
        }
        IMessage sentMessage = null;
        try {
            //setup for the stream
            final HttpURLConnection connection = (HttpURLConnection) new URL(imageURL).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) " + "AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
            InputStream stream = connection.getInputStream();
            sentMessage = XRequestBuffer.request(() -> {
                try {
                    //set up the file name
                    URL url = new URL(imageURL);
                    String filename = FilenameUtils.getName(url.getPath());
                    if (filename.equalsIgnoreCase("giphy.gif")) {
                        return sendMessage(message + " " + imageURL, channel).get();
                    }
                    //checks if url is valid
                    if (!isImageLink(filename)) {
                        return sendMessage(message + " " + imageURL, channel).get();
                    }
                    //sends message/files
                    if (StringUtils.containsOnly(message, "\n") || (message == null) || message.equals("") && imageURL != null) {
                        return channel.sendFile("", stream, filename);
                    } else if (message != null && !message.isEmpty() && imageURL != null) {
                        return channel.sendFile(removeMentions(message), false, stream, filename);
                    } else {
                        logger.debug("Error sending File to channel with id: " + channel.getStringID() + " on guild with id: " + channel.getGuild().getStringID() +
                                ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: No file to send");
                        return null;
                    }
                } catch (DiscordException e) {
                    if (e.getMessage().contains("CloudFlare")) {
                        return sendFileURL(message, imageURL, channel, false);
                    } else {
                        sendStack(e);
                        return null;
                    }
                } catch (MalformedURLException e) {
                    return sendMessage(message + " " + imageURL, channel).get();
                } catch (IOException e) {
                    sendStack(e);
                    return null;
                } catch (MissingPermissionsException e) {
                    logger.debug("Error sending File to channel with id: " + channel.getStringID() + " on guild with id: " + channel.getGuild().getStringID() +
                            ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Missing permissions.");
                    return sendMessage(message + " <" + imageURL + ">", channel).get();
                }
            }).get();
            stream.close();
        } catch (MalformedURLException e) {
            sendStack(e);
        } catch (SSLHandshakeException e) {
            sendMessage("> Could not get image from website, invalid SSL certificate.", channel);
        } catch (IOException e) {
            sendStack(e);
        }
        if (loadMessage && toDelete != null) {
            deleteMessage(toDelete);
        }
        return sentMessage;
    }

    public static IMessage sendDMEmbed(String message, XEmbedBuilder embed, String userID) {
        IChannel channel = Globals.getClient().getOrCreatePMChannel(Globals.getClient().getUserByID(userID));
        if (channel != null) {
            return sendEmbedMessage(message, embed, channel).get();
        } else {
            return null;
        }
    }

    public static XRequestBuffer.RequestFuture<IMessage> sendEmbedMessage(String message, XEmbedBuilder builder, IChannel channel) {

        //removal of @everyone and @here Mentions.
        EmbedObject embed = builder.build();
        return XRequestBuffer.request(() -> {
            try {
                String iMessage = message;
                if (iMessage == null) {
                    iMessage = "";
                }
                return channel.sendMessage(iMessage, builder.build(), false);
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    sendEmbedMessage(message, builder, channel);
                } else {
                    sendStack(e);
                    return null;
                }
            } catch (MissingPermissionsException e) {
                logger.debug("Error sending File to channel with id: " + channel.getStringID() + " on guild with id: " + channel.getGuild().getStringID() +
                        ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Missing permissions.");
                StringBuilder embedToString = new StringBuilder();
                if (embed.author != null) embedToString.append("**" + embed.author.name + "**\n");
                if (embed.title != null) embedToString.append("**" + embed.title + "**\n");
                if (embed.description != null) embedToString.append(embed.description + "\n");
                if (embed.fields != null) {
                    for (EmbedObject.EmbedFieldObject field : embed.fields) {
                        embedToString.append("**" + field.name + "**\n" + field.value + "\n");
                    }
                }
                if (embed.footer != null) embedToString.append("*" + embed.footer.text + "*");
                if (embed.image != null) embedToString.append(embed.image.url);
                return sendMessage(embedToString.toString(), channel).get();
            }
            return null;
        });
    }

    public static String removeMentions(String from) {
        return from.replaceAll("(?i)@everyone", "").replaceAll("(?i)@here", "");
    }


    public static XRequestBuffer.RequestFuture<IMessage> sendDM(String message, long userID) {
        return XRequestBuffer.request(() -> {
            try {
                IChannel channel = Globals.getClient().getOrCreatePMChannel(Globals.getClient().getUserByID(userID));
                if (message == null || message.isEmpty()) {
                    return null;
                }
                return sendMessage(message, channel).get();
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    return sendDM(message, userID).get();
                } else {
                    sendStack(e);
                    return null;
                }
            } catch (NullPointerException e) {
                logger.debug("[sendDM] " + e.getMessage());
                return null;
            }
        });
    }

    public static IMessage sendDM(String message, String userID) {
        try {
            return sendDM(message, Long.parseLong(userID)).get();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static XRequestBuffer.RequestFuture<Boolean> sendFileDM(String message, File attatchment, String userID) {
        return XRequestBuffer.request(() -> {
            try {
                IChannel channel = Globals.getClient().getOrCreatePMChannel(Globals.getClient().getUserByID(userID));
                if (message == null || message.isEmpty()) {
                    return true;
                }
                sendFile(message, attatchment, channel);
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    sendFileDM(message, attatchment, userID);
                } else {
                    sendStack(e);
                    return true;
                }
            } catch (NullPointerException e) {
                logger.debug("[sendDM] " + e.getMessage());
                return true;
            }
            return false;
        });
    }

    public static XRequestBuffer.RequestFuture<Boolean> roleManagement(IUser author, IGuild guild, long newRoleID,
                                                                       boolean isAdding) {
        return XRequestBuffer.request(() -> {
            try {
                if (isAdding) {
                    if (guild.getRoleByID(newRoleID) != null) {
                        author.addRole(guild.getRoleByID(newRoleID));
                    } else {
                        return true;
                    }
                } else {
                    if (guild.getRoleByID(newRoleID) != null) {
                        author.removeRole(guild.getRoleByID(newRoleID));
                    } else {
                        return true;
                    }
                }
            } catch (MissingPermissionsException e) {
                if (e.getMessage().contains("Edited roles hierarchy is too high.")) {
                    logger.debug("Error Editing roles of user with id: " + author.getStringID() + " on guild with id: " + guild.getStringID() +
                            ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Edited roles hierarchy is too high.");
                    return true;
                } else {
                    sendStack(e);
                    return true;
                }
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    roleManagement(author, guild, newRoleID, isAdding);
                } else {
                    sendStack(e);
                    return true;
                }
            }
            return false;
        });
    }

    public static XRequestBuffer.RequestFuture<Boolean> roleManagement(IUser author, IGuild
            guild, List<IRole> userRoles) {
        return XRequestBuffer.request(() -> {
            try {
                IRole[] roles = new IRole[userRoles.size()];
                int i = 0;
                for (IRole r : userRoles) {
                    if (r == null) {
                        logger.error("ROLE RETURNED NULL");
                    }
                    roles[i] = r;
                    i++;
                }
                guild.editUserRoles(author, roles);
                return true;
            } catch (MissingPermissionsException e) {
                if (e.getMessage().contains("hierarchy")) {
                    logger.debug("Error Editing roles of user with id: " + author.getStringID() + " on guild with id: " + guild.getStringID() +
                            ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Edited roles hierarchy is too high.");
                    return false;
                } else {
                    sendStack(e);
                    return false;
                }
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    return roleManagement(author, guild, userRoles).get();
                } else {
                    sendStack(e);
                    return false;
                }
            }
        });
    }

    public static XRequestBuffer.RequestFuture<Boolean> updateAvatar(Image avatar) {
        return XRequestBuffer.request(() -> {
            try {
                Globals.getClient().changeAvatar(avatar);
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    updateAvatar(avatar);
                } else {
                    sendStack(e);
                    return true;
                }
            }
            return false;
        });
    }

    public static XRequestBuffer.RequestFuture<Boolean> updateUsername(String botName) {
        return XRequestBuffer.request(() -> {
            try {
                Globals.getClient().changeUsername(botName);
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    updateUsername(botName);
                } else {
                    sendStack(e);
                    return true;
                }
            }
            return false;
        });
    }

    public static XRequestBuffer.RequestFuture<Boolean> deleteMessage(IMessage message) {
        return XRequestBuffer.request(() -> {
            try {
                message.delete();
            } catch (MissingPermissionsException e) {
                sendStack(e);
                return true;
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    deleteMessage(message);
                } else {
                    sendStack(e);
                    return true;
                }
            }
            return false;
        });
    }

    public static XRequestBuffer.RequestFuture<Boolean> deleteMessage(MessageHistory messages) {
        return XRequestBuffer.request(() -> {
            try {
                messages.bulkDelete();
            } catch (MissingPermissionsException e) {
                sendStack(e);
                return true;
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    deleteMessage(messages);
                } else {
                    sendStack(e);
                    return true;
                }
            }
            return false;
        });
    }

    public static XRequestBuffer.RequestFuture<Boolean> updateUserNickName(IUser author, IGuild guild, String nickname) {
        return XRequestBuffer.request(() -> {
            try {
                guild.setUserNickname(author, nickname);
            } catch (MissingPermissionsException e) {
                if (e.getMessage().toLowerCase().contains("hierarchy")) {
                    logger.debug("Could not Update Nickname. User's position in hierarchy is higher than mine.");
                } else {
                    sendStack(e);
                }
                return true;
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    updateUserNickName(author, guild, nickname);
                } else {
                    sendStack(e);
                    return true;
                }
            }
            return false;
        });
    }

    public static Color getUsersColour(IUser user, IGuild guild) {
        List<IRole> userRoles = guild.getRolesForUser(user);
        IRole topColour = null;
        String defaultColour = "0,0,0";
        for (IRole role : userRoles) {
            if (!(role.getColor().getRed() + "," + role.getColor().getGreen() + "," + role.getColor().getBlue()).equals(defaultColour)) {
                if (topColour != null) {
                    if (role.getPosition() > topColour.getPosition()) {
                        topColour = role;
                    }
                } else {
                    topColour = role;
                }
            }
        }
        if (topColour != null) {
            return topColour.getColor();
        }
        return null;
    }

    public static Color getUsersColour(List<IRole> userRoles, IGuild guild) {
        IRole topColour = null;
        String defaultColour = "0,0,0";
        for (IRole role : userRoles) {
            if (!(role.getColor().getRed() + "," + role.getColor().getGreen() + "," + role.getColor().getBlue()).equals(defaultColour)) {
                if (topColour != null) {
                    if (role.getPosition() > topColour.getPosition()) {
                        topColour = role;
                    }
                } else {
                    topColour = role;
                }
            }
        }
        if (topColour != null) {
            return topColour.getColor();
        }
        return null;
    }

    //Time Utils
    public static String formatTimeSeconds(long timeMillis) {
        long second = (timeMillis) % 60;
        long minute = (timeMillis / 60) % 60;
        long hour = (timeMillis / (60 * 60)) % 24;
        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        return time;
    }

    public static Boolean testModifier(String modifier) {
        switch (modifier.toLowerCase()) {
            case "+":
                return true;
            case "-":
                return false;
            case "add":
                return true;
            case "del":
                return false;
            default:
                return null;
        }
    }

    public static boolean canBypass(IUser author, IGuild guild, boolean logging) {
        if (author.getStringID().equals(Globals.creatorID)) {
            if (logging) {
                logger.trace("User is Creator, BYPASSING.");
            }
            return true;
        }
        if (guild == null) {
            return false;
        }
        if (author.getStringID().equals(guild.getOwnerID())) {
            if (logging) {
                logger.trace("User is Guild Owner, GUILD : \"" + guild.getStringID() + "\", BYPASSING.");
            }
            return true;
        }
        if (author.getPermissionsForGuild(guild).contains(Permissions.ADMINISTRATOR)) {
            return true;
        }
        return false;
    }

    public static boolean canBypass(IUser author, IGuild guild) {
        return canBypass(author, guild, true);
    }

    public static String getMentionUserID(String content) {
        if (content.contains("<@")) {
            String userID = StringUtils.substringBetween(content, "<@!", ">");
            if (userID == null) {
                userID = StringUtils.substringBetween(content, "<@", ">");
            }
            IUser user = Globals.getClient().getUserByID(userID);
            if (user != null) {
                return userID;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String convertMentionToText(String from) {
        String last;
        do {
            last = from;
            if (from.contains("<@") || from.contains("<!@")) {
                String userID = getMentionUserID(from);
                if (userID != null) {
                    IUser mentioned = Globals.getClient().getUserByID(userID);
                    from = from.replace("<@!" + userID + ">", mentioned.getName() + "#" + mentioned.getDiscriminator());
                    from = from.replace("<@" + userID + ">", mentioned.getName() + "#" + mentioned.getDiscriminator());
                }
            }
        } while (!last.equals(from));
        return from;
    }

    public static void listFormatterEmbed(String title, XEmbedBuilder builder, List<String> list,
                                          boolean horizontal) {
        String formattedList = listFormatter(list, horizontal);
        if (title == null || title.isEmpty()) {
            title = Command.spacer;
        }
        if (formattedList.isEmpty()) {
            builder.appendField(title, Command.spacer, false);
            return;
        }
        if (horizontal) {
            builder.appendField(title, "`" + formattedList + "`", false);
        } else {
            builder.appendField(title, "```\n" + formattedList + "```", false);
        }
    }

    public static void listFormatterEmbed(String title, EmbedBuilder builder, List<String> list,
                                          boolean horizontal, String suffix) {
        String formattedList = listFormatter(list, horizontal);
        if (title == null || title.isEmpty()) {
            title = Command.spacer;
        }
        if (formattedList.isEmpty()) {
            builder.appendField(title, Command.spacer + suffix, false);
            return;
        }
        if (horizontal) {
            builder.appendField(title, "`" + formattedList + "`\n" + suffix, false);
        } else {
            builder.appendField(title, "```\n" + formattedList + "```\n" + suffix, false);
        }
    }

    public static String listFormatter(List<String> list, boolean horizontal) {
        StringBuilder formattedList = new StringBuilder();
        if (list.size() == 0) {
            return "";
        }
        if (horizontal) {
            for (String s : list) {
                formattedList.append(s + ", ");
            }
            formattedList.delete(formattedList.length() - 2, formattedList.length());
            formattedList.append(".");
            return formattedList.toString();
        } else {
            for (String s : list) {
                formattedList.append(s + "\n");
            }
            return formattedList.toString();
        }
    }

    public static List<IRole> getRolesByName(IGuild guild, String name) {
        List<IRole> roles = guild.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
        return roles;
    }

    public static String loggingFormatter(CommandObject commandObject, String type) {
        return "TYPE: \"" + type + "\", GUILD : \"" + commandObject.guild.stringID +
                "\", CHANNEL : \"" + commandObject.channel.stringID +
                "\", USER : \"" + commandObject.user.stringID +
                "\", MESSAGE : \"" + commandObject.message.stringID +
                "\".";
    }

    public static void sendGlobalAdminLogging(Command command, String args, CommandObject commandObject) {
        for (GuildObject c : Globals.getGuilds()) {
            StringBuilder message = new StringBuilder("***GLOBAL LOGGING***\n> **@" + commandObject.user.username + "** Has Used Command `" + command.names()[0] + "`");
            IChannel channel = null;
            if (!(args == null || args.isEmpty())) {
                message.append(" with args: `" + args + "`");
            }
            if (c.config.getChannelIDsByType(Command.CHANNEL_SERVER_LOG) != null) {
                channel = commandObject.client.get().getChannelByID(c.config.getChannelIDsByType(Command.CHANNEL_SERVER_LOG).get(0));
            }
            if (c.config.getChannelIDsByType(Command.CHANNEL_ADMIN_LOG) != null) {
                channel = commandObject.client.get().getChannelByID(c.config.getChannelIDsByType(Command.CHANNEL_ADMIN_LOG).get(0));
            }
            if (channel != null) {
                sendMessage(message.toString(), channel);
            }
        }
    }

    public static String formatTimeDifference(long difference) {
        String formatted = "";
        try {
            long days = TimeUnit.SECONDS.toDays(difference);
            long hours = TimeUnit.SECONDS.toHours(difference);
            hours -= days * 24;
            long mins = TimeUnit.SECONDS.toMinutes(difference);
            mins -= (days * 24 + hours) * 60;

            if (days > 0) {
                if (days > 1) {
                    formatted = formatted + days + " days, ";
                } else {
                    formatted = formatted + days + " day, ";
                }
            }
            if (hours > 0) {
                if (hours > 1) {
                    formatted = formatted + hours + " hours and ";
                } else {
                    formatted = formatted + hours + " hour and ";
                }
            }
            if (mins > 1) {
                formatted = formatted + mins + " minutes ago";
            } else if (mins != 0) {
                formatted = formatted + mins + " minute ago";
            }
            if (difference < 60) {
                formatted = "less than a minute ago";
            }
        } catch (NoSuchElementException e) {
            logger.error("Error getting Edited Message Timestamp.");
        }
        return formatted;
    }

    public static String removeFun(String from) {
        String last;
        boolean exit;
        do {
            last = from;
            exit = false;
            if (from.contains("***")) {
                from = replaceFun(from, "***");
                exit = true;
            }
            if (from.contains("**") && !exit) {
                from = replaceFun(from, "**");
                exit = true;
            }
            if (from.contains("*") && !exit) {
                from = replaceFun(from, "*");
            }
            exit = false;
            if (from.contains("```")) {
                from = replaceFun(from, "```");
                exit = true;
            }
            if (from.contains("`") && !exit) {
                from = replaceFun(from, "`");
            }
            exit = false;
            if (from.contains("~~")) {
                from = replaceFun(from, "~~");
            }
            if (from.contains("__")) {
                from = replaceFun(from, "__");
                exit = true;
            }
            if (from.contains("_") && !exit) {
                from = replaceFun(from, "_");
            }
        } while (last != from);
        return from;
    }

    public static String replaceFun(String from, String fun) {
        String noFun = StringUtils.substringBetween(from, fun, fun);
        if (noFun != null) {
            from = from.replace(fun + noFun + fun, noFun);
        }
        return from;
    }

    public static boolean isImageLink(String link) {
        if (!checkURL(link)) {
            return false;
        }
        List<String> suffixes = new ArrayList<String>() {{
            add(".png");
            add(".gif");
            add(".jpg");
            add(".webp");
        }};
        if (link.contains("\n") || link.contains(" ")) {
            return false;
        }
        for (String s : suffixes) {
            if (link.toLowerCase().endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean muteUser(long guildID, String userID, boolean isMuting) {
        GuildObject content = Globals.getGuildContent(guildID);
        IUser user = Globals.getClient().getUserByID(userID);
        IGuild guild = Globals.getClient().getGuildByID(guildID);
        IRole mutedRole = Globals.client.getRoleByID(content.config.getMutedRoleID());
        List<IRole> oldRoles = user.getRolesForGuild(guild);
        if (mutedRole != null) {
            roleManagement(Globals.getClient().getUserByID(userID), Globals.client.getGuildByID(guildID), mutedRole.getLongID(), isMuting);
            List<IRole> newRoles = user.getRolesForGuild(guild);
            Globals.getClient().getDispatcher().dispatch(new UserRoleUpdateEvent(guild, user, oldRoles, newRoles));
            return true;
        }
        return false;
    }

    public static boolean testUserHierarchy(IUser higherUser, IUser lowerUser, IGuild guild) {
        List<IRole> lowerRoles = lowerUser.getRolesForGuild(guild);
        List<IRole> higherRoles = higherUser.getRolesForGuild(guild);
        IRole topRole = null;
        int topRolePos = 0;
        for (IRole role : higherRoles) {
            if (topRole == null) {
                topRole = role;
                topRolePos = role.getPosition();
            } else {
                if (role.getPosition() > topRolePos) {
                    topRole = role;
                    topRolePos = role.getPosition();
                }
            }
        }
        for (IRole role : lowerRoles) {
            if (role.getPosition() > topRolePos) {
                return false;
            }
        }
        return true;
    }

    public static boolean testUserHierarchy(IUser author, IRole toTest, IGuild guild) {
        boolean roleIsLower = false;
        for (IRole r : author.getRolesForGuild(guild)) {
            if (toTest.getPosition() < r.getPosition()) {
                roleIsLower = true;
            }
        }
        return roleIsLower;
    }

    public static long textToSeconds(String time) {
        try {
            String sub = time.substring(0, time.length() - 1);
            long timeSecs = Long.parseLong(sub);
            if (time.toLowerCase().endsWith("s")) {
                return timeSecs;
            } else if (time.toLowerCase().endsWith("m")) {
                return timeSecs * 60;
            } else if (time.toLowerCase().endsWith("h")) {
                return timeSecs * 60 * 60;
            } else if (time.toLowerCase().endsWith("d")) {
                return timeSecs * 60 * 60 * 24;
            } else {
                timeSecs = Long.parseLong(time);
                return timeSecs;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void sendLog(String content, GuildConfig config, boolean isAdmin) {
        IChannel logChannel;
        if (isAdmin) {
            if (config.getChannelIDsByType(Command.CHANNEL_ADMIN_LOG) == null) {
                return;
            }
            logChannel = Globals.getClient().getChannelByID(config.getChannelIDsByType(Command.CHANNEL_ADMIN_LOG).get(0));
        } else {
            if (config.getChannelIDsByType(Command.CHANNEL_SERVER_LOG) == null) {
                return;
            }
            logChannel = Globals.getClient().getChannelByID(config.getChannelIDsByType(Command.CHANNEL_SERVER_LOG).get(0));
        }
        if (logChannel == null) {
            return;
        } else {
            sendMessage(content, logChannel);
        }
    }

    public static String unFormatMentions(IMessage message) {
        String from = message.getContent();
        for (IUser user : message.getMentions()) {
            if (user == null) {
                break;
            }
            String mention = "<@" + user.getStringID() + ">";
            String mentionNic = "<@!" + user.getStringID() + ">";
            from = from.replace(mention, "__@" + user.getDisplayName(message.getGuild()) + "__");
            from = from.replace(mentionNic, "__@" + user.getDisplayName(message.getGuild()) + "__");
        }
        for (IRole role : message.getRoleMentions()) {
            String roleMention = "<@&" + role.getStringID() + ">";
            from = from.replace(roleMention, "__**@" + role.getName() + "**__");
        }
        return from;
    }

    public static String formatTimestamp(ZonedDateTime time) {
        StringBuilder content = new StringBuilder();
        content.append(time.getYear());
        content.append("/" + time.getMonthValue());
        content.append("/" + time.getDayOfMonth());
        content.append(" - " + time.getHour());
        content.append(":" + time.getMinute());
        content.append(":" + time.getSecond());
        return content.toString();
    }

    public static boolean checkURL(String args) {
        List<String> blacklist = Globals.getBlacklistedURls();
        for (String s : blacklist) {
            SplitFirstObject firstWord = new SplitFirstObject(s);
            if (!firstWord.getFirstWord().startsWith("//")) {
                if (firstWord.getFirstWord() != null && firstWord.getFirstWord().length() != 0) {
                    if (args.toLowerCase().contains(firstWord.getFirstWord().toLowerCase())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void sortRewards(List<RewardRoleObject> rewardRoles) {
        Collections.sort(rewardRoles, (o1, o2) -> {
            if (o1.getLevel() < o2.getLevel()) {
                return -1;
            } else if (o1.getLevel() > o2.getLevel()) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    public static void sortUserObjects(List<ProfileObject> users, boolean sortAsc) {
        if (sortAsc) {
            Collections.sort(users, (o1, o2) -> {
                if (o1.getXP() < o2.getXP()) {
                    return -1;
                } else if (o1.getXP() > o2.getXP()) {
                    return 1;
                } else {
                    return 0;
                }
            });
        } else {
            Collections.sort(users, (o1, o2) -> {
                if (o1.getXP() > o2.getXP()) {
                    return -1;
                } else if (o1.getXP() < o2.getXP()) {
                    return 1;
                } else {
                    return 0;
                }
            });
        }
    }

    public static long newDailyMsgUID(DailyMessages data) {
        long result;
        Random random = new Random();
        List<Long> uIDs = new ArrayList<>();
        for (DailyUserMessageObject d : data.getMessages()) {
            if (d.getUID() != -1) {
                uIDs.add(d.getUID());
            }
        }
        for (QueueObject o : data.getQueue()) {
            uIDs.add(o.getuID());
        }
        result = random.nextInt(9000) + 1000;
        while (uIDs.contains(result)) {
            result = random.nextInt(9000) + 1000;
        }
        return result;
    }

    public static List<String> getChannelMentions(ArrayList<String> channelIDs, CommandObject command) {
        List<String> channelNames = new ArrayList<>();
        if (channelIDs != null) {
            for (String s : channelIDs) {
                IChannel channel = command.guild.get().getChannelByID(s);
                if (channel != null) {
                    channelNames.add(channel.mention());
                }
            }
        }
        return channelNames;
    }

    public static void sendStack(Exception e) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        System.err.printf("%02d:%02d:%02d.%03d ", now.getHour(), now.getMinute(), now.getSecond(), now.getNano() / 1000000);
        System.err.print("[" + Thread.currentThread().getName() + "] ERROR ");
        e.printStackTrace();
    }

    public static List<Command> getCommandsByType(List<Command> commands, CommandObject commandObject, String type, boolean testPerms) {
        List<Command> toReturn = new ArrayList<>();
        for (Command c : commands) {
            String ctype = c.type();
            if (c.channel() != null && c.channel().equalsIgnoreCase(Command.CHANNEL_DM)) {
                ctype = Command.TYPE_DM;
            }
            boolean isDualType = false;
            if (c.dualType() != null && c.dualType().equalsIgnoreCase(type)) {
                isDualType = true;
            }
            if (ctype.equalsIgnoreCase(type) || isDualType) {
                if (testPerms) {
                    if (c.type().equalsIgnoreCase(Command.TYPE_CREATOR) && !commandObject.user.get().equals(commandObject.client.creator)) {
                        //do nothing
                    } else if (isDualType && testForPerms(c.dualPerms(), commandObject.user.get(), commandObject.guild.get())) {
                        toReturn.add(c);
                    } else if (testForPerms(c.perms(), commandObject.user.get(), commandObject.guild.get())) {
                        toReturn.add(c);
                    }
                } else {
                    toReturn.add(c);
                }
            }
        }
        toReturn.sort(Comparator.comparing(o -> o.names()[0]));
        return toReturn;
    }

    public static List<IChannel> getVisibleChannels(List<IChannel> channels, UserObject user) {
        List<IChannel> newSet = new ArrayList<>();
        for (IChannel c : channels) {
            if (c.getModifiedPermissions(user.get()).contains(Permissions.READ_MESSAGES)
                    && c.getModifiedPermissions(user.get()).contains(Permissions.SEND_MESSAGES)) {
                newSet.add(c);
            }
        }
        return newSet;
    }

    public static List<String> getChannelMentions(List<IChannel> channels) {
        List<String> mentions = new ArrayList<>();
        for (IChannel c : channels) {
            mentions.add(c.mention());
        }
        return mentions;
    }

    public static UserObject getUser(CommandObject command, String args, boolean doContains) {
        if (args != null && !args.isEmpty()) {
            IUser user = null;
            IUser conUser = null;
            String toTest;
            if (args.split(" ").length != 1) {
                toTest = escapeRegex(args);
            } else {
                toTest = escapeRegex(args).replace("_", "[_| ]");
            }
            for (IUser u : command.guild.get().getUsers()) {
                if (user != null) {
                    break;
                }
                try {
                    if ((u.getName() + "#" + u.getDiscriminator()).matches("(?i)" + toTest)) {
                        user = u;
                    }
                    if (u.getName().matches("(?i)" + toTest)) {
                        user = u;
                    }
                    String displayName = u.getDisplayName(command.guild.get());
                    if (displayName.matches("(?i)" + toTest)) {
                        user = u;
                    }
                    if (doContains && conUser == null) {
                        if (u.getName().matches("(?i).*" + toTest + ".*")) {
                            conUser = u;
                        }
                        if (displayName.matches("(?i).*" + toTest + ".*")) {
                            conUser = u;
                        }
                    }
                } catch (PatternSyntaxException e) {
                    //continue.
                }
            }
            try {
                long uID = Long.parseLong(args);
                user = command.client.get().getUserByID(uID);
            } catch (NumberFormatException e) {
                if (command.message.get().getMentions().size() > 0) {
                    user = command.message.get().getMentions().get(0);
                }
            }
            if (user == null && doContains) {
                user = conUser;
            }
            if (user != null) {
                UserObject userObject = new UserObject(user, command.guild);
                return userObject;
            }
        }
        return null;
    }

    private static String escapeRegex(String args) {
        //[\^$.|?*+(){}
        args = args.replace("\\", "\\u005C");
        args = args.replace("[", "\\u005B");
        args = args.replace("^", "\\u005E");
        args = args.replace("$", "\\u0024");
        args = args.replace(".", "\\u002E");
        args = args.replace("|", "\\u007C");
        args = args.replace("?", "\\u003F");
        args = args.replace("*", "\\u002A");
        args = args.replace("+", "\\u002B");
        args = args.replace("(", "\\u0028");
        args = args.replace(")", "\\u0029");
        args = args.replace("{", "\\u007B");
        args = args.replace("}", "\\u007D");
        return args;
    }
}