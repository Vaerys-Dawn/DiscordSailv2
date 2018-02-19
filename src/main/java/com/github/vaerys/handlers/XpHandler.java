package com.github.vaerys.handlers;

import static com.github.vaerys.main.UserSetting.DENY_AUTO_ROLE;
import static com.github.vaerys.main.UserSetting.DONT_SEND_LVLUP;
import static com.github.vaerys.main.UserSetting.HIT_LEVEL_FLOOR;
import static com.github.vaerys.main.UserSetting.NO_XP_GAIN;
import static com.github.vaerys.main.UserSetting.SEND_LVLUP_DMS;
import static com.github.vaerys.main.UserSetting.SEND_LVLUP_RANK_CHANNEL;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.general.ProfileSettings;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.UserSetting;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.objects.RewardRoleObject;
import com.github.vaerys.pogos.GuildUsers;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.TagObject;
import com.github.vaerys.templates.TagType;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

/**
 * Created by Vaerys on 29/06/2017.
 */
public class XpHandler {

    final static Logger logger = LoggerFactory.getLogger(XpHandler.class);

    public static void doDecay(GuildObject content) {
        if (!content.config.modulePixels || !content.config.xpDecay) return;
        for (ProfileObject u : content.users.getProfiles()) {
            long days = u.daysDecayed(content);
            if (7 > days) {
                long decay;
                u.setCurrentLevel(XpHandler.xpToLevel(u.getXP()));
                //modifiable min and max decay days needs to be implemented.
                if (days > 15) {
                    //plateaued xp decay
                    decay = (long) ((15 - 7) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8);
                } else if (days > 90) {
                    // kill the xp after 90 days of absences
                    decay = (long) ((90 - 7) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8);
                } else {
                    //normal xp decay formula
                    decay = (long) ((days - 7) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8);
                }
                //half decay if you turn you xp gain off but only if it is voluntary
                if (u.getSettings().contains(UserSetting.NO_XP_GAIN)) decay = decay / 2;
                //half decay for patreon supporters
                if (u.getUser(content).isPatron) decay = decay / 2;
                //reward handlers
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

//    //for testing purposes only
//    public static void doDeacyUser(ProfileObject u, GuildObject content, long days) {
//        if (u.getLastTalked() != -1) {
//            float temp = 0;
//            long decay;
//            u.setCurrentLevel(XpHandler.xpToLevel(u.getXP()));
//            //modifiable min and max decay days needs to be implemented.
//            if (days > 7 && days < 30) {
//                //normal xp decay formula
//                temp = (days - 7) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8;
//            } else if (days > 20) {
//                //plateaued xp decay
//                temp = (8) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8;
//            }
//            decay = (long) temp;
//            //half decay if you turn you xp gain off but only if it is voluntary
//            if (u.getSettings().contains(UserSetting.NO_XP_GAIN)) {
//                decay = decay / 2;
//            }
//            if (XpHandler.getRewardCount(content, u.getUserID()) != 0) {
//                long pseudoLevel = xpToLevel(u.getXP() + 120);
//                RewardRoleObject rewardRole = content.config.getCurrentReward(pseudoLevel);
//                if (rewardRole != null) {
//                    long rewardFloor = rewardRole.getXp() - 100;
//                    if (u.getXP() > rewardFloor) {
//                        u.setXp(u.getXP() - decay);
//                        // your total xp should never reach below 0;
//                        if (u.getXP() < 0) {
//                            u.setXp(0);
//                        }
//                        //decay should never lower your total xp below the reward floor.
//                        if (u.getXP() < rewardFloor) {
//                            u.setXp(rewardFloor);
//                        }
//                        //if user is at level floor add setting.
//                        if (u.getXP() == rewardFloor && !u.getSettings().contains(HIT_LEVEL_FLOOR)) {
//                            u.getSettings().add(HIT_LEVEL_FLOOR);
//                        }
//                    }
//                    //if your days away is a multiple of 30 you should be checked if you are at the
//                    //reward floor, if you are reward decay occurs
//                    if (days % 30 == 0 && u.getXP() == rewardFloor) {
//                        u.setXp(u.getXP() - 100);
//                    }
//                }
//            }
//            //check user's roles and make sure that they have the right roles.
//        }
//        checkUsersRoles(u.getUserID(), content);
//    }

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
            RequestHandler.roleManagement(user, content.get(), userRoles);
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

        user.lastTalked = object.message.getTimestamp().toEpochSecond();

        //ony do xp checks if module is true
        if (!object.guild.config.modulePixels) return;
        if (!object.guild.config.xpGain) return;


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

        //you must have typed at least 10 chars to gain xp and doesn't contain an image.
        if (object.message.get().getContent().length() < 10 &&
                object.message.get().getAttachments().isEmpty()) return;

        //you cannot gain xp in an xpDenied channel
        List<IChannel> xpChannels = object.guild.getChannelsByType(ChannelSetting.XP_DENIED);
        if (xpChannels.size() > 0) {
            if (xpChannels.contains(object.channel.get())) {
                return;
            }
        }

        // check level cap, don't grant xp to level-capped users
        if (object.user.getProfile(object.guild) != null) {
            if (object.user.getProfile(object.guild).getXP() >= Constants.PIXELS_CAP) {
                user.setXp(Constants.PIXELS_CAP);
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
            String levelUpMessage = object.guild.config.levelUpMessage;

            //run tags
            for (TagObject t : TagList.getType(TagType.LEVEL)) {
                levelUpMessage = t.handleTag(levelUpMessage, object, "");
            }

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
                        levelUpMessage = "Welcome Back.\n" + levelUpMessage + "\nYour **@" + object.guild.getRoleByID(r.getRoleID()).getName() + "** role has been returned to you.";
                    } else {
                        levelUpMessage += "\nYou have been granted the **@" + object.guild.getRoleByID(r.getRoleID()).getName() + "** role for reaching this level.";
                    }
                    rankedup = true;
                }
            }


            String loggingType = rankedup ? "RANKUP" : "LEVELUP";
            object.guild.sendDebugLog(object, "PIXELS", loggingType, user.getCurrentLevel() + "");


            //if the user only just reached level 1 send them a message telling them about the pixelSettings command.
            if (user.getCurrentLevel() == 1) {
                levelUpMessage += "\n\n> If you want to change where these messages are sent or want to remove them completely you can change that with `" + new ProfileSettings().getUsage(object) + "`.";
            }

            if (userOverride != null) {
                if (object.guild.config.getDefaultLevelMode() == DONT_SEND_LVLUP && userOverride != SEND_LVLUP_DMS) {
                    userOverride = DONT_SEND_LVLUP;
                }
            } else {
                userOverride = object.guild.config.defaultLevelMode;
            }
            List<IChannel> levelDenied = object.guild.getChannelsByType(ChannelSetting.LEVEL_UP_DENIED);
            List<IChannel> levelUpChannel = object.guild.getChannelsByType(ChannelSetting.LEVEL_UP);
            if (levelDenied.size() != 0 && levelDenied.contains(object.channel.get())) {
                if (userOverride != SEND_LVLUP_DMS || userOverride != SEND_LVLUP_RANK_CHANNEL && levelUpChannel.size() == 0) {
                    userOverride = DONT_SEND_LVLUP;
                }
            }
            switch (userOverride) {
                case SEND_LVLUP_CURRENT_CHANNEL:
                    selfDestruct = RequestHandler.sendMessage(levelUpMessage.toString(), object.channel.get()).get();
                    break;
                case SEND_LVLUP_RANK_CHANNEL:
                    IChannel channel = null;
                    if (levelUpChannel.size() != 0) {
                        channel = levelUpChannel.get(0);
                    }
                    if (channel != null) {
                        if (channel.getModifiedPermissions(object.client.bot.get()).contains(Permissions.ATTACH_FILES)) {
                            if (rankedup) {
                                RequestHandler.sendEmbededImage(levelUpMessage.toString(), Constants.RANK_UP_IMAGE_URL, channel);
                            } else {
                                RequestHandler.sendEmbededImage(levelUpMessage.toString(), Constants.LEVEL_UP_IMAGE_URL, channel);
                            }
                        } else {
                            RequestHandler.sendMessage(levelUpMessage.toString(), channel).get();
                        }
                    } else {
                        selfDestruct = RequestHandler.sendMessage(levelUpMessage.toString(), object.channel.get()).get();
                    }
                    break;
                case SEND_LVLUP_DMS:
                    if (rankedup) {
                        RequestHandler.sendEmbededImage(levelUpMessage.toString(), Constants.RANK_UP_IMAGE_URL, object.user.get().getOrCreatePMChannel());
                    } else {
                        RequestHandler.sendEmbededImage(levelUpMessage.toString(), Constants.LEVEL_UP_IMAGE_URL, object.user.get().getOrCreatePMChannel());
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
                RequestHandler.deleteMessage(selfDestruct);
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
            boolean found = false;
            for (IGuild g : object.client.get().getGuilds()) {
                IEmoji test;
                try {
                    long emojiId = Long.parseLong(object.guild.config.levelUpReaction);
                    test = g.getEmojiByID(emojiId);
                    if (test != null) {
                        customEmoji = test;
                        found = true;
                    }
                } catch (NumberFormatException e) {
                    test = g.getEmojiByName(object.guild.config.levelUpReaction);
                    if (test != null) {
                        customEmoji = test;
                        found = true;
                        break;
                    }
                }
            }
            if (object.guild.config.levelUpReaction.equalsIgnoreCase("null")) return;
            if (found == false) {
                IChannel adminChannel = object.guild.getChannelByType(ChannelSetting.ADMIN);
                if (adminChannel == null) adminChannel = object.channel.get();
                RequestHandler.sendMessage("> The current emoji set to be used for level up reactions is invalid and needs to be updated.", adminChannel);
                return;
            }
            IEmoji finalCustomEmoji = customEmoji;
            RequestBuffer.request(() -> {
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
                    } else {
                        throw e;
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

    public static boolean isUnRanked(long userID, GuildUsers users, IGuild guild) {
        ProfileObject user = users.getUserByID(userID);
        GuildObject guildObject = Globals.getGuildContent(guild.getLongID());
        if (user == null) {
            return true;
        }
        if (guild.getUserByID(userID) == null) {
            return true;
        }
        if (user.getXP() == 0) {
            return true;
        }
        for (UserSetting s : user.getSettings()) {
            for (UserSetting test : Constants.dontLogStates) {
                if (s == test) {
                    return true;
                }
            }
        }
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        long diff = now.toEpochSecond() - user.getLastTalked();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
        if (days > 14 && guildObject.config.xpDecay && !user.getSettings().contains(UserSetting.DONT_DECAY)) {
            return true;
        }
        return false;
    }


    public static long rank(GuildUsers guildUsers, IGuild guild, long userID) {
        if (isUnRanked(userID, guildUsers, guild)) {
            return -1;
        }

        ProfileObject user = guildUsers.getUserByID(userID);

        //rank calc
        long rank = 0;
        ArrayList<ProfileObject> users = (ArrayList<ProfileObject>) guildUsers.getProfiles().clone();
        //sort so that can accurately check rank
        Utility.sortUserObjects(users, false);

        //for all the users
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID() == userID) {
                return rank + 1;
            } else {
                boolean hideRank = isUnRanked(users.get(i).getUserID(), guildUsers, guild);
                if (!hideRank && user.getXP() != users.get(i).getXP()) {
                    rank++;
                }
            }
        }
        // this should never occur
        return rank;
    }

    public static long totalRanked(CommandObject command) {
        long totalRanked = 0;
        for (ProfileObject u : command.guild.users.getProfiles()) {
            boolean hideRank;
            UserObject object = new UserObject(command.guild.getUserByID(u.getUserID()), command.guild);
            hideRank = !object.showRank(command.guild);
            if (command.guild.getUserByID(u.getUserID()) != null) {
                if (u.getXP() != 0 && !hideRank) {
                    totalRanked++;
                }
            }
        }
        return totalRanked;
    }

    public static int getRewardCount(GuildObject object, long userID) {
        if (!object.config.modulePixels) return 4;
        ProfileObject userObject = object.users.getUserByID(userID);
        if (userObject == null) {
            return 0;
        }
        List<RewardRoleObject> userRewards = object.config.getAllRewards(userObject.getCurrentLevel());
        List<RewardRoleObject> allRewards = object.config.getRewardRoles();
        if (userRewards.size() == 0) {
            return 0;
        } else {
            if (allRewards.size() < 4) {
                float value = (userRewards.size() * 100.0f) / allRewards.size();
                value = value / 25;
                return (int) value;
            } else {
                if (userRewards.size() > 4) {
                    return 4;
                } else {
                    return userRewards.size();
                }
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

