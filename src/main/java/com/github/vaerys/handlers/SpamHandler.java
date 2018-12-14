package com.github.vaerys.handlers;

import com.github.vaerys.commands.cc.EditCC;
import com.github.vaerys.commands.cc.NewCC;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.OffenderObject;
import com.github.vaerys.objects.adminlevel.UserRateObject;
import com.github.vaerys.objects.deprecated.BlackListObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.pogos.GlobalData;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.*;

import java.time.Instant;
import java.util.*;

public class SpamHandler {

    final static Logger logger = LoggerFactory.getLogger(SpamHandler.class);
    private static Map<Long, Integer> spamCounter = new HashMap<>();
    private static Map<Long, Long> spamTimeout = new HashMap<>();

    /*TODO: MAJOR RATELIMIT REFACTOR (AndrielChaoti 2/3/2018)*/

    public static boolean catchWalls(CommandObject command) {
        if (!command.guild.config.stopSpamWalls) return false;
        if (command.message.length() < 800) return false;

        List<IChannel> channels = command.guild.getChannelsByType(ChannelSetting.IGNORE_SPAM);
        if (channels.contains(command.channel.get())) return false;

        String message = command.message.getContent();
        if (StringUtils.startsWithIgnoreCase(message, new NewCC().getCommand(command))) return false;
        if (StringUtils.startsWithIgnoreCase(message, new EditCC().getCommand(command))) return false;
        List<String> chunks = Arrays.asList(message.split("(?<=\\G............)"));
        int maxCount = message.length() / 100;
        for (String chunk : chunks) {
            if (chunk.length() != 10) {
                break;
            }
            int count = StringUtils.countMatches(message, chunk);
            if (count > maxCount) {
                command.message.delete();
                command.guild.config.addOffence(command.user.longID);
                command.guild.sendDebugLog(command, "CATCH_SPAM_WALLS", "OFFENCE_ADDED", count + " Occurrences");
                int offenceCount = command.guild.config.getOffender(command.user.longID).getCount();
                if (offenceCount > 2) {
                    if (command.guild.config.muteRepeatOffenders) {
                        command.guild.users.muteUser(command, -1);
                        command.user.sendDm("You were muted for spamming.");
                        command.guild.sendDebugLog(command, "CATCH_SPAM_WALLS", "MUTE", offenceCount + " Offences");
                        IChannel admin = command.guild.getChannelByType(ChannelSetting.ADMIN);
                        String report = command.user.mention() + " was muted for spamming";
                        // add an automated note to the user.
                        command.user.getProfile(command.guild).addSailModNote(report, command, false);
                        if (admin != null) {
                            RequestHandler.sendMessage(report + " in " + command.channel.get().mention() + ".", admin);
                        } else {
                            RequestHandler.sendMessage(report + ".", command.channel.get());
                        }
                        return true;
                    }
                }
                command.user.sendDm("Your message was deleted because it was considered spam.");
                return true;
            }
        }
        return false;
    }

    public static boolean checkMentionCount(CommandObject command) {
        IMessage message = command.message.get();
        GuildConfig guildconfig = command.guild.config;
        IUser author = command.user.get();
        List<IRole> oldRoles = command.user.roles;
        IGuild guild = command.guild.get();

        List<IChannel> channels = command.guild.getChannelsByType(ChannelSetting.IGNORE_SPAM);
        if (channels.contains(command.channel.get())) return false;

        if (GuildHandler.testForPerms(command, Permissions.MENTION_EVERYONE)) return false;

        if (message.toString().contains("@everyone") || message.toString().contains("@here")) {
            return false;
        }
        if (guildconfig.maxMentions) {
            if (message.getMentions().size() > 8) {
                RequestHandler.deleteMessage(message);
                int i = 0;
                boolean offenderFound = false;
                for (OffenderObject o : guildconfig.getOffenders()) {
                    if (author.getLongID() == o.getID()) {
                        guildconfig.addOffence(o.getID());
                        command.guild.sendDebugLog(command, "STOP_MASS_MENTIONS", "OFFENCE_ADDED", message.getMentions().size() + " Mentions");
                        offenderFound = true;
                        i++;
                        if (o.getCount() >= Globals.maxWarnings) {
                            String report = "> %s has been muted for repeat offences of spamming mentions.";
                            command.guild.users.muteUser(command, -1);
                            command.guild.sendDebugLog(command, "STOP_MASS_MENTIONS", "MUTE", o.getCount() + " Offences");
                            // add strike in modnote
                            command.user.getProfile(command.guild).addSailModNote(String.format(report, author.mention()), command, false);
                            // send admin notification
                            RequestHandler.sendMessage(String.format(report, author.mention()), command.channel.get());
                        }
                    }
                }
                if (!offenderFound) {
                    guildconfig.addOffender(new OffenderObject(author.getLongID()));
                }
                String response = "> <mentionAdmin>, " + author.mention() + "  has attempted to post more than " + guildconfig.getMaxMentionLimit() + " Mentions in a single message in " + command.channel.mention + ".";
                IRole roleToMention = command.guild.getRoleByID(guildconfig.getRoleToMentionID());
                if (roleToMention != null) {
                    response = response.replaceAll("<mentionAdmin>", roleToMention.mention());
                } else {
                    response = response.replaceAll("<mentionAdmin>", "Admin");
                }
                RequestHandler.sendMessage(response, command.channel.get());
                return true;
            }
        }
        return false;
    }


