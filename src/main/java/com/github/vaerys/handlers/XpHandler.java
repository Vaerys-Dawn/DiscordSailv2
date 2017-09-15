package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.general.ProfileSettings;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.XRequestBuffer;
import com.github.vaerys.objects.RewardRoleObject;
import com.github.vaerys.pogos.GuildUsers;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import static com.github.vaerys.enums.UserSetting.*;

/**
 * Created by Vaerys on 29/06/2017.
 */
public class XpHandler {

    final static Logger logger = LoggerFactory.getLogger(XpHandler.class);

    public static void doDecay(GuildObject content, ZonedDateTime nowUTC) {
        for (ProfileObject u : content.users.getProfiles()) {
            if (u.getLastTalked() != -1 && !u.getSettings().contains(DONT_DECAY)) {
                long diff = nowUTC.toEpochSecond() - u.getLastTalked();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
                float temp = 0;
                long decay;
                u.setCurrentLevel(XpHandler.xpToLevel(u.getXP()));
                //modifiable min and max decay days needs to be implemented.
                if (days > 7 && days < 30) {
                    //normal xp decay formula
                    temp = (days - 7) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8;
                } else if (days > 15) {
                    //plateaued xp decay
                    temp = (15 - 7) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8;
                }
                decay = (long) temp;
                //half decay if you turn you xp gain off but only if it is voluntary
                if (u.getSettings().contains(UserSetting.NO_XP_GAIN)) {
                    decay = decay / 2;
                }
                if (XpHandler.getRewardCount(content, u.getUserID()) != 0) {
                    long pseudoLevel = xpToLevel(u.getXP() + 120);
                    RewardRoleObject rewardRole = content.config.getCurrentReward(pseudoLevel);
                    if (rewardRole != null) {

                        long rewardFloor = rewardRole.getXp() - 100;
                        if (u.getXP() > rewardFloor) {
                            u.setXp(u.getXP() - decay);
                            // your total xp should never reach below 0;
                            if (u.getXP() < 0) {
                                u.setXp(0);
                            }
                            //decay should never lower your total xp below the reward floor.
                            if (u.getXP() < rewardFloor) {
                                u.setXp(rewardFloor);
                            }
                            //if user is at level floor add setting.
                            if (u.getXP() == rewardFloor && !u.getSettings().contains(HIT_LEVEL_FLOOR)) {
                                u.getSettings().add(HIT_LEVEL_FLOOR);
                            }
                        }
                        //if your days away is a multiple of 30 you should be checked if you are at the
                        //reward floor, if you are reward decay occurs
                        if (days % 30 == 0 && u.getXP() == rewardFloor) {
                            u.setXp(u.getXP() - 100);
                        }
                    }
                }
                //check user's roles and make sure that they have the right roles.
                checkUsersRoles(u.getUserID(), content);
            }
        }
    }

    //for testing purposes only
    public static void doDeacyUser(ProfileObject u, GuildObject content, long days) {
        if (u.getLastTalked() != -1) {
            float temp = 0;
            long decay;
            u.setCurrentLevel(XpHandler.xpToLevel(u.getXP()));
            //modifiable min and max decay days needs to be implemented.
            if (days > 7 && days < 30) {
                //normal xp decay formula
                temp = (days - 7) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8;
            } else if (days > 15) {
                //plateaued xp decay
                temp = (15 - 7) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8;
            }
            decay = (long) temp;
            //half decay if you turn you xp gain off but only if it is voluntary
            if (u.getSettings().contains(UserSetting.NO_XP_GAIN)) {
                decay = decay / 2;
            }
            if (XpHandler.getRewardCount(content, u.getUserID()) != 0) {
                long pseudoLevel = xpToLevel(u.getXP() + 120);
                RewardRoleObject rewardRole = content.config.getCurrentReward(pseudoLevel);
                if (rewardRole != null) {
                    long rewardFloor = rewardRole.getXp() - 100;
                    if (u.getXP() > rewardFloor) {
                        u.setXp(u.getXP() - decay);
                        // your total xp should never reach below 0;
                        if (u.getXP() < 0) {
                            u.setXp(0);
                        }
                        //decay should never lower your total xp below the reward floor.
                        if (u.getXP() < rewardFloor) {
                            u.setXp(rewardFloor);
                        }
                        //if user is at level floor add setting.
                        if (u.getXP() == rewardFloor && !u.getSettings().contains(HIT_LEVEL_FLOOR)) {
                            u.getSettings().add(HIT_LEVEL_FLOOR);
                        }
                    }
                    //if your days away is a multiple of 30 you should be checked if you are at the
                    //reward floor, if you are reward decay occurs
                    if (days % 30 == 0 && u.getXP() == rewardFloor) {
                        u.setXp(u.getXP() - 100);
                    }
                }
            }
            //check user's roles and make sure that they have the right roles.
        }
        checkUsersRoles(u.getUserID(), content);
    }

