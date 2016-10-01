package Main;

import Annotations.CommandAnnotation;
import Handlers.FileHandler;
import Handlers.MessageHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class Utility {

    static FileHandler handler = new FileHandler();

    //Logger
    final static Logger logger = LoggerFactory.getLogger(Utility.class);

    //Discord Role Utils
    public static String getRoleIDFromName(String roleName, IGuild guild) {
        String roleID = Constants.NULL_VARIABLE;
        List<IRole> guildRoles = guild.getRoles();
        for (IRole r : guildRoles) {
            if (r.getName().equalsIgnoreCase(roleName)) {
                roleID = r.getID();
            }
        }
        return roleID;
    }

    //Command Utils
    public static String getCommandInfo(CommandAnnotation annotation) {
        StringBuilder builder = new StringBuilder();
        builder.append("`" + Constants.PREFIX_COMMAND + annotation.name() + " " + annotation.usage() + "`");
        return builder.toString();
    }

    public static String getCommandInfo(String methodName) {
        try {
            Method method = MessageHandler.class.getMethod(methodName);
            return getCommandInfo(method.getAnnotation(CommandAnnotation.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String tagSystem(String contents, IMessage message, String args) {
        String response = contents;
        String tagRandom;
        String tagRegex;
        String prefixRandom = "#random#{";
        String prefixRegex = "#regex#{";
        String lastAttempt;
        response = response.replace("#args#", args);
        try {
            if (response.contains(prefixRandom)) {
                do {
                    lastAttempt = response;
                    tagRandom = StringUtils.substringBetween(response, prefixRandom, "}");
                    if (tagRandom != null) {
                        ArrayList<String> splitRandom = new ArrayList<>(Arrays.asList(tagRandom.split(";")));
                        Random random = new Random();
                        String toRegex = "#random#{" + tagRandom + "}";
                        response = response.replaceFirst(Pattern.quote(toRegex), splitRandom.get(random.nextInt(splitRandom.size())));
                    }
                } while (StringUtils.countMatches(response, prefixRandom) > 0 && (!lastAttempt.equals(response)));
            }
            if (response.contains(prefixRegex)) {
                do {
                    lastAttempt = response;
                    tagRegex = StringUtils.substringBetween(response, prefixRegex, "}");
                    if (tagRegex != null) {
                        ArrayList<String> splitRegex = new ArrayList<>(Arrays.asList(tagRegex.split(";")));
                        String toRegex = prefixRegex + tagRegex + "}";
                        if (splitRegex.size() == 2) {
                            response = response.replace(toRegex, "");
                            response = response.replace(splitRegex.get(0), splitRegex.get(1));
                        } else {
                            response = response.replace(tagRegex, "#ERROR#");
                        }
                    }
                } while (StringUtils.countMatches(response, prefixRegex) > 0 && (!lastAttempt.equals(response)));
            }
        } catch (PatternSyntaxException ex) {
            return "> An Error occurred while attempting to run this command.";
        }
        response = response.replace("#author#", message.getAuthor().getDisplayName(message.getGuild()));
        response = response.replace("#channel#", message.getChannel().mention());
        response = response.replace("#guild#", message.getGuild().getName());
        response = response.replace("#authorID#", message.getAuthor().getID());
        response = response.replace("#channelID#", message.getChannel().getID());
        response = response.replace("#guildID#", message.getGuild().getID());
        response = response.replace("@everyone,", "**[REDACTED]**");
        response = response.replace("@here", "**[REDACTED]**");
        return response;
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
        return handler.readfromJson(Utility.getFilePath(guildID, filePath), objClass);
    }

    public static void flushFile(String guildID, String filePath, Object object, boolean wasInit) {
        if (wasInit) {
            handler.writetoJson(Utility.getFilePath(guildID, filePath), object);
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

    //Discord Request Processors
    public static RequestBuffer.RequestFuture<Boolean> sendMessage(String message, IChannel channel) {
        return RequestBuffer.request(() -> {
            if (message.length() < 2000) {
                try {
                    channel.sendMessage(message);
                } catch (MissingPermissionsException e) {
                    logger.error("Error sending message to channel with id: " + channel.getID() + " on guild with id: " + channel.getGuild().getID() +
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
                logger.error("Message to be sent to channel with id: " + channel.getID() + "on guild with id: " + channel.getGuild().getID() +
                        ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: message to large.");
                return true;
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> sendDM(String message, String userID, IDiscordClient client) {
        return RequestBuffer.request(() -> {
            try {
                IChannel channel = client.getOrCreatePMChannel(client.getUserByID(userID));
                sendMessage(message, channel);
            } catch (DiscordException e) {
                e.printStackTrace();
                return true;
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> roleManagement(IUser author, IGuild guild, String newRoleID, boolean isAdding) {
        return RequestBuffer.request(() -> {
            try {
                if (isAdding) {
                    author.addRole(guild.getRoleByID(newRoleID));
                } else {
                    author.removeRole(guild.getRoleByID(newRoleID));
                }
            } catch (MissingPermissionsException e) {
                if (e.getMessage().contains("Edited roles hierarchy is too high.")) {
                    logger.error("Error Editing roles of user with id: " + author.getID() + " on guild with id: " + guild.getID() +
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
                    logger.error("Error Editing roles of user with id: " + author.getID() + " on guild with id: " + guild.getID() +
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

    public static RequestBuffer.RequestFuture updateAvatar(Image avatar) {
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



}
