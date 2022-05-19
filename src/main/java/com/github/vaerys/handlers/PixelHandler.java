package com.github.vaerys.handlers;

import com.github.vaerys.commands.general.ProfileSettings;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.*;
import com.github.vaerys.objects.adminlevel.RewardRoleObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.pogos.GuildUsers;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.TagObject;
import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.github.vaerys.enums.UserSetting.*;

/**
 * Created by Vaerys on 29/06/2017.
 */
public class PixelHandler {

    final static Logger logger = LoggerFactory.getLogger(PixelHandler.class);

    private static final ScheduledExecutorService selfDestruct = Executors.newSingleThreadScheduledExecutor();

    /***
     * Handler for performing pixel decay.
     *
     * @param content The Guild decay is being checked on ({@link GuildObject})
     * @param user    the profile object for the globalUser that is having decay tested.
     */
    public static void doDecay(GuildObject content, ProfileObject user) {
        if (user.getUser(content) instanceof EmptyUserObject) return;
        long days = user.daysDecayed(content);
        if (7 < days) {
            long decay;
            user.setCurrentLevel(PixelHandler.xpToLevel(user.getXP()));
            //modifiable min and max decay days needs to be implemented.
            if (days > 90) {
                // kill the xp after 90 days of absences
                decay = (long) ((90 - 7) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8);
            } else if (days > 15) {
                //plateaued xp decay
                decay = (long) ((15 - 7) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8);
            } else {
                //normal xp decay formula
                decay = (long) ((days - 7) * (Globals.avgMessagesPerDay * content.config.xpRate * content.config.xpModifier) / 8);
            }
            //half decay if you turn you xp gain off but only if it is voluntary
            if (user.getSettings().contains(UserSetting.NO_XP_GAIN)) decay = decay / 2;
            //half decay for patreon supporters
            if (user.getUser(content).isPatron) decay = decay / 2;
            //reward handlers
            if (PixelHandler.getRewardCount(content, user.getUserID()) != 0) {
                long pseudoLevel = xpToLevel(user.getXP() + 120);
                RewardRoleObject rewardRole = content.config.getCurrentReward(pseudoLevel);
                if (rewardRole != null) {

                    long rewardFloor = rewardRole.getXp() - 100;
                    if (user.getXP() > rewardFloor) {
                        user.setXp(user.getXP() - decay);
                        //your total xp should never reach below 0
                        if (user.getXP() < 0) {
                            user.setXp(0);
                        }
                        //decay should never lower your total xp below the reward floor.
                        if (user.getXP() < rewardFloor) {
                            user.setXp(rewardFloor);
                        }
                        //if globalUser is at level floor add setting.
                        if (user.getXP() == rewardFloor && !user.getSettings().contains(HIT_LEVEL_FLOOR)) {
                            user.getSettings().add(HIT_LEVEL_FLOOR);
                        }
                    }
                    //if your days away is a multiple of 30 you should be checked if you are at the
                    //reward floor, if you are reward decay occurs
                    if (days % 30 == 0 && user.getXP() == rewardFloor) {
                        user.setXp(user.getXP() - 100);
                    }
                }
            }
        }
    }

    /***
     * Handler for performing automatic role allocation
     *
     * @param id      the globalUser ID of the globalUser that is having their roles checked.
     * @param content a wrapper around the Guild API object that contains extra information about the guild.
     */
    public static void checkUsersRoles(long id, GuildObject content) {
        //do code.
        ProfileObject userObject = content.users.getUserByID(id);
        if (userObject == null) {
            return;
        }
        //if denyAutoRole exit
        if (userObject.getSettings().contains(DENY_AUTO_ROLE)) return;

        //check if globalUser is on the server.
        Member user = content.get().getMemberById(id);
        if (user == null) {
            return;
        }

        Set<Role> newRoleList = content.config.updateRewardRoles(userObject, content, new HashSet<>(user.getRoles()));
        //only do a role update if the role count changes
        List<Role> currentRoles = user.getRoles();
        if (!currentRoles.containsAll(newRoleList) || currentRoles.size() != newRoleList.size()) {
            RequestHandler.roleManagement(user, content.get(), newRoleList);
        }
    }