    public static void checkUsersRoles(long id, GuildObject content) {
        //do code.
        ProfileObject userObject = content.users.getUserByID(id);
        if (userObject == null) {
            return;
        }
        if (userObject.getSettings().contains(DENY_AUTO_ROLE)) return;

        IUser user = Globals.getClient().getUserByID(userObject.getUserID());
        if (user == null) {
            return;
        }
        List<IRole> userRoles = user.getRolesForGuild(content.get());
        //remove all rewardRoles to prep for checking.
        ListIterator iterator = userRoles.listIterator();
        while (iterator.hasNext()) {
            IRole role = (IRole) iterator.next();
            if (content.config.isRoleReward(role.getLongID())) {
                iterator.remove();
            }
        }
        //add all roles that the user should have.
        ArrayList<RewardRoleObject> allRewards = content.config.getAllRewards(userObject.getCurrentLevel());
        for (RewardRoleObject r : allRewards) {
            userRoles.add(Globals.getClient().getRoleByID(r.getRoleID()));
        }
        //add the top ten role if they should have it.
        IRole topTenRole = content.get().getRoleByID(content.config.topTenRoleID);
        if (topTenRole != null) {
            long rank = XpHandler.rank(content.users, content.get(), user.getLongID());
            if (rank <= 10 && rank > 0) {
                userRoles.add(topTenRole);
            }
        }
        //only do a role update if the role count changes
        List<IRole> currentRoles = user.getRolesForGuild(content.get());
        if (!currentRoles.containsAll(userRoles) || currentRoles.size() != userRoles.size()) {
            Utility.roleManagement(user, content.get(), userRoles);
        }
    }

    public static void grantXP(CommandObject object) {
        //bots don't get XP
        if (object.user.get().isBot()) return;

        //creates a profile for the user if they don't already have one.
        ProfileObject user = new ProfileObject(object.user.longID);
        if (object.guild.users.getUserByID(object.user.longID) == null) {
            object.guild.users.getProfiles().add(user);
        } else {
            user = object.guild.users.getUserByID(object.user.longID);
        }

        //ony do xp checks if module is true
        if (!object.guild.config.modulePixels) return;
        if (!object.guild.config.xpGain) return;

        user.lastTalked = object.message.get().getTimestamp().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();

        //user setting no xp gain
        if (user.getSettings().contains(NO_XP_GAIN)) return;

        //deny xp if they have the xp denied role.
        for (IRole r : object.user.roles) {
            if (r.getLongID() == object.guild.config.xpDeniedRoleID) {
                return;
            }
        }

        //you can only gain xp once per min
        if (object.guild.getSpokenUsers().contains(object.user.longID)) return;

        //messages that might be considered commands should be ignored.
        ArrayList<String> deniedPrefixes = (ArrayList<String>) object.guild.config.getXpDeniedPrefixes().clone();
        deniedPrefixes.add(object.guild.config.getPrefixCommand());
        deniedPrefixes.add(object.guild.config.getPrefixCC());
        for (String s : deniedPrefixes) {
            if (object.message.get().getContent().startsWith(s)) {
                return;
            }
        }

        //you must have typed at least 10 chars to gain xp
        if (object.message.get().getContent().length() < 10) return;

        //you cannot gain xp in an xpDenied channel
        List<IChannel> xpChannels = object.guild.config.getChannelsByType(Command.CHANNEL_XP_DENIED, object.guild);
        if (xpChannels.size() > 0) {
            if (xpChannels.contains(object.channel.get())) {
                return;
            }
        }

        //gives them their xp.
        user.addXP(object.guild.config);
        object.guild.getSpokenUsers().add(object.user.longID);
        checkIfLevelUp(user, object);
    }

