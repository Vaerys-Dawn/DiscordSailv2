package com.github.vaerys.pogos;

import com.github.vaerys.main.Globals;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.ChannelSettingObject;
import com.github.vaerys.objects.DailyMessage;
import com.github.vaerys.objects.OffenderObject;
import com.github.vaerys.objects.RewardRoleObject;
import com.github.vaerys.templates.GuildFile;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 03/08/2016.
 */
public class GuildConfig extends GuildFile {
    public static final String FILE_PATH = "Guild_Config.json";
    private double fileVersion = 1.1;
    String prefixCommand = Globals.defaultPrefixCommand;
    String prefixCC = Globals.defaultPrefixCC;
    String guildName = "";
    long guildID = -1;
    //toggles
    //--Auto Tasks
    public boolean dailyMessage = false;
    public boolean artPinning = false;
    public boolean autoArtPinning = false;
    public boolean xpDecay = false;
    public boolean xpGain = false;
    public boolean joinsServerMessages = false;
    public boolean selfDestructLevelUps = true;
    public boolean reactToLevelUp = false;
    public boolean likeArt = false;
    public boolean welcomeMessage = false;
    //--Logging
    public boolean generalLogging = false;
    public boolean adminLogging = false;
    public boolean deleteLogging = false;
    public boolean joinLeaveLogging = false;
    public boolean userRoleLogging = false;
    public boolean editLogging = false;
    public boolean extendEditLog = false;
    public boolean channelLogging = false;
    public boolean useTimeStamps = false;
    public boolean dontLogBot = false;
    //--Admin
    public boolean denyInvites = false;
    public boolean maxMentions = true;
    public boolean shitPostFiltering = false;
    public boolean muteRepeatOffenders = true;
    public boolean stopSpamWalls = true;
    public boolean rateLimiting = false;
    public boolean slashCommands = false;
    public boolean roleIsToggle = false;
    public boolean userInfoShowsDate = false;
    public boolean debugMode = true;
    public boolean readRuleReward = false;
    //--Competition
    public boolean compEntries = false;
    public boolean compVoting = false;
    //modules
    public boolean moduleServers = false;
    public boolean moduleChars = false;
    public boolean moduleComp = false;
    public boolean moduleRoles = true;
    public boolean moduleCC = true;
    public boolean moduleMe = true;
    public boolean moduleModMute = false;
    public boolean moduleGroups = false;
    public boolean modulePixels = false;

    public int maxMentionLimit = 8;
    public int messageLimit = 10;
    public int xpRate = 20;
    public float xpModifier = 1;

    public long xpDeniedRoleID = -1;
    public long topTenRoleID = -1;
    long roleToMentionID = -1;
    long mutedRoleID = -1;
    public int pinLimit = 25;

    public UserSetting defaultLevelMode = UserSetting.SEND_LVLUP_RANK_CHANNEL;


    public String levelUpReaction = "null";
    public String levelUpMessage = "Ding. Gratz on level <level> <user>.";
    private String joinMessage = "> Welcome to the <server> server <user>.";
    private DailyMessage lastDailyMessage = null;
    private ArrayList<String> xpDeniedPrefixes = new ArrayList<>();

    // TODO: 04/10/2016 let the mention limit be customisable.
    ArrayList<ChannelSettingObject> channelSettings = new ArrayList<>();
    ArrayList<Long> cosmeticRoleIDs = new ArrayList<>();
    ArrayList<Long> modifierRoleIDs = new ArrayList<>();
    ArrayList<Long> trustedRoleIDs = new ArrayList<>();
    ArrayList<RewardRoleObject> rewardRoles = new ArrayList<>();
    ArrayList<OffenderObject> offenders = new ArrayList<>();
    private String ruleCode = null;


    public void setLastDailyMessage(DailyMessage lastDailyMessage) {
        this.lastDailyMessage = lastDailyMessage;
    }

    public DailyMessage getLastDailyMessage() {
        return lastDailyMessage;
    }

    public ArrayList<RewardRoleObject> getRewardRoles() {
        return rewardRoles;
    }

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

