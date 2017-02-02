package Main;

import Commands.Command;
import Commands.CommandObject;
import Handlers.FileHandler;
import Objects.BlackListObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.*;
import sx.blah.discord.util.Image;

import javax.annotation.Nonnull;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class Utility {

    static FileHandler handler = new FileHandler();

    //Logger
    final static Logger logger = LoggerFactory.getLogger(Utility.class);

    //Discord Utils
    public static String getRoleIDFromName(String roleName, IGuild guild) {
        String roleID = null;
        List<IRole> guildRoles = guild.getRoles();
        for (IRole r : guildRoles) {
            if (r.getName().equalsIgnoreCase(roleName)) {
                roleID = r.getID();
            }
        }
        return roleID;
    }

    public static boolean testForPerms(Permissions[] perms, IUser author, IGuild guild) {
        Permissions[] compiledPerms = new Permissions[perms.length];
        int permsIndex = 0;
        for (Permissions aP : perms) {
            for (IRole r : author.getRolesForGuild(guild)) {
                for (Permissions p : r.getPermissions()) {
                    if (aP.equals(p)) {
                        compiledPerms[permsIndex] = p;
                    }
                }
            }
            permsIndex++;
        }
        if (Arrays.equals(compiledPerms, perms)) {
            return true;
        } else return false;
    }

    //Command Utils
    public static String getCommandInfo(Command command,CommandObject commandObject) {
        return ">> **" + commandObject.guildConfig.getPrefixCommand() + command.names()[0] + " " + command.usage() + "** <<";
    }

    public static String checkBlacklist(String message, ArrayList<BlackListObject> blacklist) {
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

    @Nonnull
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
                e.printStackTrace();
            }
        }
        if (counter > 1) {
            logger.debug(filePath + " File for guild with id " + guildID + " took " + counter + " tries to Init");
        }
        return object;
    }

    public static void flushFile(String guildID, String filePath, Object object, boolean wasInit) {
        if (wasInit) {
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
            e.printStackTrace();
        }
    }

    public static void backupConfigFile(String fileConfig) {
        try {
            File backup1 = new File(Constants.FILE_CONFIG_BACKUP + 1);
            File backup2 = new File(Constants.FILE_CONFIG_BACKUP + 2);
            File backup3 = new File(Constants.FILE_CONFIG_BACKUP + 3);
            File toBackup = new File(Constants.FILE_CONFIG);
            if (backup3.exists()) backup3.delete();
            if (backup2.exists()) backup2.renameTo(backup3);
            if (backup1.exists()) backup1.renameTo(backup2);
            if (toBackup.exists())
                Files.copy(Paths.get(toBackup.getPath()), backup1.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Discord Request Processors
    public static RequestBuffer.RequestFuture<Boolean> sendMessage(String message, IChannel channel) {
        return RequestBuffer.request(() -> {
            if (message == null) {
                return false;
            }
            if (message.length() < 2000) {
                try {
                    if (StringUtils.containsOnly(message, "\n")) {
                        return true;
                    }
                    if (message != null || !message.equals("")) {
                        channel.sendMessage(message);
                    }
                } catch (MissingPermissionsException e) {
                    logger.debug("Error sending message to channel with id: " + channel.getID() + " on guild with id: " + channel.getGuild().getID() +
                            ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Missing permissions.");
                    return true;
                } catch (DiscordException e) {
                    if (e.getMessage().contains("CloudFlare")) {
                        sendMessage(message, channel);
                    } else {
                        e.printStackTrace();
                        return true;
                    }
                }
            } else {
                logger.debug("Message to be sent to channel with id: " + channel.getID() + "on guild with id: " + channel.getGuild().getID() +
                        ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Message to large.");
                return true;
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> sendFile(String message, File file, IChannel channel) {
        return RequestBuffer.request(() -> {
            try {
                if (StringUtils.containsOnly(message, "\n") || (message == null) || message.equals("")) {
                    if (message != null) {
                        channel.sendFile(file);
                    } else {
                        logger.debug("Error sending File to channel with id: " + channel.getID() + " on guild with id: " + channel.getGuild().getID() +
                                ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: No file to send");
                        return true;
                    }
                } else {
                    if (message != null) {
                        channel.sendFile(message, file);
                    } else {
                        sendMessage(message, channel);
                        return true;
                    }
                }
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    sendFile(message, file, channel);
                } else {
                    e.printStackTrace();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MissingPermissionsException e) {
                logger.debug("Error sending File to channel with id: " + channel.getID() + " on guild with id: " + channel.getGuild().getID() +
                        ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Missing permissions.");
                return true;
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> sendFile(String message, String imageURL, IChannel channel) {
        return RequestBuffer.request(() -> {
            String messageID = "";
            try {
                messageID = channel.sendMessage("`Loading...`").getID();
                final HttpURLConnection connection = (HttpURLConnection) new URL(imageURL).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) " + "AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
                InputStream stream = connection.getInputStream();
                String[] urlSplit = imageURL.split(Pattern.quote("."));
                String suffix = "." + urlSplit[urlSplit.length-1];
                if (!suffix.contains(".png") && !suffix.contains(".jpg") && !suffix.contains(".gif") && !suffix.contains(".webp")){
                    sendMessage(message + " " + imageURL, channel);
                    return true;
                }
                if (StringUtils.containsOnly(message, "\n") || (message == null) || message.equals("")) {
                    if (imageURL != null) {
                        channel.sendFile("", stream, suffix);
                    } else {
                        logger.debug("Error sending File to channel with id: " + channel.getID() + " on guild with id: " + channel.getGuild().getID() +
                                ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: No file to send");
                        return true;
                    }
                } else {
                    if (imageURL != null) {
                        channel.sendFile(message, true, stream, suffix);
                    } else {
                        sendMessage(message, channel);
                        return true;
                    }
                }
                Globals.getClient().getMessageByID(messageID).delete();
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    sendFile(message, imageURL, channel);
                    deleteMessage(Globals.getClient().getMessageByID(messageID));
                } else {
                    e.printStackTrace();
                    return true;
                }
            } catch (MalformedURLException e) {
                sendMessage(message + " " + imageURL, channel);
                deleteMessage(Globals.getClient().getMessageByID(messageID));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MissingPermissionsException e) {
                sendMessage(message + " <" + imageURL+">", channel);
                deleteMessage(Globals.getClient().getMessageByID(messageID));
                logger.debug("Error sending File to channel with id: " + channel.getID() + " on guild with id: " + channel.getGuild().getID() +
                        ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Missing permissions.");
                return true;
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> sendEmbededMessage(String message, EmbedObject embed, IChannel channel) {
        return RequestBuffer.request(() -> {
            try {
                String iMessage = message;
                if (iMessage == null) {
                    iMessage = "";
                }
                channel.sendMessage(iMessage, embed, false);
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    sendMessage(message, channel);
                } else {
                    e.printStackTrace();
                    return true;
                }
            } catch (MissingPermissionsException e) {
                logger.debug("Error sending File to channel with id: " + channel.getID() + " on guild with id: " + channel.getGuild().getID() +
                        ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Missing permissions.");
                StringBuilder embedtoString = new StringBuilder();
                if (embed.author != null) embedtoString.append("**" + embed.author.name + "**\n");
                if (embed.title != null) embedtoString.append("**" + embed.title + "**\n");
                if (embed.description != null) embedtoString.append(embed.description + "\n");
                if (embed.fields != null) {
                    for (EmbedObject.EmbedFieldObject field : embed.fields) {
                        embedtoString.append("**" + field.name + "**\n" + field.value + "\n");
                    }
                }
                if (embed.footer != null) embedtoString.append("*" + embed.footer.text + "*");
                if (embed.image != null) embedtoString.append(embed.image.url);
                sendMessage(embedtoString.toString(), channel);
                return true;
            }
            return false;
        });
    }


    public static RequestBuffer.RequestFuture<Boolean> sendDM(String message, String userID) {
        return RequestBuffer.request(() -> {
            try {
                IChannel channel = Globals.getClient().getOrCreatePMChannel(Globals.getClient().getUserByID(userID));
                sendMessage(message, channel);
            } catch (DiscordException e) {
                e.printStackTrace();
                return true;
            } catch (NullPointerException e) {
                logger.debug("[sendDM] " + e.getMessage());
                return true;
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> roleManagement(IUser author, IGuild guild, String newRoleID, boolean isAdding) {
        return RequestBuffer.request(() -> {
            try {
                if (isAdding) {
                    if (guild.getRoleByID(newRoleID) != null) {
                        author.addRole(guild.getRoleByID(newRoleID));
                    }
                } else {
                    if (guild.getRoleByID(newRoleID) != null) {
                        author.removeRole(guild.getRoleByID(newRoleID));
                    }
                }
            } catch (MissingPermissionsException e) {
                if (e.getMessage().contains("Edited roles hierarchy is too high.")) {
                    logger.debug("Error Editing roles of user with id: " + author.getID() + " on guild with id: " + guild.getID() +
                            ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Edited roles hierarchy is too high.");
                    return true;
                } else {
                    e.printStackTrace();
                    return true;
                }
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    roleManagement(author, guild, newRoleID, isAdding);
                } else {
                    e.printStackTrace();
                    return true;
                }
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> roleManagement(IUser author, IGuild guild, List<IRole> userRoles) {
        return RequestBuffer.request(() -> {
            try {
                IRole[] roles = new IRole[userRoles.size()];
                int i = 0;
                for (IRole r : userRoles) {
                    roles[i] = r;
                    i++;
                }
                guild.editUserRoles(author, roles);
            } catch (MissingPermissionsException e) {
                if (e.getMessage().contains("Edited roles hierarchy is too high.")) {
                    logger.debug("Error Editing roles of user with id: " + author.getID() + " on guild with id: " + guild.getID() +
                            ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Edited roles hierarchy is too high.");
                    return true;
                } else {
                    e.printStackTrace();
                    return true;
                }
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    roleManagement(author, guild, userRoles);
                } else {
                    e.printStackTrace();
                    return true;
                }
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> updateAvatar(Image avatar) {
        return RequestBuffer.request(() -> {
            try {
                Globals.getClient().changeAvatar(avatar);
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    updateAvatar(avatar);
                } else {
                    e.printStackTrace();
                    return true;
                }
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> updateUsername(String botName) {
        return RequestBuffer.request(() -> {
            try {
                Globals.getClient().changeUsername(botName);
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    updateUsername(botName);
                } else {
                    e.printStackTrace();
                    return true;
                }
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> deleteMessage(IMessage message) {
        return RequestBuffer.request(() -> {
            try {
                message.delete();
            } catch (MissingPermissionsException e) {
                e.printStackTrace();
                return true;
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    deleteMessage(message);
                } else {
                    e.printStackTrace();
                    return true;
                }
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> deleteMessage(MessageList messages) {
        return RequestBuffer.request(() -> {
            try {
                messages.bulkDelete(messages);
            } catch (MissingPermissionsException e) {
                e.printStackTrace();
                return true;
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    deleteMessage(messages);
                } else {
                    e.printStackTrace();
                    return true;
                }
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> updateUserNickName(IUser author, IGuild guild, String nickname) {
        return RequestBuffer.request(() -> {
            try {
                guild.setUserNickname(author, nickname);
            } catch (MissingPermissionsException e) {
                if (e.getMessage().toLowerCase().contains("hierarchy")) {
                    logger.debug("Could not Update Nickname. User's position in hierarchy is higher than mine.");
                } else {
                    e.printStackTrace();
                }
                return true;
            } catch (DiscordException e) {
                if (e.getMessage().contains("CloudFlare")) {
                    updateUserNickName(author, guild, nickname);
                } else {
                    e.printStackTrace();
                    return true;
                }
            }
            return false;
        });
    }

    public static Color getUsersColour(IUser user, IGuild guild) {
        List<IRole> botRoles = guild.getRolesForUser(Globals.getClient().getOurUser());
        IRole topColour = null;
        String defaultColour = "0,0,0";
        for (IRole role : botRoles) {
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

    public static boolean canBypass(IUser author, IGuild guild) {
        if (author.getID().equals(Globals.creatorID)) {
            return true;
        }
        if (author.getID().equals(guild.getOwnerID())) {
            return true;
        }
        return testForPerms(new Permissions[]{Permissions.ADMINISTRATOR}, author, guild);
    }
}