    /***
     * handler for granting users pixels.
     *
     * @param object the container for all relevant API content.
     */
    public static void grantXP(CommandObject object) {
        //bots don't get XP
        if (object.user.get().isBot()) return;

        //creates a profile for the globalUser if they don't already have one.
        ProfileObject user = new ProfileObject(object.user.longID);
        if (object.guild.users.getUserByID(object.user.longID) == null) {
            object.guild.users.addUser(user);
        } else {
            user = object.guild.users.getUserByID(object.user.longID);
        }

        //update last talked timestamp
        user.lastTalked = object.message.getTimestampZone().toEpochSecond();

        //ony do xp checks if module is true
        if (!object.guild.config.modulePixels) return;
        if (!object.guild.config.xpGain) return;

        //globalUser setting no xp gain
        if (user.getSettings().contains(NO_XP_GAIN) || user.getSettings().contains(DENIED_XP)) return;

        //role xp denying
        Role xpDenied = object.guild.getXPDeniedRole();
        if (xpDenied != null && object.user.roles.contains(xpDenied)) return;

        //you can only gain xp once per min
        if (object.guild.getSpokenUsers().contains(object.user.longID)) return;

        //messages that might be considered commands should be ignored.
        List<String> deniedPrefixes = new ArrayList<>(object.guild.config.getXpDeniedPrefixes());
        deniedPrefixes.add(object.guild.config.getPrefixCommand());
        deniedPrefixes.add(object.guild.config.getPrefixCC());
        deniedPrefixes.add(object.guild.config.getPrefixAdminCC());
        for (String s : deniedPrefixes) {
            if (object.message.get().getContentRaw().startsWith(s)) {
                return;
            }
        }

        //you must have typed at least 10 chars to gain xp and doesn't contain an image.
        if (object.message.get().getContentRaw().length() < 10 &&
                object.message.get().getAttachments().isEmpty()) return;

        //you cannot gain xp in an xpDenied messageChannel
        if (object.guild.channelHasSetting(ChannelSetting.XP_DENIED, object.guildChannel)) return;

        //gives them their xp.
        user.addXP(object.guild.config);

        // check level cap, don't grant xp to level-capped users
        if (user.getXP() >= Constants.PIXELS_CAP) user.setXp(Constants.PIXELS_CAP);

        //adds to the list of users who have spoken in the last min
        object.guild.getSpokenUsers().add(object.user.longID);

        //check the level up state of the globalUser.
        handleLevelUp(user, object);
    }

    /***
     * Handler for globalUser level ups.
     *
     * @param user   the globalUser that leveled up.
     * @param object the container for all relevant API content.
     */
    private static void handleLevelUp(ProfileObject user, CommandObject object) {
        RewardRoleObject reward = null;

        //get next level xp
        long nextLevelXp = totalXPForLevel(user.getCurrentLevel() + 1);

        //check if the globalUser actually leveled up
        if (user.getXP() < nextLevelXp) return;
        user.levelUp();

        //check if the globalUser ranked up
        for (RewardRoleObject r : object.guild.config.getRewardRoles()) {
            if (r.getLevel() == user.getCurrentLevel()) {
                reward = r;
                break;
            }
        }
        //if denyAutoRole remove Reward.
        if (user.getSettings().contains(DENY_AUTO_ROLE)) reward = null;

        //logging
        object.guild.sendDebugLog(object, "PIXELS", reward != null ? "RANKUP" : "LEVELUP", user.getCurrentLevel() + "");

        //handle reactions
        handleReactions(user, object);

        //handle level up messages
        handleMessage(user, object, reward);

        //check globalUser's roles for rank up purposes
        checkUsersRoles(user.getUserID(), object.guild);
    }

    /***
     * Handles the sending of the level up message
     *
     * @param user   the globalUser that leveled up.
     * @param object the container for all relevant API content.
     * @param reward reward object that contains info on the reward they have received.
     */
    private static void handleMessage(ProfileObject user, CommandObject object, RewardRoleObject reward) {
        UserSetting defaultOverride = object.guild.config.defaultLevelMode;
        UserSetting userState = user.getLevelState();
        boolean rankedUp = reward != null;
        TextChannel levelChannel = object.guild.getChannelByType(ChannelSetting.LEVEL_UP);
        TextChannel currentChannel = object.guildChannel.get();
        PrivateChannel dmChannel = object.user.getDmChannel();
        Message levelMessage = null;

        //force override
        if (userState == null || defaultOverride == DONT_SEND_LVLUP || defaultOverride == SEND_LVLUP_DMS) {
            userState = defaultOverride;
        }

        //get custom level up message.
        StringHandler levelUpMessage = new StringHandler(object.guild.config.levelUpMessage);

        //handle level up tags
        for (TagObject t : TagList.getType(TagType.LEVEL)) {
            levelUpMessage.setContent(t.handleTag(levelUpMessage.toString(), object, ""));
        }

        //build rank up message
        if (rankedUp) {
            levelUpMessage.replace("%s", "");
            if (user.getSettings().contains(HIT_LEVEL_FLOOR)) {
                levelUpMessage.appendFront("Welcome Back.\n");
                levelUpMessage.append("\nYour **@%s** role has been returned to you.");
                user.getSettings().remove(HIT_LEVEL_FLOOR);
            } else {
                levelUpMessage.append("\nYou have been granted the **@%s** role for reaching this level.");
            }
            levelUpMessage.format(reward.getRole(object).getName());
        }

        if (user.getCurrentLevel() == 1) {
            levelUpMessage.appendFormatted("\n\n\\> If you want to change where these messages are sent or want" +
                    " to remove them completely you can change that with `%s`.", Command.get(ProfileSettings.class).getUsage(object));
        }

        //do send message
        switch (userState) {
            case SEND_LVLUP_CURRENT_CHANNEL:
                levelMessage = sendLevelMessage(levelUpMessage.toString(), currentChannel, rankedUp, false);
                break;
            case SEND_LVLUP_RANK_CHANNEL:
                if (levelChannel == null) {
                    levelMessage = sendLevelMessage(levelUpMessage.toString(), currentChannel, rankedUp, false);
                } else {
                    sendLevelMessage(levelUpMessage.toString(), levelChannel, rankedUp, true);
                }
                break;
            case SEND_LVLUP_DMS:
                sendLevelMessage(levelUpMessage.toString(), dmChannel, rankedUp, true);
                break;
        }

        //self destruct level messages
        if (levelMessage != null) {
            selfDestruct(levelMessage, user, object);
        }
    }

