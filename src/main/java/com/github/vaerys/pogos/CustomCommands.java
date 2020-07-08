package com.github.vaerys.pogos;

import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.templates.GlobalFile;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Guild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.HashMap;

// TODO: 31/08/2016 Add the ability to search for a custom command based on name, getContents, or ShitPost (use separate commands) -- partially complete.
// name:[name] shitpost getContents:[getContents]
// TODO: 31/08/2016 Add blacklisting of phrases to custom command creation, editing and execution
// TODO: 07/10/2016 add tag `variant` that allows people to use $editcc [command Name] addvariant [Content] to that (would use 'Random' tag as a base)

/**
 * Created by Vaerys on 14/08/2016.
 */
public class CustomCommands extends GlobalFile {
    public static final String FILE_PATH = "Custom_Commands.json";
    private double fileVersion = 1.5;
    ArrayList<CCommandObject> commands = new ArrayList<>();

    public int maxCCs(UserObject user, GuildObject guild) {
        int total = 10;
        boolean hasManagePerms = GuildHandler.testForPerms(user, guild, Permissions.MANAGE_MESSAGES);
        boolean hasAdminPerms = GuildHandler.testForPerms(user, guild, Permissions.ADMINISTRATOR);
        if (hasManagePerms) {
            total += 50;
        }
        if (hasAdminPerms) {
            total += 100;
        }
        total += (PixelHandler.getRewardCount(guild, user.longID) * 10);
        if (guild.getOwnerID() == user.longID) {
            total = 200;
        }
        if (user.isPatron) {
            total += 50;
        }
        return total;
    }

    public String addCommand(boolean isLocked, String commandName, String commandContents, boolean isShitPost, CommandObject object) {
        int counter = 0;
        int limitCCs;
        if (commandName.length() > 50) {
            return "\\> Command name too long.";
        }
        if (commandName.isEmpty()) {
            return "\\> Command name cannot be empty.";
        }
        if (StringUtils.countMatches(commandContents, "<embedImage>{") > 1) {
            return "\\> Custom Commands Cannot have multiple <embedImage> tags";
        }
        limitCCs = maxCCs(object.user, object.guild);

        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(commandName)) {
                return "\\> Command name already in use.";
            }
            if (c.getUserID() == object.user.longID) {
                counter++;
            }
        }
        if (counter < limitCCs) {
            commands.add(new CCommandObject(isLocked, object.user.longID, commandName, commandContents, isShitPost));
            long remaining = limitCCs - counter - 1;
            String response = "\\> Command Added, you have ";
            if (remaining > 1) {
                response += remaining + " custom command slots left.\n";
            } else {
                response += remaining + " custom command slot left.\n";
            }
            response += Constants.PREFIX_INDENT + "You can run your new command by performing `" + object.guild.config.getPrefixCC() + commandName + "`.";
            return response;

        } else {
            return "\\> You have run out of custom command slots. you can make room by deleting or editing old custom commands.";
        }
    }

    public void initCustomCommands(Guild guild) {
        //make sure that echo works properly
        boolean echoExists = true;
        CCommandObject echo = getCommand("echo");
        if (echo != null && echo.getContents(false).contains("#")) {
            delCommand("echo", Globals.getClient().getOurUser(), guild);
            echoExists = false;
        } else if (echo == null) {
            echoExists = false;
        }
        if (!echoExists)
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getIdLong(), "Echo", "<args><dontSanitize>", false));

        //make sure that wiki works properly
        boolean wikiExists = true;
        CCommandObject wiki = getCommand("wiki");
        if (wiki != null && wiki.getContents(false).contains("#")) {
            delCommand("wiki", Globals.getClient().getOurUser(), guild);
            wikiExists = false;
        } else if (wiki == null) {
            wikiExists = false;
        }
        if (!wikiExists)
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getIdLong(), "Wiki", "http://starbounder.org/Special:Search/<args><replace>{ ;;_}", false));
    }

    public ArrayList<CCommandObject> getCommandList() {
        return commands;
    }

//    @Deprecated
//    public String sendCCasJSON(long channelID, String commandName) {
//        TextChannel channel = Globals.getClient().getChannelByID(channelID);
//        for (CCommandObject c : commands) {
//            if (c.getName().equalsIgnoreCase(commandName)) {
//                FileHandler.writeToJson(Constants.DIRECTORY_TEMP + c.getName() + ".json", c);
//                File file = new File(Constants.DIRECTORY_TEMP + c.getName() + ".json");
//                if (RequestHandler.sendFile("> Here is the Raw Data for Custom Command: **" + c.getName() + "**", file, channel).get() == null) {
//                    RequestHandler.sendMessage("> An error occurred when attempting to getSlashCommands CC data.", channel);
//                }
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    Utility.sendStack(e);
//                }
//                file.delete();
//                return null;
//            }
//        }
//        RequestHandler.sendMessage(Constants.ERROR_CC_NOT_FOUND, channel);
//        return null;
//    }


    public String delCommand(String args, IUser author, Guild guild) {
        int i = 0;
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(args)) {
                boolean canBypass = GuildHandler.testForPerms(author, guild, Permissions.MANAGE_MESSAGES);
                if (author.getIdLong() == guild.getOwnerLongID()
                        || author.getIdLong() == Globals.creatorID
                        || author.getIdLong() == Globals.client.getOurUser().getIdLong()) {
                    canBypass = true;
                }
                if (author.getIdLong() == c.getUserID() || canBypass) {
                    if (c.isLocked() && Globals.client.getOurUser().getIdLong() != author.getIdLong()) {
                        return "\\> This command is locked and must be unlocked to be deleted.";
                    } else {
                        commands.remove(i);
                        return "\\> Command Deleted.";
                    }
                } else {
                    return "\\> You do not have permission to delete that command.";
                }
            }
            i++;
        }
        return Constants.ERROR_CC_NOT_FOUND;
    }

    public String getUserCommandCount(UserObject user, GuildObject guild) {
        int totalCommands = 0;
        int ccMax = maxCCs(user, guild);
        for (CCommandObject c : commands) {
            if (c.getUserID() == user.longID) {
                totalCommands++;
            }
        }
        return totalCommands + "/" + ccMax;
    }

    public CCommandObject getCommand(String name) {
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public CCommandObject getCommand(String name, CommandObject command) {
        for (CCommandObject c : commands) {
            if ((command.guild.config.prefixCC + c.getName()).equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public boolean checkForUser(long userID) {
        if (commands.stream().map(c -> c.getUserID()).filter(c -> c == userID).toArray().length != 0) return true;
        return false;
    }
}
