package Main;

import Annotations.CommandAnnotation;
import Handlers.FileHandler;
import POGOs.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.obj.Guild;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class Utility {

    static FileHandler handler = new FileHandler();

    final static Logger logger = LoggerFactory.getLogger(Utility.class);

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

    public static void sendMessage(String message, IChannel channel) {
        try {
            if (message.length() > 2000){
                channel.sendMessage("> Error: message size to great.");
                return;
            }
            if (message.equals("")) {
                return;
            }
            channel.sendMessage(message);
        } catch (MissingPermissionsException e) {
            e.printStackTrace();
        } catch (DiscordException | RateLimitException e) {
            logger.info(e.getMessage());
            try {
                Thread.sleep(1000);
                sendMessage(message, channel);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static String getCommandInfo(CommandAnnotation annotation) {
        StringBuilder builder = new StringBuilder();
        builder.append("**" + Constants.COMMAND_PREFIX + annotation.name() + "** `" + annotation.usage() + "`\n");
        return builder.toString();
    }

    public static String getFilePath(String guildID, String type) {
        return Constants.DIRECTORY_STORAGE + guildID + "/" + type;
    }

    public static String getFilePath(String guildID, String type, boolean isBackup) {
        return Constants.DIRECTORY_BACKUPS + guildID + "/" + type;
    }

    public static String getDirectory(String guildID) {
        return Constants.DIRECTORY_STORAGE + guildID + "/";
    }

    public static String getDirectory(String guildID,boolean isBackup) {
        return Constants.DIRECTORY_BACKUPS + guildID + "/";
    }

    public static void flushFile(String guildID, String filePath, Object object) {
        handler.writetoJson(Utility.getFilePath(guildID, Constants.FILE_GUILD_CONFIG), object);
    }

    public static void backupFile(String guildID, String type) {
        try {
            File backup1 = new File(getFilePath(guildID, type, true) + 1);
            File backup2 = new File(getFilePath(guildID, type, true) + 2);
            File backup3 = new File(getFilePath(guildID, type, true) + 3);
            File toBackup = new File(getFilePath(guildID, type));
            if (backup3.exists()) backup3.delete();
            if (backup2.exists()) backup2.renameTo(new File(getFilePath(guildID, type, true) + 3));
            if (backup1.exists()) backup1.renameTo(new File(getFilePath(guildID, type, true) + 2));
            if (toBackup.exists()) Files.copy(Paths.get(toBackup.getPath()), Paths.get(getFilePath(guildID, type, true) + 1), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
