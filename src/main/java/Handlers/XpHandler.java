package Handlers;

import Commands.CommandObject;
import Enums.UserSetting;
import Interfaces.Command;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.GuildContentObject;
import Objects.RewardRoleObject;
import Objects.UserTypeObject;
import POGOs.GuildUsers;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static Enums.UserSetting.*;

/**
 * Created by Vaerys on 29/06/2017.
 */
public class XpHandler {

    public static void doDecay(GuildContentObject content, ZonedDateTime nowUTC) {
        for (UserTypeObject u : content.getGuildUsers().getUsers()) {
            if (u.getLastTalked() != -1) {
                long diff = nowUTC.toEpochSecond() - u.getLastTalked();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                float temp = 0;
                long decay;
                //modifiable min and max decay days needs to be implemented.
                if (days > 7 && days < 30) {
                    //normal xp decay formula
                    temp = (days - 7) * (Globals.avgMessagesPerDay * content.getGuildConfig().xpRate * content.getGuildConfig().xpModifier) / 8;
                } else if (days > 15) {
                    //plateaued xp decay
                    temp = (15 - 7) * (Globals.avgMessagesPerDay * content.getGuildConfig().xpRate * content.getGuildConfig().xpModifier) / 8;
                }
                decay = (long) temp;
                //half decay if you turn you xp gain off but only if it is voluntary
                if (u.getSettings().contains(UserSetting.NO_XP_GAIN)) {
                    decay = decay / 2;
                }
                if (u.getRewardID() != -1) {
                    RewardRoleObject rewardRole = content.getGuildConfig().getRewardRole(u.getRewardID());
                    if (rewardRole != null) {
                        long rewardFloor = rewardRole.getXp() - 100;
                        if (u.getXP() > rewardFloor) {
                            u.setXp(u.getXP() - decay);
                            // your total xp should never reach below 0;
                            if (u.getXP() < 0) {
                                u.setXp(0);
                            }
                            //decay should never lower your total xp below the reward floor
                            if (u.getXP() < rewardFloor) {
                                u.setXp(rewardFloor);
                            }
                            if (u.getXP() == rewardFloor && !u.getSettings().contains(HIT_LEVEL_FLOOR)) {
                                u.getSettings().add(HIT_LEVEL_FLOOR);
                            }
                        }
                        //if your days away is a multiple of 30 you should be checked if you are at the
                        //reward floor, if you are reward decay occurs
                        if (days % 30 == 0 && u.getXP() == rewardFloor) {
                            RewardRoleObject lowerReward = content.getGuildConfig().getLowerReward(rewardRole);
                            if (lowerReward != null) {
                                u.setRewardID(lowerReward.getRoleID());
                            }
                        }
                    }
                }
                //check user's roles and make sure that they have the right roles.
                checkUsersRoles(u.getID(), content);
            }
        }
    }

    public static void doDeacyUser(UserTypeObject u, CommandObject object, ZonedDateTime nowUTC) {
        if (u.getLastTalked() != -1) {
            long diff = nowUTC.toEpochSecond() - u.getLastTalked();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
            float temp = 0;
            long decay;
            //modifiable min and max decay days needs to be implemented.
            if (days > 7 && days < 30) {
                //normal xp decay formula
                temp = (days - 7) * (Globals.avgMessagesPerDay * object.guildConfig.xpRate * object.guildConfig.xpModifier) / 8;
            } else if (days > 30) {
                //plateaued xp decay
                temp = (30 - 7) * (Globals.avgMessagesPerDay * object.guildConfig.xpRate * object.guildConfig.xpModifier) / 8;
            }
            decay = (long) temp;
            //haf decay if you turn you xp gain off but only if it is voluntary
            if (u.getSettings().contains(UserSetting.NO_XP_GAIN)) {
                decay = decay / 2;
            }
            if (u.getRewardID() != -1) {
                RewardRoleObject rewardRole = object.guildConfig.getRewardRole(u.getRewardID());
                if (rewardRole != null) {
                    long rewardFloor = rewardRole.getXp() - 100;
                    if (u.getXP() > rewardFloor) {
                        u.setXp(u.getXP() - decay);
                        // your total xp should never reach below 0;
                        if (u.getXP() < 0) {
                            u.setXp(0);
                        }
                        //decay should never lower your total xp below the reward floor
                        if (u.getXP() < rewardFloor) {
                            u.setXp(rewardFloor);
                        }
                        if (u.getXP() == rewardFloor && !u.getSettings().contains(HIT_LEVEL_FLOOR)) {
                            u.getSettings().add(HIT_LEVEL_FLOOR);
                        }
                    }
                    //if your days away is a multiple of 30 you should be checked if you are at the
                    //reward floor, if you are reward decay occurs
                    if (days % 30 == 0 && u.getXP() == rewardFloor) {
                        RewardRoleObject lowerReward = object.guildConfig.getLowerReward(rewardRole);
                        if (lowerReward != null) {
                            u.setRewardID(lowerReward.getRoleID());
                        }
                    }
                }
            }
            //check user's roles and make sure that they have the right roles.
            checkUsersRoles(u.getID(), object.guildContent);
        }
    }

