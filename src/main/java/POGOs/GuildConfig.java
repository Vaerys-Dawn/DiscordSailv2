package POGOs;

import Main.Constants;
import Objects.BlackListObject;
import Objects.ChannelTypeObject;
import Objects.RoleTypeObject;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.DiscordClientImpl;
import sx.blah.discord.handle.obj.IDiscordObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

import java.util.ArrayList;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class GuildConfig{
    public boolean properlyInit = false;
    String guildName = "";
    public Boolean doLoginMessage = false;
    public Boolean doLogging = false;
    public Boolean doBlackListing = false;
    public Boolean doShitPostFiltering = false;
    ArrayList<ChannelTypeObject> channels = new ArrayList<>();
    ArrayList<RoleTypeObject> races = new ArrayList<>();
    ArrayList<RoleTypeObject> trustedRoles = new ArrayList<>();
    ArrayList<BlackListObject> blackList = new ArrayList<>();
    RoleTypeObject roleToMention = new RoleTypeObject("No Role Set", Constants.NULL_VARIABLE);

    public void initConfig() {
        if (!properlyInit){
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

    public void toggleDoBlackListing(){
        doBlackListing = !doBlackListing;
    }

    public void toggleLogging(){
        doLogging = !doLogging;
    }

    public void toggleShitPostFiltering(){
        doShitPostFiltering = !doShitPostFiltering;
    }

    public void setUpChannel(String channelType, String channelID){
        if (channels.size() == 0){
            channels.add(new ChannelTypeObject(channelType,channelID));
            return;
        }
        for (int i = 0;i < channels.size();i++){
            if (channels.get(i).getType().equals(channelType)){
                channels.set(i,new ChannelTypeObject(channelType,channelID));
                return;
            }
        }
        channels.add(new ChannelTypeObject(channelType,channelID));
    }

    public String getChannelTypeID(String channelType){
        for (ChannelTypeObject c : channels){
            if (c.getType().equals(channelType)){
                return c.getID();
            }
        }
        return Constants.NULL_VARIABLE;
    }

    public void updateVariables(IGuild guild) {
        //Update Races
        ArrayList<RoleTypeObject> newRaces = new ArrayList<>();
        for (RoleTypeObject r : races){
            r.updateRoleName(guild.getRoleByID(r.getRoleID()).getName());
            newRaces.add(r);
        }
        races = newRaces;
        //Update Trusted Roles
        ArrayList<RoleTypeObject> newTrustedRoles = new ArrayList<>();
        for (RoleTypeObject r : trustedRoles){
            r.updateRoleName(guild.getRoleByID(r.getRoleID()).getName());
            newTrustedRoles.add(r);
        }
        trustedRoles = newTrustedRoles;
        if (!roleToMention.getRoleID().equals(Constants.NULL_VARIABLE)){
            roleToMention.updateRoleName(guild.getRoleByID(roleToMention.getRoleID()).getName());
        }
    }

    public ArrayList<BlackListObject> getBlackList(){
        return blackList;
    }

    public void addBlacklistItem(String phrase, String reason) {
        blackList.add(new BlackListObject(phrase,reason));
    }

    public void removeBlackListItem(String phrase){
        for (int i = 0; i < blackList.size(); i++){
            if (blackList.get(1).phrase.equalsIgnoreCase(phrase)){
                blackList.remove(i);
            }
        }
    }

    public void setRoleToMention(String roleName, String RoleID){
        roleToMention = new RoleTypeObject(roleName,roleName);
    }

    public RoleTypeObject getRoleToMention() {
        return roleToMention;
    }

    public String addRaceRole(String roleID,String roleName){
        if (races.size() > 0){
            for (RoleTypeObject r : races){
                if (!r.getRoleID().equals(roleID)){
                    races.add(new RoleTypeObject(roleName,roleID));
                    return "> Role `" + roleName + "` Added to Role List.";
                }
            }
        }else {
            races.add(new RoleTypeObject(roleName,roleID));
            return "> Role `" + roleName + "` Added to Role List.";
        }
        return "> Role already added to list.";
    }

    public String removeRaceRole(String roleID,String roleName) {
        for (int i = 0; i < races.size(); i++){
            if (races.get(i).getRoleID().equals(roleID)){
                races.remove(i);
                return "> Role `" + roleName + "` Removed from Role List.";
            }
        }
        return "> Role not in list of roles.";
    }
}