    public void addChannelSetting(String type, long id) {
        channelSettings.add(new ChannelSettingObject(type, id));
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public ArrayList<ChannelSettingObject> getChannelSettings() {
        return channelSettings;
    }

    public void updateVariables(IGuild guild) {
        //update Guild Name
        setGuildName(guild.getName());
        guildID = guild.getLongID();

        validateRoles();

        //update channels
        for (ChannelSettingObject c : channelSettings) {
            ListIterator iterator = c.getChannelIDs().listIterator();
            while (iterator.hasNext()) {
                IChannel channel = guild.getChannelByID((Long) iterator.next());
                if (channel == null) {
                    iterator.remove();
                }
            }
        }

    }

    public void setRoleToMentionID(long roleID) {
        validateRoles();
        roleToMentionID = roleID;
    }

    public long getRoleToMentionID() {
        validateRoles();
        return roleToMentionID;
    }

    public void validateRoles() {
        IGuild guild = Globals.client.getGuildByID(guildID);
        if (guild == null) {
            return;
        }
        ListIterator iterator = cosmeticRoleIDs.listIterator();
        while (iterator.hasNext()) {
            IRole role = guild.getRoleByID((Long) iterator.next());
            if (role == null) {
                iterator.remove();
            }
        }
        iterator = modifierRoleIDs.listIterator();
        while (iterator.hasNext()) {
            IRole role = guild.getRoleByID((Long) iterator.next());
            if (role == null) {
                iterator.remove();
            }
        }
        iterator = trustedRoleIDs.listIterator();
        while (iterator.hasNext()) {
            IRole role = guild.getRoleByID((Long) iterator.next());
            if (role == null) {
                iterator.remove();
            }
        }
        IRole mutedRole = guild.getRoleByID(mutedRoleID);
        if (mutedRole == null) {
            mutedRoleID = -1;
        }
        IRole roleToMention = guild.getRoleByID(roleToMentionID);
        if (roleToMention == null) {
            roleToMentionID = -1;
        }
        iterator = rewardRoles.listIterator();
        while (iterator.hasNext()) {
            RewardRoleObject reward = (RewardRoleObject) iterator.next();
            IRole role = guild.getRoleByID(reward.getRoleID());
            if (role == null) {
                iterator.remove();
            }
        }
    }

    public ArrayList<Long> getModifierRoleIDs() {
        validateRoles();
        return modifierRoleIDs;
    }

    public ArrayList<Long> getCosmeticRoleIDs() {
        validateRoles();
        return cosmeticRoleIDs;
    }

    public boolean testIsTrusted(IUser author, IGuild guild) {
        validateRoles();
        if (trustedRoleIDs.size() == 0) {
            return true;
        } else {
            for (IRole r : author.getRolesForGuild(guild)) {
                if (isRoleTrusted(r.getLongID())) {
                    return true;
                }
            }
            return false;
        }
    }

    public void setMaxMentionLimit(int maxMentionLimit) {
        this.maxMentionLimit = maxMentionLimit;
    }

    public void addOffender(OffenderObject offenderObject) {
        offenders.add(offenderObject);
    }

    public ArrayList<OffenderObject> getOffenders() {
        return offenders;
    }

    public void addOffence(long userID) {
        for (OffenderObject o : offenders) {
            if (o.getID() == userID) {
                o.addOffence();
                return;
            }
        }
        addOffender(new OffenderObject(userID));
    }

    public void setMutedRoleID(long mutedRole) {
        validateRoles();
        this.mutedRoleID = mutedRole;
    }

    public long getMutedRoleID() {
        validateRoles();
        return mutedRoleID;
    }

    public ArrayList<Long> getTrustedRoleIDs() {
        validateRoles();
        return trustedRoleIDs;
    }

    public boolean isRoleCosmetic(long id) {
        validateRoles();
        return cosmeticRoleIDs.contains(id);
    }

    public boolean isRoleModifier(long id) {
        validateRoles();
        return modifierRoleIDs.contains(id);
    }

    public boolean isRoleTrusted(long id) {
        validateRoles();
        return trustedRoleIDs.contains(id);
    }

    public boolean isRoleReward(long id) {
        validateRoles();
        if (id == topTenRoleID) {
            return true;
        }
        ArrayList<Long> ids = rewardRoles.stream().map(RewardRoleObject::getRoleID).collect(Collectors.toCollection(ArrayList::new));
        return ids.contains(id);
    }

    public int getMaxMentionLimit() {
        return maxMentionLimit;
    }

    public void setRateLimit(int rateLimit) {
        messageLimit = rateLimit;
    }

    public RewardRoleObject getRewardRole(long rewardID) {
        validateRoles();
        for (RewardRoleObject r : rewardRoles) {
            if (r.getRoleID() == rewardID) {
                return r;
            }
        }
        return null;
    }

    public RewardRoleObject getLowerReward(RewardRoleObject rewardRole) {
        validateRoles();
        Utility.sortRewards(rewardRoles);
        ArrayList<RewardRoleObject> compareRewards = new ArrayList<>();
        RewardRoleObject lowerReward = null;
        //remove reward roles that are above it or are it.
        for (int i = 0; i < rewardRoles.size(); i++) {
            if (rewardRoles.get(i).getLevel() < rewardRole.getLevel()) {
                compareRewards.add(rewardRoles.get(i));
            }
        }
        if (compareRewards.size() == 0) {
            return null;
        }
        for (RewardRoleObject r : compareRewards) {
            if (lowerReward == null) {
                lowerReward = r;
            } else {
                if (r.getLevel() < lowerReward.getLevel()) {
                    lowerReward = r;
                }
            }
        }
        return lowerReward;
    }

    public ArrayList<RewardRoleObject> getAllRewards(long currentLevel) {
        validateRoles();
        ArrayList<RewardRoleObject> rewards = new ArrayList<>();
        for (RewardRoleObject r : rewardRoles) {
            if (r.getLevel() <= currentLevel) {
                rewards.add(r);
            }
        }
        return rewards;
    }

    public RewardRoleObject lowestReward() {
        validateRoles();
        RewardRoleObject lowest = null;
        for (RewardRoleObject r : rewardRoles) {
            if (lowest == null) {
                lowest = r;
            } else {
                if (lowest.getLevel() > r.getLevel()) {
                    lowest = r;
                }
            }
        }
        return lowest;
    }

    public RewardRoleObject getNextReward(RewardRoleObject rewardRole) {
        validateRoles();
        ArrayList<RewardRoleObject> compareRewards = (ArrayList<RewardRoleObject>) rewardRoles.clone();
        RewardRoleObject lowestRole = null;
        //remove reward roles that are above it or are it.
        for (int i = 0; i < compareRewards.size(); i++) {
            if (compareRewards.get(i).getLevel() <= rewardRole.getLevel()) {
                compareRewards.remove(i);
            }
        }
        if (compareRewards == null || compareRewards.size() == 0) {
            return null;
        }
        for (RewardRoleObject r : compareRewards) {
            if (lowestRole == null) {
                lowestRole = r;
            } else {
                if (r.getLevel() < lowestRole.getLevel()) {
                    lowestRole = r;
                }
            }
        }
        return lowestRole;
    }

    public UserSetting getDefaultLevelMode() {
        return defaultLevelMode;
    }

    public void setDefaultLevelMode(UserSetting defaultLevelMode) {
        this.defaultLevelMode = defaultLevelMode;
    }

    public void setLevelUpMessage(String levelUpMessage) {
        this.levelUpMessage = levelUpMessage;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(String args) {
        joinMessage = args;
    }

    public ArrayList<String> getXpDeniedPrefixes() {
        return xpDeniedPrefixes;
    }

    public RewardRoleObject getCurrentReward(long currentLevel) {
        RewardRoleObject currentReward = null;
        for (RewardRoleObject r : rewardRoles) {
            if (r.getLevel() <= currentLevel) {
                if (currentReward == null) {
                    currentReward = r;
                } else {
                    if (currentReward.getLevel() < r.getLevel()) {
                        currentReward = r;
                    }
                }
            }
        }
        return currentReward;
    }


    public void setPinLimit(int pinLimit) {
        this.pinLimit = pinLimit;
    }

    public OffenderObject getOffender(long userID) {
        for (OffenderObject o : offenders) {
            if (o.getID() == userID) {
                return o;
            }
        }
        return null;
    }

    public void resetOffenders() {
        offenders = new ArrayList<>();
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }
}