    public static void checkUsersRoles(String id, GuildContentObject content) {
        //do code.
        UserTypeObject userObject = content.getGuildUsers().getUserByID(id);
        if (userObject == null) {
            return;
        }
        IUser user = Globals.getClient().getUserByID(userObject.getID());
        if (user == null) {
            return;
        }
        List<IRole> userRoles = user.getRolesForGuild(Globals.getClient().getGuildByID(content.getGuildID()));
        //remove all rewardRoles to prep for checking
        for (int i = 0; i < userRoles.size(); i++) {
            for (RewardRoleObject r : content.getGuildConfig().getRewardRoles()) {
                if (r.getRoleID() == userRoles.get(i).getLongID()) {
                    userRoles.remove(i);
                } else if (userRoles.get(i).getLongID() == content.getGuildConfig().topTenRoleID) {
                    userRoles.remove(i);
                }
            }

        }
        if (userObject.getRewardID() != -1) {
            ArrayList<RewardRoleObject> allRewards = content.getGuildConfig().getAllRewards(userObject.getRewardID());
            if (allRewards == null) {
                return;
            }
            for (RewardRoleObject r : allRewards) {
                IRole role = Globals.getClient().getRoleByID(r.getRoleID());
                if (role != null) {
                    userRoles.add(role);
                }
            }
        }
        IRole topTenRole = Globals.client.getGuildByID(content.getGuildID()).getRoleByID(content.getGuildConfig().topTenRoleID);
        if (topTenRole != null) {
            long rank = XpHandler.rank(content.getGuildUsers(), Globals.client.getGuildByID(content.getGuildID()), user.getStringID());
            if (rank <= 10 && rank > 0) {
                userRoles.add(topTenRole);
            }
        }
        //only do a role update if the role count changes
        if (user.getRolesForGuild(Globals.getClient().getGuildByID(content.getGuildID())).size() != userRoles.size()) {
            Utility.roleManagement(user, Globals.client.getGuildByID(content.getGuildID()), userRoles);
        }
    }

    public static void grantXP(CommandObject object) {
        //bots don't get XP
        if (object.author.isBot()) return;

        //creates a profile for the user if they don't already have one.
        UserTypeObject user = new UserTypeObject(object.authorSID);
        if (object.guildUsers.getUserByID(object.authorSID) == null) {
            object.guildUsers.getUsers().add(user);
        } else {
            user = object.guildUsers.getUserByID(object.authorSID);
        }

        //ony do xp checks if module is true
        if (!object.guildConfig.modulePixels) return;
        if (!object.guildConfig.xpGain) return;

        user.lastTalked = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();

        //user setting no xp gain
        if (user.getSettings().contains(NO_XP_GAIN)) return;

        //deny xp if they have the xp denied role.
        for (IRole r : object.authorRoles) {
            if (r.getLongID() == object.guildConfig.xpDeniedRoleID) {
                return;
            }
        }

        //you can only gain xp once per min
        if (object.guildContent.getSpokenUsers().contains(object.authorID)) return;

        //messages that might be considered commands should be ignored.
        ArrayList<String> deniedPrefixes = (ArrayList<String>) object.guildConfig.getXPDeniedPrefixes().clone();
        deniedPrefixes.add(object.guildConfig.getPrefixCommand());
        deniedPrefixes.add(object.guildConfig.getPrefixCC());
        for (String s: deniedPrefixes){
            if (object.message.getContent().startsWith(s)){
                return;
            }
        }

        //you must have typed at least 10 chars to gain xp
        if (object.message.getContent().length() < 10) return;

        //you cannot gain xp in an xpDenied channel
        ArrayList<String> xpChannels = object.guildConfig.getChannelIDsByType(Command.CHANNEL_XP_DENIED);
        if (xpChannels != null && xpChannels.size() > 0) {
            if (xpChannels.contains(object.channelSID)) return;
        }

        //gives them their xp.
        user.addXP(object.guildConfig);
        object.guildContent.getSpokenUsers().add(object.authorID);
        checkIfLevelUp(user, object);
    }