    public static boolean rateLimiting(CommandObject command) {
        //make sure that the rate limiting should actually happen
        if (!command.guild.config.rateLimiting) return false;
        if (GuildHandler.testForPerms(command, Permissions.MANAGE_MESSAGES)) return false;

        //ignore spam in tagged channels
        List<IChannel> channels = command.guild.getChannelsByType(ChannelSetting.IGNORE_SPAM);
        if (channels.contains(command.channel.get())) return false;

        //increase the counter and get the object
        UserRateObject userRate = command.guild.rateLimit(command);

        //Force Reset rate limiter if things go wrong
        if (Globals.lastRateLimitReset + 20 * 1000 < System.currentTimeMillis()) {
            command.guild.resetRateLimit();
            RequestHandler.sendMessage("> Forced Rate Limit Reset. **Guild ID:** " + command.guild.longID +
                    ", **Guild Name:** " + command.guild.get().getName(), command.client.creator.getDmChannel());
        }

        //check if user is rate limited
        if (!userRate.isRateLimited(command.guild)) return false;

        //delete messages
        logDelete(command, "RATE_LIMITING", "MESSAGE_DELETED");
        command.message.delete();

        String rateLimitFormat = "> Whoa there, You're typing too fast! You're breaking %s's Rate limit. (%d messages every 10 seconds)";
        String muteFormat = "> Time to chill! You have been muted for 5 minutes for breaking %s's Rate limit. (%d messages every 10 seconds)";
        String guildName = command.guild.get().getName();
        int messageLimit = command.guild.config.messageLimit;

        //send warnings
        if (userRate.getSize() < messageLimit + 4) {
            command.user.sendDm(String.format(rateLimitFormat, guildName, messageLimit));
            return true;
        } else {
            //mute handling
            if (!command.guild.config.muteRepeatOffenders) return true;
            if (userRate.isMuted()) return true;
            IRole mutedRole = command.guild.getMutedRole();
            if (mutedRole == null) return true;
            userRate.mute();
            //mute user for 300 seconds
            command.guild.users.muteUser(command, 300);
            command.user.sendDm(String.format(muteFormat, guildName, messageLimit)).get();
            return true;
        }
    }

    private static void logDelete(CommandObject command, String type, String reason) {
        String contents = command.message.getContent();
        for (IMessage.Attachment a : command.message.getAttachments()) {
            contents += " <" + a.getUrl() + ">";
        }
        if (command.message.getContent() == null || command.message.getContent().isEmpty()) {
            contents = contents.replace("  ", "");
        }
        command.guild.sendDebugLog(command, type, reason, contents);
    }

