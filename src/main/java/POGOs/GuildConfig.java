package POGOs;

import Annotations.ToggleAnnotation;
import Commands.Command;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.*;
import sx.blah.discord.handle.obj.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class GuildConfig {
    String prefixCommand = Constants.PREFIX_COMMAND;
    String prefixCC = Constants.PREFIX_CC;
    boolean properlyInit = false;
    String guildName = "";
    boolean loginMessage = true;
    boolean generalLogging = false;
    boolean adminLogging = false;
    boolean blackListing = false;
    boolean maxMentions = true;
    boolean dailyMessage = true;
    boolean shitPostFiltering = false;
    boolean muteRepeatOffenders = true;
    boolean compEntries = false;
    boolean compVoting = false;
    boolean moduleServers = true;
    boolean moduleChars = true;
    boolean moduleComp = false;
    int maxMentionLimit = 8;

    // TODO: 04/10/2016 let the mention limit be customisable.
    ArrayList<ChannelTypeObject> channels = new ArrayList<>();
    ArrayList<RoleTypeObject> cosmeticRoles = new ArrayList<>();
    ArrayList<RoleTypeObject> modifierRoles = new ArrayList<>();
    ArrayList<RoleTypeObject> trustedRoles = new ArrayList<>();
    ArrayList<BlackListObject> blackList = new ArrayList<>();
    ArrayList<OffenderObject> repeatOffenders = new ArrayList<>();
    RoleTypeObject roleToMention = new RoleTypeObject("No Role Set", null);
    RoleTypeObject mutedRole = new RoleTypeObject("No Role Set", null);

    public String getPrefixCommand() {
        return prefixCommand;
    }

    public void setPrefixCommand(String prefixCommand) {
        this.prefixCommand = prefixCommand;
    }

    public String getPrefixCC() {
        return prefixCC;
    }

    public void setPrefixCC(String prefixCC) {
        this.prefixCC = prefixCC;
    }

    public boolean isProperlyInit() {
        return properlyInit;
    }

    public void setProperlyInit(boolean properlyInit) {
        this.properlyInit = properlyInit;
    }

    public void initConfig() {
        if (!properlyInit) {
            blackList.add(new BlackListObject("discord.gg", "Please do not put **invites** in Custom Commands."));
            blackList.add(new BlackListObject("discordapp.com/Invite/", "Please do not put **invites** in Custom Commands."));
        }
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public ArrayList<ChannelTypeObject> getChannels() {
        return channels;
    }

    //Getters For the Toggles.
    public boolean doLoginMessage() {
        return loginMessage;
    }

    public int getMaxMentionLimit() {
        return maxMentionLimit;
    }

    public boolean doGeneralLogging() {
        return generalLogging;
    }

    public boolean doBlackListing() {
        return blackListing;
    }

    public boolean doShitPostFiltering() {
        return shitPostFiltering;
    }

    public boolean doAdminLogging() {
        return adminLogging;
    }

    public boolean doMaxMentions() {
        return maxMentions;
    }

    public boolean doDailyMessage() {
        return dailyMessage;
    }

    public boolean doMuteRepeatOffenders() {
        return muteRepeatOffenders;
    }

    public boolean doCompEntries() {
        return compEntries;
    }

    public boolean doCompVoting() {
        return compVoting;
    }

    public boolean doModuleServers() {
        return moduleServers;
    }

    public boolean doModuleChars() {
        return moduleChars;
    }

    public boolean doModuleComp() {
        return moduleComp;
    }

    //Togglers
    @ToggleAnnotation(name = "LoginMessage")
    public boolean toggleDoLoginMessage() {
        return loginMessage = !loginMessage;
    }

    @ToggleAnnotation(name = "GeneralLogging")
    public boolean toggleLogging() {
        return generalLogging = !generalLogging;
    }

    @ToggleAnnotation(name = "AdminLogging")
    public boolean toggleAdminLogging() {
        return adminLogging = !adminLogging;
    }

    @ToggleAnnotation(name = "BlackListing")
    public boolean toggleDoBlackListing() {
        return blackListing = !blackListing;
    }

    @ToggleAnnotation(name = "MentionSpam")
    public boolean toggelMaxMentions() {
        return maxMentions = !maxMentions;
    }

    @ToggleAnnotation(name = "DailyMessage")
    public boolean toggleDailyMessage() {
        return dailyMessage = !dailyMessage;
    }

    @ToggleAnnotation(name = "ShitPostFiltering")
    public boolean toggleShitPostFiltering() {
        return shitPostFiltering = !shitPostFiltering;
    }

    @ToggleAnnotation(name = "MuteRepeatOffender")
    public boolean toggleRepeatOffender() {
        return muteRepeatOffenders = !muteRepeatOffenders;
    }

    @ToggleAnnotation(name = "CompEntries")
    public boolean toggleCompEntries() {
        return compEntries = !compEntries;
    }

    @ToggleAnnotation(name = "Voting")
    public boolean toggleCompVoting() {
        return compVoting = !compVoting;
    }

    @ToggleAnnotation(name = "ModuleComp")
    public boolean toggleModuleComp() {
        return moduleComp = !moduleComp;
    }

    @ToggleAnnotation(name = "ModuleChars")
    public boolean toggleModuleChars() {
        return moduleChars = !moduleChars;
    }

    @ToggleAnnotation(name = "ModuleServers")
    public boolean toggleModuleServers() {
        return moduleServers = !moduleServers;
    }

    public void setUpChannel(String channelType, String channelID) {
        if (channelType.equals(Command.CHANNEL_SERVERS) && !moduleServers){
            return;
        }
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
                if (channelType.equalsIgnoreCase(Command.CHANNEL_SERVERS) && !moduleServers){
                    return null;
                }
                return c.getID();
            }
        }
        return null;
    }

    public void updateVariables(IGuild guild) {
        //update Guild Name
        setGuildName(guild.getName());

        //Update Races
        ArrayList<RoleTypeObject> newRaces = new ArrayList<>();
        for (RoleTypeObject r : cosmeticRoles) {
            if (guild.getRoleByID(r.getRoleID()) != null) {
                r.updateRoleName(guild.getRoleByID(r.getRoleID()).getName());
                newRaces.add(r);
            }
        }
        cosmeticRoles = newRaces;

        //Update Trusted Roles
        ArrayList<RoleTypeObject> newTrustedRoles = new ArrayList<>();
        for (RoleTypeObject r : trustedRoles) {
            if (guild.getRoleByID(r.getRoleID()) != null) {
                r.updateRoleName(guild.getRoleByID(r.getRoleID()).getName());
                newTrustedRoles.add(r);
            }
        }
        trustedRoles = newTrustedRoles;

        //Update Role to Mention
        if (roleToMention.getRoleID() != null) {
            if (guild.getRoleByID(roleToMention.getRoleID()) == null) {
                roleToMention = new RoleTypeObject("Role Missing", null);
            } else {
                roleToMention.updateRoleName(guild.getRoleByID(roleToMention.getRoleID()).getName());
            }
        }

        //update Muted Role
        if (mutedRole.getRoleID() != null) {
            if (guild.getRoleByID(mutedRole.getRoleID()) == null) {
                mutedRole = new RoleTypeObject("Role Missing", null);
            } else {
                mutedRole.updateRoleName(guild.getRoleByID(mutedRole.getRoleID()).getName());
            }
        }

        //update repeat offenders.
        ArrayList<OffenderObject> newMentionSpammers = new ArrayList<>();
        for (int i = 0; i < repeatOffenders.size(); i++) {
            IUser offender = Globals.getClient().getUserByID(repeatOffenders.get(i).getID());
            if (offender != null) {
                repeatOffenders.get(i).setDisplayName(offender.getName() + "#" + offender.getDiscriminator());
            }
        }

        //update channels
        for (ChannelTypeObject c : channels) {
            IChannel channel = guild.getChannelByID(c.getID());
            if (channel == null) {
                channels.remove(c);
            }
        }
        repeatOffenders = newMentionSpammers;
    }

    public ArrayList<BlackListObject> getBlackList() {
        return blackList;
    }

    public void addBlacklistItem(String phrase, String reason) {
        blackList.add(new BlackListObject(phrase, reason));
    }

    public void removeBlackListItem(String phrase) {
        for (int i = 0; i < blackList.size(); i++) {
            if (blackList.get(1).getPhrase().equalsIgnoreCase(phrase)) {
                blackList.remove(i);
            }
        }
    }

    public String setRoleToMention(String roleName, String roleID) {
        roleToMention = new RoleTypeObject(roleName, roleID);
        return "> the Role `" + roleName + "` will now be mentioned when the tag #admin# is called within the blacklisting process.";
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
        while (i < roleList.size()) {
            if (roleList.get(i).getRoleID().equals(roleID)) {
                isfound = true;
            }
            i++;
        }
        if (!isfound) {
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

    public boolean testIsTrusted(IUser author, IGuild guild) {
        if (trustedRoles.size() == 0) {
            return true;
        } else {
            for (RoleTypeObject task : trustedRoles) {
                for (IRole role : author.getRolesForGuild(guild)) {
                    if (role.getID().equals(task.getRoleID())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public void addTrusted(String roleID) {
        for (RoleTypeObject r : trustedRoles) {
            if (r.getRoleID().equals(roleID)) {
                return;
            }
        }
        trustedRoles.add(new RoleTypeObject(Globals.getClient().getRoleByID(roleID).getName(), roleID));
    }

    public void delTrusted(String roleID) {
        int i = 0;
        for (RoleTypeObject r : trustedRoles) {
            if (r.getRoleID().equals(roleID)) {
                trustedRoles.remove(i);
                return;
            }
            i++;
        }
    }

    public void setMaxMentionLimit(int maxMentionLimit) {
        this.maxMentionLimit = maxMentionLimit;
    }

    public void addOffender(OffenderObject offenderObject) {
        repeatOffenders.add(offenderObject);
    }

    public ArrayList<OffenderObject> getRepeatOffenders() {
        return repeatOffenders;
    }

    public void addOffence(String userID) {
        for (int i = 0; i < repeatOffenders.size(); i++) {
            if (repeatOffenders.get(i).getID().equals(userID)) {
                repeatOffenders.get(i).addOffence();
            }
        }
    }

    public void setMutedRole(RoleTypeObject mutedRole) {
        this.mutedRole = mutedRole;
    }

    public RoleTypeObject getMutedRole() {
        return mutedRole;
    }

    public ArrayList<RoleTypeObject> getTrustedRoles() {
        return trustedRoles;
    }

    public boolean isRoleCosmetic(String id) {
        for (RoleTypeObject r : cosmeticRoles) {
            if (r.getRoleID().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRoleModifier(String id) {
        for (RoleTypeObject r : modifierRoles) {
            if (r.getRoleID().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRoleTrusted(String id) {
        for (RoleTypeObject r : trustedRoles) {
            if (r.getRoleID().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public String getInfo(IGuild guild, IUser author) {
        String guildName = guild.getName();
        LocalDateTime creationDate = guild.getCreationDate();
        IUser guildOwner = guild.getOwner();
        IRegion region = guild.getRegion();
        List<IRole> roles = guild.getRoles();
        StringBuilder builder = new StringBuilder();
        ArrayList<String> cosmeticRoleStats = new ArrayList<>();
        ArrayList<String> modifierRoleStats = new ArrayList<>();
        int totalCosmetic = 0;
        int totalModified = 0;
        builder.append("***[" + guildName.toUpperCase() + "]***");
        builder.append("\n\n> Guild ID : **" + guild.getID());
        builder.append("**\n> Creation Date : **" + creationDate.getYear() + " " + creationDate.getMonth() + " " + creationDate.getDayOfMonth() + " - " + creationDate.getHour() + ":" + creationDate.getMinute());
        builder.append("**\n> Guild Owner : **@" + guildOwner.getName() + "#" + guildOwner.getDiscriminator() + "**");
        if (region != null) {
            builder.append("\n> Region : **" + region.getName() + "**");
        }
        builder.append("\n> Total Members: **" + guild.getUsers().size() + "**");
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_SERVER}, author, guild) || author.getID().equals(Globals.creatorID)) {
            builder.append("\n\n***[GUILD CONFIG OPTIONS]***");
            builder.append("\n> LoginMessage = **" + doLoginMessage());
            builder.append("**\n> DailyMessage = **" + doDailyMessage());
            builder.append("**\n> GeneralLogging = **" + doGeneralLogging());
            builder.append("**\n> AdminLogging = **" + doAdminLogging());
            builder.append("**\n> BlackListing = **" + doBlackListing());
            builder.append("**\n> MaxMentions = **" + doMaxMentions());
            builder.append("**\n> ShitPostFiltering = **" + doShitPostFiltering());
            builder.append("**\n> MuteRepeatOffenders = **" + doMuteRepeatOffenders());
            builder.append("**\n> CompEntries = **" + doCompEntries());
            builder.append("**\n> CompVoting = **" + doCompVoting());
            builder.append("**\n> Muted Role : **@" + getMutedRole().getRoleName());
            builder.append("**\n> RoleToMention : **@" + getRoleToMention().getRoleName() + "**");
        }
        if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_CHANNELS}, author, guild) || author.getID().equals(Globals.creatorID)) {
            builder.append("\n\n***[CHANNELS]***");
            for (ChannelTypeObject c : getChannels()) {
                builder.append("\n> " + c.getType() + " = **#" + guild.getChannelByID(c.getID()).getName() + "**");
            }
        }

        builder.append("\n\n***[ROLES]***");
        ArrayList<RoleStatsObject> statsObjects = new ArrayList<>();
        for (IRole r : roles) {
            if (!r.isEveryoneRole()) {
                statsObjects.add(new RoleStatsObject(r, this, guild.getUsersByRole(r).size()));
            }
        }
        for (RoleStatsObject rso : statsObjects) {
            StringBuilder formatted = new StringBuilder();
            formatted.append("\n> **" + rso.getRoleName() + "**");
            if (Utility.testForPerms(new Permissions[]{Permissions.MANAGE_ROLES}, author, guild) || author.getID().equals(Globals.creatorID)) {
                formatted.append(" Colour : \"**" + rso.getColour() + "**\",");
            }
            formatted.append(" Total Users: \"**" + rso.getTotalUsers() + "**\"");
            if (rso.isCosmetic()) {
                cosmeticRoleStats.add(formatted.toString());
                totalCosmetic += rso.getTotalUsers();
            }
            if (rso.isModifier()) {
                modifierRoleStats.add(formatted.toString());
                totalModified += rso.getTotalUsers();
            }
        }
        Collections.sort(cosmeticRoleStats);
        Collections.sort(modifierRoleStats);
        builder.append("\n\n**COSMETIC ROLES**");
        for (String s : cosmeticRoleStats) {
            if (builder.length() > 1800) {
                Utility.sendDM(builder.toString(), author.getID());
                builder.delete(0, builder.length());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            builder.append(s);
        }
        builder.append("\n > Total users : \"**" + totalCosmetic + "**\"");
        builder.append("\n\n**MODIFIER ROLES**");
        for (String s : modifierRoleStats) {
            if (builder.length() > 1800) {
                Utility.sendDM(builder.toString(), author.getID());
                builder.delete(0, builder.length());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            builder.append(s);
        }
        builder.append("\n > Total users : \"**" + totalModified + "**\"");
        builder.append("\n\n------{END OF INFO}------");
        Utility.sendDM(builder.toString(), author.getID());
        return "> Info sent to you via Direct Message.";
    }


}
