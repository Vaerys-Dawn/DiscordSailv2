package com.github.vaerys.pogos;

import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.depreciated.BlackListObject;
import com.github.vaerys.objects.userlevel.ServerObject;
import com.github.vaerys.templates.GlobalFile;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.Guild;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class Servers extends GlobalFile {
    public static final String FILE_PATH = "Servers.json";
    private double fileVersion = 1.0;
    ArrayList<BlackListObject> blackList = new ArrayList<>();
    ArrayList<ServerObject> servers = new ArrayList<>();

    public String addToBlacklist(String phrase, String reason) {
        for (BlackListObject b : blackList) {
            if (b.getPhrase().equalsIgnoreCase(phrase)) {
                return "\\> Phrase already blacklisted";
            }
        }
        blackList.add(new BlackListObject(phrase, reason));
        return "\\> Phrase added to blacklist";
    }

    public String removeFromBlacklist(String phrase) {
        int i = 0;
        for (BlackListObject b : blackList) {
            if (b.getPhrase().equalsIgnoreCase(phrase)) {
                blackList.remove(i);
                return "\\> Phrase removed from blacklist.";
            }
            i++;
        }
        return "\\> Phrase could not be found.";
    }

    public String addServer(long userID, String serverName, String serverIP, String serverPort) {
        if (servers.size() > 0) {
            for (ServerObject s : servers) {
                if (s.getName().equalsIgnoreCase(serverName)) {
                    return "\\> A Server with that name already Exists.";
                }
                String checked = Utility.checkBlacklist(serverName + serverIP + serverPort, blackList);
                if (checked != null) {
                    return checked;
                }
            }
        }
        servers.add(new ServerObject(serverName, userID, serverIP, serverPort));
        return "\\> Server Added.";

    }

    public ArrayList<ServerObject> getServers() {
        return servers;
    }

    public String editServerDesc(long userID, String serverName, String desc, Guild guild) {
        for (ServerObject s : servers) {
            if (s.getName().equalsIgnoreCase(serverName)) {
                boolean bypass = GuildHandler.testForPerms(Globals.getClient().getUserByID(userID), guild, Permissions.MANAGE_MESSAGES);
                if (s.getCreatorID() == userID || bypass) {
                    String check = Utility.checkBlacklist(desc, blackList);
                    if (check != null) {
                        return check;
                    }
                    if (desc.length() > 1000) {
                        return "\\> Could not edit Description, Reason: Too long.";
                    }
                    if (StringUtils.countMatches(desc, "\n") > 15) {
                        return "\\> Could not edit Description, Reason: Too many lines.";
                    }
                    ArrayList<String> newDesc = new ArrayList<>(Arrays.asList(desc.split("\n")));
                    s.setServerDesc(newDesc);
                    return "\\> Server Description Edited.";
                } else return "\\> You do not have permission to edit this server's Description.";
            }
        }
        return "\\> Server with that name not found.";
    }

    public String editIP(long userID, String serverName, String IP, String port, Guild guild) {
        for (ServerObject s : servers) {
            if (s.getName().equalsIgnoreCase(serverName)) {
                boolean bypass = GuildHandler.testForPerms(Globals.getClient().getUserByID(userID), guild, Permissions.MANAGE_MESSAGES);
                if (s.getCreatorID() == userID || bypass) {
                    String check = Utility.checkBlacklist(IP + port, blackList);
                    if (check != null) {
                        return check;
                    }
                    s.setServerIP(IP);
                    s.setServerPort(port);
                    return "\\> Server IP edited.";
                } else return "\\> You do not have permission to edit this server's IP.";
            }
        }
        return "\\> Server with that name not found.";
    }

    public String editServerName(long userID, String oldServerName, String newServerName, Guild guild) {
        for (ServerObject s : servers) {
            if (s.getName().equalsIgnoreCase(oldServerName)) {
                boolean bypass = GuildHandler.testForPerms(Globals.getClient().getUserByID(userID), guild, Permissions.MANAGE_MESSAGES);
                if (s.getCreatorID() == userID || bypass) {
                    for (ServerObject so : servers) {
                        if (so.getName().equalsIgnoreCase(newServerName)) {
                            return "\\> Cannot change Server name. That server name is already in use.";
                        }
                    }
                    String check = Utility.checkBlacklist(newServerName, blackList);
                    if (check != null) {
                        return check;
                    }
                    s.setName(newServerName);
                    return "\\> Server Name Edited.";
                } else return "\\> You do not have permission to edit this server's name.";
            }
        }
        return "\\> Server with that name not found.";
    }

    public String deleteServer(long userID, String serverName, Guild guild) {
        int position = 0;
        for (ServerObject s : servers) {
            if (s.getName().equalsIgnoreCase(serverName)) {
                boolean bypass = GuildHandler.testForPerms(Globals.getClient().getUserByID(userID), guild, Permissions.MANAGE_MESSAGES);
                if (s.getCreatorID() == userID || bypass) {
                    servers.remove(position);
                    return "\\> Server Deleted.";
                } else return "\\> You do not have permission to delete this server.";
            }
            position++;
        }
        return "\\> Server with that name not found.";
    }

    public boolean checkForUser(long userID) {
        if (servers.stream().map(c -> c.getCreatorID()).filter(c -> c == userID).toArray().length != 0) return true;
        return false;
    }
}


// TODO: 03/10/2016 add default blacklisted items.
