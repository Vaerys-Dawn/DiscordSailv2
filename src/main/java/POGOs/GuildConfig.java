package POGOs;

import Main.Constants;
import Objects.BlackListObject;
import Objects.ChannelTypeObject;
import Objects.RoleTypeObject;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class GuildConfig {
    public boolean properlyInit = false;
    String guildName = "";
    public Boolean doLoginMessage = false;
    public Boolean doLogging = false;
    public Boolean doBlackListing = false;
    public Boolean doShitPostFiltering = false;
    ArrayList<ChannelTypeObject> channels = new ArrayList<>();
    ArrayList<RoleTypeObject> cosmeticRoles = new ArrayList<>();
    ArrayList<RoleTypeObject> modifierRoles = new ArrayList<>();
    ArrayList<RoleTypeObject> trustedRoles = new ArrayList<>();
    ArrayList<BlackListObject> blackList = new ArrayList<>();
    RoleTypeObject roleToMention = new RoleTypeObject("No Role Set", Constants.NULL_VARIABLE);

    public void initConfig() {
        if (!properlyInit) {
            blackList.add(new BlackListObject("discord.gg", "Please do not put **invites** in Custom Commands."));
            blackList.add(new BlackListObject("discordapp.com/Invite/", "Please do not put **invites** in Custom Commands."));
        }
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public void toggleDoLoginMessage() {
        doLoginMessage = !doLoginMessage;
    }

    public void toggleDoBlackListing() {
        doBlackListing = !doBlackListing;
    }

    public void toggleLogging() {
        doLogging = !doLogging;
    }

    public void toggleShitPostFiltering() {
        doShitPostFiltering = !doShitPostFiltering;
    }

    public void setUpChannel(String channelType, String channelID) {
        if (channels.size() == 0) {
            channels.add(new ChannelTypeObject(channelType, channelID));
            return;
        }
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getType().equals(channelType)) {
                channels.set(i, new ChannelTypeObject(channelType, channelID));
                return;
            }
        }
        channels.add(new ChannelTypeObject(channelType, channelID));
    }

    public String getChannelTypeID(String channelType) {
        for (ChannelTypeObject c : channels) {
            if (c.getType().equals(channelType)) {
                return c.getID();
            }
        }
        return Constants.NULL_VARIABLE;
    }

    public void updateVariables(IGuild guild) {
        //Update Races
        ArrayList<RoleTypeObject> newRaces = new ArrayList<>();
        for (RoleTypeObject r : cosmeticRoles) {
            r.updateRoleName(guild.getRoleByID(r.getRoleID()).getName());
            newRaces.add(r);
        }
        cosmeticRoles = newRaces;
        //Update Trusted Roles
        ArrayList<RoleTypeObject> newTrustedRoles = new ArrayList<>();
        for (RoleTypeObject r : trustedRoles) {
            r.updateRoleName(guild.getRoleByID(r.getRoleID()).getName());
            newTrustedRoles.add(r);
        }
        trustedRoles = newTrustedRoles;
        if (!roleToMention.getRoleID().equals(Constants.NULL_VARIABLE)) {
            roleToMention.updateRoleName(guild.getRoleByID(roleToMention.getRoleID()).getName());
        }
    }

    public ArrayList<BlackListObject> getBlackList() {
        return blackList;
    }

    public void addBlacklistItem(String phrase, String reason) {
        blackList.add(new BlackListObject(phrase, reason));
    }

    public void removeBlackListItem(String phrase) {
        for (int i = 0; i < blackList.size(); i++) {
            if (blackList.get(1).phrase.equalsIgnoreCase(phrase)) {
                blackList.remove(i);
            }
        }
    }

    public String setRoleToMention(String roleName, String roleID) {
        roleToMention = new RoleTypeObject(roleName, roleID);
        return "> the Role `" + roleName + "` will now be mentioned when the tag #admin# is called withing the blacklisting process.";
    }

    public RoleTypeObject getRoleToMention() {
        return roleToMention;
    }

    public String addRole(String roleID, String roleName, boolean isCosmetic) {
        ArrayList<RoleTypeObject> roleList;
        boolean isfound = false;
        int i = 0;
        if (isCosmetic) {
            roleList = cosmeticRoles;
        } else {
            roleList = modifierRoles;
        }
        while (i < roleList.size()){
            if (roleList.get(i).getRoleID().equals(roleID)) {
                isfound = true;
            }
            i++;
        }
        if (!isfound){
            roleList.add(new RoleTypeObject(roleName, roleID));
            if (isCosmetic) {
                cosmeticRoles = roleList;
            } else {
                modifierRoles = roleList;
            }
            return "> Role `" + roleName + "` Added to Role List.";
        }
        return "> Role already added to list.";
    }

    public String removeRole(String roleID, String roleName, boolean isCosmetic) {
        ArrayList<RoleTypeObject> roleList;
        if (isCosmetic) {
            roleList = cosmeticRoles;
        } else {
            roleList = modifierRoles;
        }
        for (int i = 0; i < roleList.size(); i++) {
            if (roleList.get(i).getRoleID().equals(roleID)) {
                roleList.remove(i);
                if (isCosmetic) {
                    cosmeticRoles = roleList;
                } else {
                    modifierRoles = roleList;
                }
                return "> Role `" + roleName + "` Removed from Role List.";
            }
        }
        return "> Role not in list of roles.";
    }

    public ArrayList<RoleTypeObject> getModifierRoles() {
        return modifierRoles;
    }

    public ArrayList<RoleTypeObject> getCosmeticRoles() {
        return cosmeticRoles;
    }
}
