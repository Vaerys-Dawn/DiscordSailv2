package Main;

import Annotations.CommandAnnotation;
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

import java.util.List;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class Utility {

    static FileHandler handler = new FileHandler();

    final static Logger logger = LoggerFactory.getLogger(Utility.class);

    public static String GetRoleIDFromName(String roleName, Guild guild){
        String roleID = "> Error Parsing Role Name, Role with that name not found.";
        List<IRole> guildRoles = guild.getRoles();
        for (IRole r: guildRoles){
            if (r.getName().equalsIgnoreCase(roleName)){
                roleID = r.getID();
            }
        }
        return roleID;
    }

    public static void sendMessage(String message, IChannel channel) {
        try {
            if (message.equals("")){
                return;
            }
            channel.sendMessage(message);
        } catch (MissingPermissionsException e) {
            e.printStackTrace();
        } catch (RateLimitException e) {
            e.printStackTrace();
        } catch (DiscordException e) {
            logger.info(e.getCause().getMessage());
            try {
                Thread.sleep(1000);
                sendMessage(message, channel);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static String getCommandInfo(CommandAnnotation annotation){
        StringBuilder builder = new StringBuilder();
        builder.append("**"+Constants.COMMAND_PREFIX+annotation.name() + "** `" + annotation.usage() + "`\n");
        return builder.toString();
    }

    public static String getFilePath(String guildID, String type){
        return Constants.DIRECTORY_STORAGE+guildID+"/"+type;
    }
    public static String getDirectory(String guildID){
        return Constants.DIRECTORY_STORAGE+guildID+"/";
    }

}