    public static boolean checkForInvites(CommandObject command) {
        GuildConfig guildconfig = command.guild.config;
        if (!guildconfig.denyInvites) {
            return false;
        }
        IMessage message = command.message.get();
        IGuild guild = command.guild.get();
        IUser author = command.user.get();
        List<String> inviteformats = new ArrayList<String>() {{
            add("discord.gg");
            add("discordapp.com/Invite");
        }};
        if (GuildHandler.testForPerms(command, Permissions.MANAGE_MESSAGES)) return false;

        boolean inviteFound = false;
        boolean shouldDelete = false;
        ProfileObject object = command.user.getProfile(command.guild);
        boolean userSettingDenied = false;
        if (object != null) {
            userSettingDenied = object.getSettings().contains(UserSetting.DENY_INVITES);
        }

        for (String s : inviteformats) {
            if (message.toString().toLowerCase().contains(s.toLowerCase())) {
                inviteFound = true;
            }
        }

        boolean isTrusted = guildconfig.testIsTrusted(author, guild);

        if (userSettingDenied || !isTrusted) {
            shouldDelete = true;
        }

        if (inviteFound && shouldDelete) {
            String response;
            if (userSettingDenied) {
                response = "> " + command.user.mention() + ", you do not have permission to post Instant Invites.";
            } else {
                response = "> " + command.user.mention() + ", please do not post Instant Invites.";
            }
            RequestHandler.deleteMessage(message);
            command.guild.sendDebugLog(command, "INVITE_REMOVAL", "REMOVED", message.getContent());
            RequestHandler.sendMessage(response, command.channel.get());
            return true;
        }
        return false;
    }

    public static boolean commandBlacklisting(CommandObject command) {
        // This is not something guilds can turn on/off, it is a temporary blacklist to keep spammers from abusing bot commands.

        //exit thinger if is staff or can bypass
        if (GuildHandler.testForPerms(command, Permissions.MANAGE_MESSAGES)) return false;

        // check if this is even a command first:
        List<Command> commands = new ArrayList<>(command.guild.commands);
        boolean isCommand = false;
        for (Command c : commands) {
            if (c.isCall(command.message.get().getContent(), command)) {
                isCommand = true;
                break;
            }
        }
        if (!isCommand) return false;

        UserObject user = command.user;
        GlobalData globalData = Globals.getGlobalData();
        if (globalData == null) throw new NullPointerException();

        // first we need to see if there *are* blacklisted users and ignore them:
        List<BlackListObject.BlacklistedUserObject> blacklistedUsers = globalData.getBlacklistedUsers();
        for (BlackListObject.BlacklistedUserObject object : blacklistedUsers) {
            if (user.longID == object.getUserID()) {
                // user is on blacklist, and hasn't timed out...
                if (Instant.now().toEpochMilli() <= object.getEndTime()) return true;
            }
        }

        // this handles people who aren't blacklisted currently:
        long userID = user.longID;
        int count = 1;

        if (spamCounter.containsKey(userID)) {
            // reset counter:
            if (spamTimeout.get(userID) <= Instant.now().toEpochMilli()) {
                spamCounter.put(userID, count);
                spamTimeout.put(userID, Instant.now().toEpochMilli() + 10000);
            } else {
                count = spamCounter.get(userID);
                count++;
                spamCounter.put(userID, count);
                logger.trace("counter is now " + count + " for " + userID);
                if (count >= 5) {
                    // User has spammed *too much*.
                    BlackListObject.BlacklistedUserObject blUser = globalData.blacklistUser(userID);
                    long diffTime = blUser.getEndTime() - Instant.now().toEpochMilli();

                    if (blUser.getCounter() >= 5) {
                        RequestHandler.sendMessage("> OKAY, ENOUGH " + user.mention() + ". You've been permanently blacklisted from using commands.", command.channel);
                        RequestHandler.sendCreatorDm(String.format("> @%s(%d) was blacklisted from using commands", user.username, user.longID));
                    } else {
                        RequestHandler.sendMessage("> You're using commands too much " + user.mention() +
                                ". Take a chill pill, and try again in " + Utility.formatTime(diffTime / 1000, true), command.channel);
                    }

                    spamCounter.remove(userID);
                    spamTimeout.remove(userID);
                    logger.info("User " + userID + "(@" + user.username + ") blacklisted for spamming commands (x" + blUser.getCounter() + ")");
                    return true;
                }
            }
        } else {
            spamCounter.put(userID, count);
            spamTimeout.put(userID, Instant.now().toEpochMilli() + 10000);
            logger.trace("counter created for " + userID);
        }
        return false;
    }
}
