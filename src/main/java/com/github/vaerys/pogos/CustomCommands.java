package com.github.vaerys.pogos;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.handlers.XpHandler;
import com.github.vaerys.interfaces.GuildFile;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.BlackListObject;
import com.github.vaerys.objects.CCommandObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

import java.io.File;
import java.util.ArrayList;

// TODO: 31/08/2016 Add the ability to search for a custom command based on name, contents, or ShitPost (use separate commands) -- partially complete.
// name:[name] shitpost contents:[contents]
// TODO: 31/08/2016 Add blacklisting of phrases to custom command creation, editing and execution
// TODO: 07/10/2016 add tag `variant` that allows people to use $editcc [command Name] addvariant [Content] to that (would use 'Random' tag as a base)

/**
 * Created by Vaerys on 14/08/2016.
 */
public class CustomCommands extends GuildFile {
    public static final String FILE_PATH = "Custom_Commands.json";
    private double fileVersion = 1.1;
    ArrayList<BlackListObject> blackList = new ArrayList<BlackListObject>() {{
        add(new BlackListObject("<@", "Please do not put **mentions** in Custom Commands."));
        add(new BlackListObject("discord.gg", "Please do not put **invites** in Custom Commands."));
        add(new BlackListObject("discordapp.com/Invite/", "Please do not put **invites** in Custom Commands."));
        add(new BlackListObject("@everyone", "Please go not put **mentions** in Custom Commands."));
        add(new BlackListObject("@here", "Please go not put **mentions** in Custom Commands."));
    }};
    ArrayList<CCommandObject> commands = new ArrayList<>();

    public int maxCCs(UserObject user, GuildObject guild) {
        // TODO: 03/07/2017 move this to grant ccs based on reward roles.
        int total = 10;
        boolean hasManagePerms = Utility.testForPerms(user, guild, Permissions.MANAGE_MESSAGES);
        boolean hasAdminPerms = Utility.testForPerms(user, guild, Permissions.ADMINISTRATOR);
        if (hasManagePerms) {
            total += 50;
        }
        if (hasAdminPerms) {
            total += 100;
        }
        if (guild.config.modulePixels) {
            total += (XpHandler.getRewardCount(guild, user.longID) * 10);
        } else {
            total += 40;
        }
        if (guild.get().getOwner().getLongID() == user.longID) {
            total = 200;
        }
        return total;
    }

    public String addCommand(boolean isLocked, String commandName, String commandContents, boolean isShitPost, CommandObject object) {
        int counter = 0;
        int limitCCs;
        String toCheck = commandName + commandContents;
        if (Utility.checkBlacklist(toCheck, blackList) != null) {
            return Utility.checkBlacklist(toCheck, blackList);
        }
        if (commandName.length() > 50) {
            return "> Command name too long.";
        }
        if (commandName.isEmpty()) {
            return "> Command name cannot be empty.";
        }
        if (StringUtils.countMatches(commandContents, "#embedImage#{") > 1) {
            return "> Custom Commands Cannot have multiple #embedImage# tags";
        }
        limitCCs = maxCCs(object.user, object.guild);

        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(commandName)) {
                return "> Command name already in use.";
            }
            if (c.getUserID() == object.user.longID) {
                counter++;
            }
        }
        if (counter < limitCCs) {
            if (commandContents.length() < 1500) {
                commands.add(new CCommandObject(isLocked, object.user.longID, commandName, commandContents, isShitPost));
                return "> Command Added you have " + (limitCCs - counter - 1) + " custom command slots left.\n" +
                        Constants.PREFIX_INDENT + "You can run your new command by performing `" + object.guild.config.getPrefixCC() + commandName + "`.";
            } else {
                return "> Command Contents to long. max length = 1500 chars.";
            }
        } else {
            return "> You have run out of custom command slots. you can make room by deleting or editing old custom commands.";
        }
    }

    public void initCustomCommands() {
        boolean echoExists = false;
        boolean wikiExists = false;
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase("Echo"))
                echoExists = true;
            if (c.getName().equalsIgnoreCase("Wiki"))
                wikiExists = true;
        }
        if (!echoExists)
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getLongID(), "Echo", "#args#", false));
        if (!wikiExists)
            commands.add(new CCommandObject(true, Globals.getClient().getOurUser().getLongID(), "Wiki", "http://starbounder.org/Special:Search/#args##regex#{ ;_}", false));
    }

    public ArrayList<CCommandObject> getCommandList() {
        return commands;
    }

    public String checkblackList(String args) {
        return Utility.checkBlacklist(args, blackList);
    }

    public String sendCCasJSON(long channelID, String commandName) {
        IChannel channel = Globals.getClient().getChannelByID(channelID);
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(commandName)) {
                FileHandler.writeToJson(Constants.DIRECTORY_TEMP + c.getName() + ".json", c);
                File file = new File(Constants.DIRECTORY_TEMP + c.getName() + ".json");
                if (Utility.sendFile("> Here is the Raw Data for Custom Command: **" + c.getName() + "**", file, channel).get()) {
                    Utility.sendMessage("> An error occurred when attempting to getSlashCommands CC data.", channel);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Utility.sendStack(e);
                }
                file.delete();
                return null;
            }
        }
        Utility.sendMessage(Constants.ERROR_CC_NOT_FOUND, channel);
        return null;
    }

    public String delCommand(String args, IUser author, IGuild guild) {
        int i = 0;
        for (CCommandObject c : commands) {
            if (c.getName().equalsIgnoreCase(args)) {
                boolean canBypass = Utility.testForPerms(author, guild, Permissions.MANAGE_MESSAGES);
                if (author.getLongID() == guild.getOwnerLongID() || author.getLongID() == Globals.creatorID) {
                    canBypass = true;
                }
                if (author.getLongID() == c.getUserID() || canBypass) {
                    if (c.isLocked()) {
                        return "> This command is locked and must be unlocked to be deleted.";
                    } else {
                        commands.remove(i);
                        return "> Command Deleted.";
                    }
                } else {
                    return "> You do not have permission to delete that command.";
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
}