    private static void checkIfLevelUp(ProfileObject user, CommandObject object) {
        // TODO: 29/07/2017 comment the steps for this code
        boolean leveledUp;
        boolean rankedup = false;
        IMessage selfDestruct = null;
        if (user.getCurrentLevel() == -1) {
            user.setCurrentLevel(xpToLevel(user.getXP()));
            return;
        }
        long newLevel = xpToLevel(user.getXP());
        long nextLevel = user.getCurrentLevel() + 1;
        if (newLevel >= nextLevel) {
            //react to message that leveled them up
            reactTolevelUp(object);

            // level the user up
            user.setCurrentLevel(xpToLevel(user.getXP()));
            UserSetting userOverride = user.getLevelState();
            StringBuilder levelUpMessage = new StringBuilder(object.guild.config.levelUpMessage);

            levelUpMessage = TagHandler.prepLevelUpMessage(levelUpMessage, object.guild, object.user);

            //adds a special message if a reward is added.
            for (RewardRoleObject r : object.guild.config.getRewardRoles()) {
                if (r.getLevel() == user.getCurrentLevel()) {
                    if (user.getSettings().contains(DENY_AUTO_ROLE)) {
                        break;
                    }
                    if (user.getSettings().contains(HIT_LEVEL_FLOOR)) {
                        for (int i = 0; i < user.getSettings().size(); i++) {
                            if (user.getSettings().get(i) == HIT_LEVEL_FLOOR) {
                                user.getSettings().remove(i);
                            }
                        }
                        levelUpMessage = new StringBuilder("Welcome Back.\n" + levelUpMessage + "\nYour **" + object.guild.get().getRoleByID(r.getRoleID()) + "** role has been returned to you.");
                    } else {
                        levelUpMessage.append("\nYou have been granted the **" + object.guild.get().getRoleByID(r.getRoleID()) + "** role for reaching this level.");
                    }
                    rankedup = true;
                }
            }
            //if the user only just reached level 1 send them a message telling them about the pixelSettings command.
            if (user.getCurrentLevel() == 1) {
                levelUpMessage.append("\n\n> If you want to change where these messages are sent or want to remove them completely you can change that with `" + new ProfileSettings().getUsage(object) + "`.");
            }

            if (userOverride != null) {
                if (object.guild.config.getDefaultLevelMode() == DONT_SEND_LVLUP && userOverride != SEND_LVLUP_DMS) {
                    userOverride = DONT_SEND_LVLUP;
                }
            } else {
                userOverride = object.guild.config.defaultLevelMode;
            }
            List<IChannel> levelDenied = object.guild.config.getChannelsByType(Command.CHANNEL_LEVEL_UP_DENIED, object.guild);
            List<IChannel> levelUpChannel = object.guild.config.getChannelsByType(Command.CHANNEL_LEVEL_UP, object.guild);
            if (levelDenied.size() != 0 && levelDenied.contains(object.channel.get())) {
                if (userOverride != SEND_LVLUP_DMS || userOverride != SEND_LVLUP_RANK_CHANNEL && levelUpChannel.size() == 0) {
                    userOverride = DONT_SEND_LVLUP;
                }
            }
            switch (userOverride) {
                case SEND_LVLUP_CURRENT_CHANNEL:
                    selfDestruct = Utility.sendMessage(levelUpMessage.toString(), object.channel.get()).get();
                    break;
                case SEND_LVLUP_RANK_CHANNEL:
                    IChannel channel = null;
                    if (levelUpChannel.size() != 0) {
                        channel = levelUpChannel.get(0);
                    }
                    if (channel != null) {
                        if (channel.getModifiedPermissions(object.client.bot).contains(Permissions.ATTACH_FILES)) {
                            if (rankedup) {
                                Utility.sendFileURL(levelUpMessage.toString(), Constants.RANK_UP_IMAGE_URL, channel, false);
                            } else {
                                Utility.sendFileURL(levelUpMessage.toString(), Constants.LEVEL_UP_IMAGE_URL, channel, false);
                            }
                        } else {
                            Utility.sendMessage(levelUpMessage.toString(), channel).get();
                        }
                    } else {
                        selfDestruct = Utility.sendMessage(levelUpMessage.toString(), object.channel.get()).get();
                    }
                    break;
                case SEND_LVLUP_DMS:
                    if (rankedup) {
                        Utility.sendFileURL(levelUpMessage.toString(), Constants.RANK_UP_IMAGE_URL, object.user.get().getOrCreatePMChannel(), false);
                    } else {
                        Utility.sendFileURL(levelUpMessage.toString(), Constants.LEVEL_UP_IMAGE_URL, object.user.get().getOrCreatePMChannel(), false);
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
        if (leveledUp) {
            checkUsersRoles(user.getUserID(), object.guild);
        }
        if (object.guild.config.selfDestructLevelUps && selfDestruct != null && !rankedup) {
            try {
                //keep the message around just a little longer for their first level up.
                if (user.getCurrentLevel() == 1) {
                    Thread.sleep(60 * 1000);
                }
                //self destruct messages after 1 min.
                Thread.sleep(60 * 1000);
                Utility.deleteMessage(selfDestruct);
            } catch (InterruptedException e) {
                Utility.sendStack(e);
            }
        }

    }

    private static void reactTolevelUp(CommandObject object) {
        if (object.guild.config.reactToLevelUp) {
            ProfileObject user = object.guild.users.getUserByID(object.user.longID);
            if (user != null && user.getSettings().contains(UserSetting.NO_LEVEL_UP_REACTIONS)) {
                return;
            }
            IEmoji customEmoji = null;
            Emoji emoji = EmojiManager.getByUnicode(object.guild.config.levelUpReaction);
            for (IGuild g : object.client.get().getGuilds()) {
                IEmoji test = null;
                try {
                    long emojiId = Long.parseLong(object.guild.config.levelUpReaction);
                    test = g.getEmojiByID(emojiId);
                    if (test != null) {
                        customEmoji = test;
                        break;
                    }
                } catch (NumberFormatException e) {
                    test = g.getEmojiByName(object.guild.config.levelUpReaction);
                    if (test != null) {
                        customEmoji = test;
                        break;
                    }
                }
            }
            IEmoji finalCustomEmoji = customEmoji;
            XRequestBuffer.request(() -> {
                try {
                    if (emoji != null) {
                        object.message.get().addReaction(emoji);
                    } else if (finalCustomEmoji != null) {
                        object.message.get().addReaction(finalCustomEmoji);
                    }
                } catch (DiscordException e) {
                    logger.error("Discord didn't like the custom emoji that guild with id: " + object.guild.longID + " chose for a levelUp react.");
                    if (emoji != null) {
                        logger.error(emoji.getUnicode());
                    } else if (finalCustomEmoji != null) {
                        logger.error(finalCustomEmoji.toString());
                    }
                    //do nothing
                } catch (MissingPermissionsException e) {
                    //also do nothing
                }
            });
        }
    }

    public static long levelToXP(long level) {
        long nextLvl = level + 1;
        long xp = (((nextLvl) * 20) + (level * level) * 4 + 40);
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

    public static long rank(GuildUsers guildUsers, IGuild guild, long userID) {
        ProfileObject user = guildUsers.getUserByID(userID);
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
        ArrayList<ProfileObject> users = (ArrayList<ProfileObject>) guildUsers.getProfiles().clone();
        Utility.sortUserObjects(users, false);
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID() == userID) {
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
                if (guild.getUserByID(users.get(i).getUserID()) != null && !hiderank && user.getXP() != users.get(i).getXP()) {
                    rank++;
                }
            }
        }
        return rank;
    }

    public static long totalRanked(CommandObject command) {
        long totalRanked = 0;
        for (ProfileObject u : command.guild.users.getProfiles()) {
            boolean hiderank = false;
            for (UserSetting s : u.getSettings()) {
                for (UserSetting test : Constants.dontLogStates) {
                    if (s == test) {
                        hiderank = true;
                    }
                }
            }
            if (command.guild.get().getUserByID(u.getUserID()) != null) {
                if (u.getXP() != 0) {
                    if (!hiderank) {
                        totalRanked++;
                    }
                }
            }
        }
        return totalRanked;
    }

    public static int getRewardCount(GuildObject object, long userID) {
        ProfileObject userObject = object.users.getUserByID(userID);
        if (userObject == null) {
            return 0;
        }
        ArrayList<RewardRoleObject> allRewards = object.config.getAllRewards(userObject.getCurrentLevel());
        if (allRewards.size() == 0) {
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
        Utility.sortRewards(object.guild.config.getRewardRoles());
        RewardRoleObject reward = null;
        for (RewardRoleObject r : object.guild.config.getRewardRoles()) {
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