    /***
     * Helper method to send a level up message.
     *
     * @param message  the message to be sent
     * @param channel  the messageChannel the message should be sent to.
     * @param isRankUp weather the rank or level up gif should be used.
     * @param doGif    weather the gif should be displayed.
     * @return the message that was sent.
     */
    private static Message sendLevelMessage(String message, MessageChannel channel, boolean isRankUp, boolean doGif) {
        MessageAction sent;
        if (doGif) {
            sent = RequestHandler.requestEmbedImage(message, isRankUp ? Constants.RANK_UP_IMAGE_URL : Constants.LEVEL_UP_IMAGE_URL, channel);
        } else {
            sent = channel.sendMessage(message);
        }
        if (sent != null) return sent.complete();
        else return null;
    }

    /***
     * Handler for attaching reactions to the message that leveled the globalUser up.
     *
     * @param user   the globalUser that leveled up
     * @param object the container for all relevant API content.
     */
    private static void handleReactions(ProfileObject user, CommandObject object) {
        if (user.getSettings().contains(UserSetting.NO_LEVEL_UP_REACTIONS)) return;
        if (object.guild.config.levelUpReaction.equalsIgnoreCase("null")) return;
        Emote emote = object.client.get().getEmoteById(object.guild.config.levelUpReaction);
        if (EmojiUtils.isEmoji(object.guild.config.levelUpReaction)) {
            object.message.get().addReaction(object.guild.config.levelUpReaction).queue();
        } else if (emote != null) {
            object.message.get().addReaction(emote).queue();
        } else {
            sendReactionError(object);
        }
    }

    private static void sendReactionError(CommandObject object) {
        ChannelObject adminChannel = new ChannelObject(object.guild.getChannelByType(ChannelSetting.ADMIN));
        if (adminChannel == null) adminChannel = object.guildChannel;
        adminChannel.queueMessage("\\> The current emoji set to be used for level up reactions is invalid and needs to be updated.");
    }

    /***
     * Helper method to self destruct messages after a certain time.
     *
     * @param message the message to be deleted.
     * @param user    the globalUser that level up message.
     */
    private static void selfDestruct(Message message, ProfileObject user, CommandObject object) {
        if (object.guild.config.selfDestructLevelUps) {
            selfDestruct.schedule(() -> message.delete().queue(), user.getCurrentLevel() == 1 ? 2 : 1, TimeUnit.MINUTES);
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

    public static boolean isUnRanked(long userID, GuildUsers users, Guild guild) {
        ProfileObject user = users.getUserByID(userID);
        GuildObject guildObject = Globals.getGuildContent(guild.getIdLong());
        if (user == null) return true;
        if (guild.getMemberById(userID) == null) return true;
        if (user.getXP() == 0) return true;

        if (user.getSettings().stream().anyMatch(Constants.dontLogStates::contains)) {
            return true;
        }
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        long diff = now.toEpochSecond() - user.getLastTalked();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.SECONDS);
        if (days > 14 && guildObject.config.xpDecay && !user.getSettings().contains(UserSetting.DONT_DECAY)) {
            return true;
        }
        return false;
    }


    public static long rank(GuildUsers guildUsers, Guild guild, long userID) {
        if (isUnRanked(userID, guildUsers, guild)) {
            return -1;
        }

        ProfileObject user = guildUsers.getUserByID(userID);

        //rank calc
        long rank = 0;
        List<ProfileObject> users = new LinkedList<>(guildUsers.getProfiles().values());
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

    public static long rank(GuildObject guild, UserObject user) {
        return rank(guild.users, guild.get(), user.longID);
    }

    public static long totalRanked(CommandObject command) {
        return command.guild.users.getProfiles().values().stream()
                .filter(p -> p.showRank(command.guild)).count();
    }

    public static int getRewardCount(GuildObject object, long userID) {
        if (!object.config.modulePixels) return 4;
        ProfileObject userObject = object.users.getUserByID(userID);
        if (userObject == null) {
            return 0;
        }
        List<RewardRoleObject> userRewards = object.config.getAllRewards(userObject.getCurrentLevel());
        List<RewardRoleObject> allRewards = object.config.getRewardRoles();
        if (allRewards.isEmpty()) return 4;
        if (userRewards.isEmpty()) {
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

