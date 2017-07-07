package Interfaces;

import Commands.CommandObject;
import sx.blah.discord.handle.obj.Permissions;


/**
 * Created by Vaerys on 29/01/2017.
 */
public interface Command {
    //Type Constants
    String TYPE_GENERAL = "General";
    String TYPE_ADMIN = "Admin";
    String TYPE_ROLE_SELECT = "Role";
    String TYPE_CHARACTER = "Characters";
    String TYPE_SERVERS = "Servers";
    String TYPE_CC = "CC";
    String TYPE_HELP = "Help";
    String TYPE_COMPETITION = "Competition";
    String TYPE_DM = "DM";
    String TYPE_CREATOR = "Creator";
    String TYPE_PIXEL = "Pixels";
    String TYPE_GROUPS = "Groups";

    //Channel Constants
    String CHANNEL_GENERAL = "General";
    String CHANNEL_SERVERS = "Servers";
    String CHANNEL_BOT_COMMANDS = "BotCommands";
    String CHANNEL_SERVER_LOG = "ServerLog";
    String CHANNEL_ADMIN_LOG = "AdminLog";
    String CHANNEL_ADMIN = "Admin";
    String CHANNEL_INFO = "Info";
    String CHANNEL_SHITPOST = "ShitPost";
    String CHANNEL_DONT_LOG = "DontLog";
    String CHANNEL_ART = "Art";
    String CHANNEL_GROUPS = "Groups";
    String CHANNEL_XP_DENIED = "XpDenied";
    String CHANNEL_PIXELS = "Pixels";
    String CHANNEL_LEVEL_UP = "LevelUp";
    String CHANNEL_LEVEL_UP_DENIED = "LevelUpDenied";

    String spacer = "\u200B";
    String indent = "    ";
    String codeBlock = "```";
    String ownerOnly = ">> ONLY THE BOT'S OWNER CAN RUN THIS <<";


    String execute(String args, CommandObject command);

    //descriptors
    String[] names();
    String description();
    String usage();
    String type();
    String channel();
    Permissions[] perms();
    boolean requiresArgs();
    boolean doAdminLogging();
    String dualDescription();
    String dualUsage();
    String dualType();
    Permissions[] dualPerms();

}