    private static void checkIfLevelUp(UserTypeObject user, CommandObject object) {
        // TODO: 29/07/2017 comment the steps for this code
        boolean leveledUp;
        boolean rankedup = false;
        IMessage selfDestruct = null;
        if (user.getCurrentLevel() == -1) {
            user.setCurrentLevel(xpToLevel(user.getXP()));
            return;
        }
        if (xpToLevel(user.getXP()) >= user.getCurrentLevel() + 1) {
            //react to message that leveled them up
            if (object.guildConfig.reactToLevelUp) {
                if (!object.guildConfig.getChannelIDsByType(Command.CHANNEL_LEVEL_UP_DENIED).contains(object.channelSID)) {
                    IEmoji customEmoji = null;
                    Emoji emoji = EmojiManager.getByUnicode(object.guildConfig.levelUpReaction);
                    boolean foundEmoji = false;
                    for (IGuild g : object.client.getGuilds()) {
                        IEmoji test = g.getEmojiByName(object.guildConfig.levelUpReaction);
                        if (!foundEmoji && test != null) {
                            customEmoji = test;
                            foundEmoji = true;
                        }
                    }
                    try {
                        if (emoji != null) {
                            RequestBuffer.request(() -> object.message.addReaction(emoji));
                        } else if (customEmoji != null) {
                            IEmoji finalCustomEmoji = customEmoji;
                            RequestBuffer.request(() -> object.message.addReaction(finalCustomEmoji));
                        }
                    } catch (DiscordException e) {
                        //do nothing
                    } catch (MissingPermissionsException e) {
                        //also do nothing
                    }
                }
            }

            // level the user up
            user.setCurrentLevel(user.getCurrentLevel() + 1);
            UserSetting userOverride = user.getLevelState();
            StringBuilder levelUpMessage = new StringBuilder(object.guildConfig.levelUpMessage);

            levelUpMessage = new StringBuilder(levelUpMessage.toString().replace("<level>", user.getCurrentLevel() + ""));
            levelUpMessage = new StringBuilder(levelUpMessage.toString().replace("<user>", object.author.mention()));
            //adds a special message if a reward is added.
            for (RewardRoleObject r : object.guildConfig.getRewardRoles()) {
                if (r.getLevel() == user.getCurrentLevel()) {
                    if (user.getSettings().contains(HIT_LEVEL_FLOOR)) {
                        for (int i = 0; i < user.getSettings().size(); i++) {
                            if (user.getSettings().get(i) == HIT_LEVEL_FLOOR) {
                                user.getSettings().remove(i);
                            }
                        }
                        levelUpMessage = new StringBuilder("Welcome Back.\n" + levelUpMessage + "\nYour **" + object.guild.getRoleByID(r.getRoleID()) + "** role has been returned to you.");
                    } else {
                        levelUpMessage.append("\nYou have been granted the **" + object.guild.getRoleByID(r.getRoleID()) + "** role for reaching this level.");
                    }
                    rankedup = true;
                }
            }
            if (userOverride != null) {
                if (object.guildConfig.getDefaultLevelMode() == DONT_SEND_LVLUP && userOverride != SEND_LVLUP_DMS) {
                    userOverride = DONT_SEND_LVLUP;
                }
            } else {
                userOverride = object.guildConfig.defaultLevelMode;
            }
            List<String> levelDenied = object.guildConfig.getChannelIDsByType(Command.CHANNEL_LEVEL_UP_DENIED);
            if (levelDenied != null && levelDenied.contains(object.channelSID)) {
                if (userOverride != SEND_LVLUP_DMS || (userOverride != SEND_LVLUP_RANK_CHANNEL && object.guildConfig.getChannelIDsByType(Command.CHANNEL_PIXELS).get(0) != null)) {
                    userOverride = DONT_SEND_LVLUP;
                }
            }
            switch (userOverride) {
                case SEND_LVLUP_CURRENT_CHANNEL:
                    selfDestruct = Utility.sendMessage(levelUpMessage.toString(), object.channel).get();
                    break;
                case SEND_LVLUP_RANK_CHANNEL:
                    IChannel channel = null;
                    if (object.guildConfig.getChannelIDsByType(Command.CHANNEL_LEVEL_UP) != null) {
                        channel = object.guild.getChannelByID(object.guildConfig.getChannelIDsByType(Command.CHANNEL_LEVEL_UP).get(0));
                    }
                    if (channel != null) {
                        if (channel.getModifiedPermissions(object.botUser).contains(Permissions.ATTACH_FILES)) {
                            if (rankedup) {
                                Utility.sendFileURL(levelUpMessage.toString(), Constants.RANK_UP_IMAGE_URL, channel, false);
                            } else {
                                Utility.sendFileURL(levelUpMessage.toString(), Constants.LEVEL_UP_IMAGE_URL, channel, false);
                            }
                        } else {
                            Utility.sendMessage(levelUpMessage.toString(), channel).get();
                        }
                    } else {
                        selfDestruct = Utility.sendMessage(levelUpMessage.toString(), object.channel).get();
                    }
                    break;
                case SEND_LVLUP_DMS:
                    if (rankedup) {
                        Utility.sendFileURL(levelUpMessage.toString(), Constants.RANK_UP_IMAGE_URL, object.author.getOrCreatePMChannel(), false);
                    } else {
                        Utility.sendFileURL(levelUpMessage.toString(), Constants.LEVEL_UP_IMAGE_URL, object.author.getOrCreatePMChannel(), false);
                    }
                    break;
                case DONT_SEND_LVLUP:
                    break;
                default:
                    break;
            }
            leveledUp = true;
        } else {
            leveledUp = false;
        }
        handleRankUp(object, user);
        if (leveledUp) {
            checkUsersRoles(user.getID(), object.guildContent);
        }
        if (object.guildConfig.selfDestructLevelUps && selfDestruct != null && !rankedup) {
            try {
                //sef destruct messages after 1 min.
                Thread.sleep(60 * 1000);
                Utility.deleteMessage(selfDestruct);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleRankUp(CommandObject object, UserTypeObject user) {
        if (object.guildConfig.getRewardRoles().size() == 0) {
            return;
        }
        RewardRoleObject nextReward;
        if (user.getRewardID() == -1) {
            nextReward = object.guildConfig.lowestReward();
        } else {
            nextReward = object.guildConfig.getNextReward(object.guildConfig.getRewardRole(user.getRewardID()));
        }
        if (user.getCurrentLevel() == nextReward.getLevel()) {
            user.setRewardID(nextReward.getRoleID());
            Utility.roleManagement(object.author, object.guild, nextReward.getRoleID(), true);
        }
        return;
    }

    public static long levelToXP(long level) {
        long nextLvl = level++;
        long xp = (((level) * 20) + (nextLvl * nextLvl) * 4 + 40);
        if ((xp + "").endsWith("4")) {
            xp += 16;
        } else if ((xp + "").endsWith("6")) {
            xp += 4;
        }
        return xp;
    }

    public static long xpToLevel(long xp) {
        long level = 0;
        while (totalXPForLevel(level) <= xp) {
            level++;
        }
        return level - 1;
    }

    public static long totalXPForLevel(long level) {
        long levelxp = 0;
        long levelup = 0;
        while (levelup != level) {
            levelup++;
            levelxp += levelToXP(levelup);
        }
        return levelxp;
    }

    public static long rank(GuildUsers guildUsers, IGuild guild, String userID) {
        UserTypeObject user = guildUsers.getUserByID(userID);
        for (UserSetting s : user.getSettings()) {
            for (UserSetting test : Constants.dontLogStates) {
                if (s == test) {
                    return -1;
                }
            }
        }
        if (guild.getUserByID(userID) == null) {
            return -1;
        }
        if (user.getXP() == 0) {
            return -1;
        }
        long rank = 0;
        ArrayList<UserTypeObject> users = (ArrayList<UserTypeObject>) guildUsers.getUsers().clone();
        Utility.sortUserObjects(users, false);
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getID().equals(userID)) {
                return rank + 1;
            } else {
                boolean hiderank = false;
                for (UserSetting s : users.get(i).getSettings()) {
                    for (UserSetting test : Constants.dontLogStates) {
                        if (s == test) {
                            hiderank = true;
                        }
                    }
                }
                if (guild.getUserByID(users.get(i).getID()) != null && !hiderank && user.getXP() != users.get(i).getXP()) {
                    rank++;
                }
            }
        }
        return rank;
    }

    public static long totalRanked(CommandObject command) {
        long totalRanked = 0;
        for (UserTypeObject u : command.guildUsers.getUsers()) {
            boolean hiderank = false;
            for (UserSetting s : u.getSettings()) {
                for (UserSetting test : Constants.dontLogStates) {
                    if (s == test) {
                        hiderank = true;
                    }
                }
            }
            if (command.guild.getUserByID(u.getID()) != null) {
                if (u.getXP() != 0) {
                    if (!hiderank) {
                        totalRanked++;
                    }
                }
            }
        }
        return totalRanked;
    }

    public static int getRewardCount(CommandObject object, IUser user) {
        UserTypeObject userObject = object.guildUsers.getUserByID(user.getStringID());
        ArrayList<RewardRoleObject> allRewards = object.guildConfig.getAllRewards(userObject.getRewardID());
        if (allRewards == null) {
            return 0;
        } else {
            if (allRewards.size() > 4) {
                return 4;
            } else {
                return allRewards.size();
            }
        }
    }

    public static RewardRoleObject rewardForLevel(long currentLevel, CommandObject object) {
        Utility.sortRewards(object.guildConfig.getRewardRoles());
        RewardRoleObject reward = null;
        for (RewardRoleObject r : object.guildConfig.getRewardRoles()) {
            if (reward == null) {
                reward = r;
                if (reward.getLevel() > currentLevel) {
                    return null;
                }
            } else {
                if (r.getLevel() <= currentLevel) {
                    reward = r;
                }
            }
        }
        return reward;
    }
}

