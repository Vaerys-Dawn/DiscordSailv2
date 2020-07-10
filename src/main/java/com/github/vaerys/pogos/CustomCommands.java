package com.github.vaerys.pogos;

import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.userlevel.CCommandObject;
import com.github.vaerys.templates.GlobalFile;
import net.dv8tion.jda.api.Permission;
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
        boolean hasManagePerms = GuildHandler.testForPerms(user, guild, Permission.MESSAGE_MANAGE);
        boolean hasAdminPerms = GuildHandler.testForPerms(user, guild, Permission.ADMINISTRATOR);
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

    public void addCommand(boolean isLocked, long userID, String commandName, String commandContents, boolean isShitPost) {
        commands.add(new CCommandObject(isLocked, userID, commandName, commandContents, isShitPost));
    }

    public void initCustomCommands(Guild guild) {
        //make sure that echo works properly
        boolean echoExists = true;
        CCommandObject echo = getCommand("echo");
        if (echo != null && echo.getContents(false).contains("#")) {
            removeCommand(echo);
            echoExists = false;
        } else if (echo == null) {
            echoExists = false;
        }
        if (!echoExists)
            commands.add(new CCommandObject(true, Client.getClientObject().bot.longID, "Echo", "<args><dontSanitize>", false));

        //make sure that wiki works properly
        boolean wikiExists = true;
        CCommandObject wiki = getCommand("wiki");
        if (wiki != null && wiki.getContents(false).contains("#")) {
            removeCommand(wiki);
            wikiExists = false;
        } else if (wiki == null) {
            wikiExists = false;
        }
        if (!wikiExists)
            commands.add(new CCommandObject(true, Client.getClientObject().bot.longID, "Wiki", "http://starbounder.org/Special:Search/<args><replace>{ ;;_}", false));
    }

    public ArrayList<CCommandObject> getCommandList() {
        return commands;
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

    public void removeCommand(CCommandObject cc) {
        commands.remove(cc);
    }
}
