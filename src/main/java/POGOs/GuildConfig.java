package POGOs;

import Objects.ChannelType;
import Objects.RoleType;
import Objects.UserType;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class GuildConfig{
    String guildName = "";
    public Boolean doLoginMessage = false;
    public Boolean doLogging = false;
    public Boolean doBlackListing = false;
    ArrayList<ChannelType> channels = new ArrayList<>();
    ArrayList<RoleType> races = new ArrayList<>();
    ArrayList<RoleType> trustedRoles = new ArrayList<>();

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public void toggleDoLoginMessage() {
        if (doLogging){
            doLogging = false;
        }else {
            doLogging = true;
        }
    }

    public void toggleDoBlackListing(){
        if (doBlackListing){
            doBlackListing = false;
        }else {
            doBlackListing = true;
        }
    }

    public void setUpChannel(String channelType, String channelID){
        if (channels.size() == 0){
            channels.add(new ChannelType(channelType,channelID));
            return;
        }
        for (int i = 0;i < channels.size();i++){
            if (channels.get(i).getType().equals(channelType)){
                channels.set(i,new ChannelType(channelType,channelID));
                return;
            }
        }
        channels.add(new ChannelType(channelType,channelID));
    }

    public String getChannelTypeID(String channelType){
        for (ChannelType c : channels){
            if (c.getType().equals(channelType)){
                return c.getID();
            }
        }
        return "";
    }

    public void updateVariables(IGuild guild) {
        //Update Races
        ArrayList<RoleType> newRaces = new ArrayList<>();
        for (RoleType r : races){
            r.updateRoleName(guild.getRoleByID(r.getRoleID()).getName());
            newRaces.add(r);
        }
        races = newRaces;
        //Update Trusted Roles
        ArrayList<RoleType> newTrustedRoles = new ArrayList<>();
        for (RoleType r : trustedRoles){
            r.updateRoleName(guild.getRoleByID(r.getRoleID()).getName());
            newTrustedRoles.add(r);
        }
        trustedRoles = newTrustedRoles;
    }
}
