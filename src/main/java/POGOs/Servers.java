package POGOs;

import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.BlackListObject;
import Objects.ServerObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Permissions;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class Servers {
    boolean properlyInit = false;
    ArrayList<BlackListObject> blackList = new ArrayList<>();
    ArrayList<ServerObject> servers = new ArrayList<>();

    public boolean isProperlyInit() {
        return properlyInit;
    }

    public void setProperlyInit(boolean properlyInit) {
        this.properlyInit = properlyInit;
    }

    public String addToBlacklist(String phrase, String reason) {
        for (BlackListObject b : blackList) {
            if (b.getPhrase().equalsIgnoreCase(phrase)) {
                return "> Phrase already blacklisted";
            }
        }
        blackList.add(new BlackListObject(phrase, reason));
        return "> Phrase added to blacklist";
    }

    public String removeFromBlacklist(String phrase) {
        int i = 0;
        for (BlackListObject b : blackList) {
            if (b.getPhrase().equalsIgnoreCase(phrase)) {
                blackList.remove(i);
                return "> Phrase removed from blacklist.";
            }
            i++;
        }
        return "> Phrase could not be found.";
    }

    public String addServer(String userID, String serverName, String serverIP, String serverPort) {
        if (servers.size() > 0) {
            for (ServerObject s : servers) {
                if (s.getName().equalsIgnoreCase(serverName)) {
                    return "> A Server with that name already Exists.";
                }
                String checked = Utility.checkBlacklist(serverName + serverIP + serverPort, blackList);
                if (checked != null) {
                    return checked;
                }
            }
        }
        servers.add(new ServerObject(serverName, userID, serverIP, serverPort));
        return "> Server Added.";

    }

    public ArrayList<ServerObject> getServers() {
        return servers;
    }

    public String editServerDesc(String userID, String serverName, String desc, IGuild guild) {
        for (ServerObject s : servers) {
            if (s.getName().equalsIgnoreCase(serverName)) {
                boolean bypass = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, Globals.getClient().getUserByID(userID), guild);
                if (s.getCreatorID().equals(userID) || bypass) {
                    String check = Utility.checkBlacklist(desc, blackList);
                    if (check != null) {
                        return check;
                    }
                    if (desc.length() > 1000) {
                        return "> Could not edit Description, Reason: Too long.";
                    }
                    if (StringUtils.countMatches(desc, "\n") > 15) {
                        return "> Could not edit Description, Reason: Too many lines.";
                    }
                    ArrayList<String> newDesc = new ArrayList<>(Arrays.asList(desc.split("\n")));
                    s.setServerDesc(newDesc);
                    return "> Server Description Edited.";
                } else return "> You do not have permission to edit this server's Description.";
            }
        }
        return "> Server with that name not found.";
    }

    public String editIP(String userID, String serverName, String IP, String port, IGuild guild) {
        for (ServerObject s : servers) {
            if (s.getName().equalsIgnoreCase(serverName)) {
                boolean bypass = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, Globals.getClient().getUserByID(userID), guild);
                if (s.getCreatorID().equals(userID) || bypass) {
                    String check = Utility.checkBlacklist(IP + port, blackList);
                    if (check != null) {
                        return check;
                    }
                    s.setServerIP(IP);
                    s.setServerPort(port);
                    return "> Server IP edited.";
                } else return "> You do not have permission to edit this server's IP.";
            }
        }
        return "> Server with that name not found.";
    }

    public String editServerName(String userID, String oldServerName, String newServerName, IGuild guild) {
        for (ServerObject s : servers) {
            if (s.getName().equalsIgnoreCase(oldServerName)) {
                boolean bypass = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, Globals.getClient().getUserByID(userID), guild);
                if (s.getCreatorID().equals(userID) || bypass) {
                    for (ServerObject so : servers) {
                        if (so.getName().equalsIgnoreCase(newServerName)) {
                            return "> Cannot change Server name. That server name is already in use.";
                        }
                    }
                    String check = Utility.checkBlacklist(newServerName, blackList);
                    if (check != null) {
                        return check;
                    }
                    s.setName(newServerName);
                    return "> Server Name Edited.";
                } else return "> You do not have permission to edit this server's name.";
            }
        }
        return "> Server with that name not found.";
    }

    public String deleteServer(String userID, String serverName, IGuild guild) {
        int position = 0;
        for (ServerObject s : servers) {
            if (s.getName().equalsIgnoreCase(serverName)) {
                boolean bypass = Utility.testForPerms(new Permissions[]{Permissions.MANAGE_MESSAGES}, Globals.getClient().getUserByID(userID), guild);
                if (s.getCreatorID().equals(userID) || bypass) {
                    servers.remove(position);
                    return "> Server Deleted.";
                } else return "> You do not have permission to delete this server.";
            }
            position++;
        }
        return "> Server with that name not found.";
    }

    public String getServer(String serverName, IGuild guild) {
        for (ServerObject s : servers) {
            if (s.getName().equalsIgnoreCase(serverName)) {
                StringBuilder builder = new StringBuilder();
                builder.append("> **" + s.getName() + "**\n");
                builder.append(Constants.PREFIX_INDENT + "IP: **" + s.getServerIP() + "** Port: **" + s.getServerPort() + "**\n");
                builder.append(Constants.PREFIX_INDENT + "Listing Creator: " + guild.getUserByID(s.getCreatorID()).getDisplayName(guild) + "\n");
                builder.append(s.getServerDesc());
                return builder.toString();
            }
        }
        return "> Server with that name not found.";
    }
}


// TODO: 03/10/2016 add default blacklisted items.