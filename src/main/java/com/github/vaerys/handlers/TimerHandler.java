package com.github.vaerys.handlers;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.listeners.LoggingListener;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.adminlevel.MutedUserObject;
import com.github.vaerys.objects.adminlevel.UserRateObject;
import com.github.vaerys.objects.botlevel.RandomStatusObject;
import com.github.vaerys.objects.depreciated.BlackListObject;
import com.github.vaerys.objects.events.TimedEvent;
import com.github.vaerys.objects.userlevel.DailyMessage;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.objects.userlevel.ReminderObject;
import com.github.vaerys.pogos.GuildConfig;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class TimerHandler {

    final static Logger logger = LoggerFactory.getLogger(TimerHandler.class);
    public static List<Double> cpuUsage = new LinkedList<>();
    private static long keepAliveTenSec;
    private static long keepAliveMin;
    private static long keepAliveFiveMin;
    private static long keepAliveDaily;

    public TimerHandler() {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
//        doEventSec();
        keepAliveFiveMin = System.currentTimeMillis();
        keepAliveMin = System.currentTimeMillis();
        keepAliveTenSec = System.currentTimeMillis();
        keepAliveDaily = System.currentTimeMillis();
        doEventTenSec();
        doEventMin();
        doEventFiveMin(nowUTC);
        doEventDaily(nowUTC);
    }

    public static void checkKeepAlive() {

        long now = System.currentTimeMillis();
        if (keepAliveTenSec - now > 10 * 4 * 1000) {
            logger.error("Ten Second Timer Failed to respond to keep alive. resetting.");
            doEventTenSec();
        }
        if (keepAliveMin - now > 60 * 4 * 1000) {
            logger.error("One Min Timer Failed to respond to keep alive. resetting.");
            doEventMin();
        }
        if (keepAliveFiveMin - now > 60 * 5 * 4 * 1000) {
            logger.error("Five Min Timer Failed to respond to keep alive. resetting.");
            doEventFiveMin(ZonedDateTime.now(ZoneOffset.UTC));
        }
        if (keepAliveDaily - now > 25 * 60 * 60 * 1000) {
            logger.error("Daily Timer failed to respond to keep alive. resetting.");
            doEventDaily(ZonedDateTime.now(ZoneOffset.UTC));
        }
    }

    private static void doEventMin() {
        int initialDelay = 4000;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    keepAliveMin = System.currentTimeMillis();
                    checkKeepAlive();

                    cpuUpdate();

                    clearSpeakers();

                    screenlogHandler();
                } catch (Exception e) {
                    Utility.sendStack(e);
                }
            }
        }, initialDelay, 60 * 1000);
    }

    //Methods for Events that occur every min

    private static void screenlogHandler() {
        if (FileHandler.exists("screenlog.0") && FileHandler.exists("screenlog.closed")) {
            File file = new File("screenlog.0");
            if (file.length() > 1048576 * 10) {
                file.delete();
            }
        } else if (FileHandler.exists("screenlog.0")) {
            File file = new File("screenlog.0");
            if (file.length() > 1048576 * 10) {
                User creator = Client.getClient().getUserById(Globals.creatorID);
                if (creator == null) return;
                UserObject creatorUser = new UserObject(creator, null);
                creatorUser.queueDm("\\> screenlog.0 got too big, potential log spam, archived log to prevent loss of usage.");
                file.renameTo(new File("screenlog.closed"));
            }
        }
    }

    private static void cpuUpdate() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        cpuUsage.add(operatingSystemMXBean.getProcessCpuLoad());

        if (cpuUsage.size() > 5) {
            cpuUsage.remove(0);
        }
    }

    private static void clearSpeakers() {
        logger.trace("Reset speakers.");
        for (GuildObject g : Globals.getGuilds()) {
            g.getSpokenUsers().clear();
        }
    }

    //----------------------------------------------------------------


    private static void doEventDaily(ZonedDateTime nowUTC) {
        ZonedDateTime midnightUTC = ZonedDateTime.now(ZoneOffset.UTC);
        midnightUTC = midnightUTC.withHour(0).withSecond(0).withMinute(0).withNano(0).plusDays(1);
        long initialDelay = midnightUTC.toEpochSecond() - nowUTC.toEpochSecond() + 4;
        long period = 24 * 60 * 60 * 1000;
        logger.trace("Now UTC = " + Utility.formatTimeSeconds(nowUTC.toEpochSecond()));
        logger.trace("Midnight UTC = " + Utility.formatTimeSeconds(midnightUTC.toEpochSecond()));
        logger.trace("Delay = " + Utility.formatTimeSeconds(initialDelay));
        if (initialDelay < 120) {
            initialDelay += 24 * 60 * 60;
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    keepAliveDaily = System.currentTimeMillis();
                    //init vars


                    //do checks
                    Client.checkPatrons();
                    checkKeepAlive();

                    //update Active Event
                    Globals.updateEvent();
                    TimedEvent event = Globals.getCurrentEvent();

                    //handle avatars
                    Client.handleAvatars();

                    //backups
                    Globals.backupAll();

                    // clear blacklist
                    List<BlackListObject.BlacklistedUserObject> blacklistedUserObjects = Globals.getGlobalData().getBlacklistedUsers();
                    blacklistedUserObjects.removeIf(object -> object.getCounter() < 5);

                    dailyMessageHandler(event);


                } catch (Exception e) {
                    Utility.sendStack(e);
                }
            }
        }, initialDelay * 1000, period);
    }

    private static void dailyMessageHandler(TimedEvent event) {
        ZonedDateTime timeNow = ZonedDateTime.now(ZoneOffset.UTC);
        DayOfWeek day = timeNow.getDayOfWeek();

        logger.info("Running Daily tasks for " + day);

        Random random = Globals.getGlobalRandom();

        for (GuildObject task : Globals.getGuilds()) {
            GuildConfig guildconfig = task.config;
            //do decay
            GuildHandler.dailyTask(task);

            //reset offenders
            task.resetOffenders();

            //reset Admin CC counters
            task.adminCCs.dailyReset();

            //getAllToggles general messageChannel
            TextChannel generalChannel = task.getChannelByType(ChannelSetting.GENERAL);

            //do daily messages
            if (generalChannel != null && guildconfig.dailyMessage) {
                DailyMessage finalMessage;
                List<DailyMessage> messages;
                if (event != null && event.doSpecialMessages() && event.getMessagesDay(day).size() != 0) {
                    messages = event.getMessagesDay(day);
                } else {
                    messages = new ArrayList<>(Globals.getDailyMessages().getDailyMessages(day));
                    messages.add(Globals.getDailyMessage(day));
                }
                int randomMessage = random.nextInt(messages.size());
                finalMessage = messages.get(randomMessage);
                task.config.setLastDailyMessage(finalMessage);
                CommandObject command = new CommandObject(task, generalChannel);
                generalChannel.sendMessage(finalMessage.getContents(command)).queue();
            }
        }
    }

    public static String addReminder(long userID, long channelID, long timeSecs, String message) {
        int reminderCount = 0;
        int maxReminders = Globals.maxReminderSlots;
        for (ReminderObject object : Globals.getGlobalData().getReminders()) {
            if (object.getUserID() == userID) {
                reminderCount++;
                long max = object.getExecuteTime() + 300;
                long min = object.getExecuteTime() - 300;
                if (min < timeSecs && max > timeSecs) {
                    return "INTERRUPTS";
                }
            }
        }
        if (Globals.getPatrons().contains(userID)) {
            maxReminders += maxReminders;
        }
        if (userID == Globals.creatorID) {
            maxReminders = Integer.MAX_VALUE;
        }
        if (reminderCount >= maxReminders) {
            return "MAX";
        }
        ReminderObject object = new ReminderObject(userID, channelID, message, timeSecs);
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        if (object.getExecuteTime() - now.toEpochSecond() < 300) {
            sendReminder(object);
            object.setSent(true);
        }
        Globals.getGlobalData().addReminder(object);
        return "OK";
    }

    //Reminder new setup.
    public static void sendReminder(ReminderObject object) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        long initialDelay = object.getExecuteTime() - now.toEpochSecond();
        if (initialDelay < 0) {
            initialDelay = 5;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                User user = Client.getClient().getUserById(object.getUserID());
                MessageChannel channel;
                TextChannel guildChannel = Client.getClient().getTextChannelById(object.getChannelID());
                if (user == null) return;
                if (guildChannel == null) {
                    channel = Client.getClient().getPrivateChannelById(object.getChannelID());
                } else {
                    if (guildChannel.getGuild().getMember(user) == null) {
                        channel = Client.getClient().getPrivateChannelById(object.getChannelID());
                    } else {
                        channel = guildChannel;
                    }
                }
                if (channel == null) {
                    Globals.getGlobalData().removeReminder(object);
                    return;
                }
                Message message = channel.sendMessage(object.getMessage()).complete();
                if (message == null && channel instanceof TextChannel) {
                    Member bot = ((TextChannel) channel).getGuild().getMember(Client.getClient().getSelfUser());
                    if (bot != null && ((TextChannel) channel).getPermissionOverride(bot).getAllowed().contains(Permission.MESSAGE_WRITE)) {
                        object.setSent(false);
                    }
                }
                Globals.getGlobalData().removeReminder(object);
            }
        }, initialDelay * 1000);
    }

    private static void doEventTenSec() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    keepAliveTenSec = System.currentTimeMillis();
                    for (GuildObject task : Globals.getGuilds()) {

                        reportHandling(task);

                        tenSecGuildTask(task);
                    }
                } catch (Exception e) {
                    Utility.sendStack(e);
                }
            }
        }, 1000 * 10, 10 * 1000);
    }

    private static void reportHandling(GuildObject guild) {
        List<UserRateObject> offenders = guild.getRateUsers();

        Role muteRole = guild.getRoleById(guild.config.getMutedRoleID());
        if (muteRole == null) return;

        TextChannel admin = guild.getChannelByType(ChannelSetting.ADMIN);

        for (UserRateObject u : offenders) {
            //ignore non muted users
            if (!u.isMuted()) continue;
            //set up messages
            String adminFormat = "\\> %s was muted for breaking the guild's rate limit (%d Over Limit) in %s.";
            String modNoteFormat = "\\> Muted by Rate Limiter, %d Over Limit. Channel%s: %s.";
            //get channels
            List<TextChannel> channels = u.getChannels(guild);
            //get admin messageChannel
            if (admin == null) admin = channels.get(channels.size() - 1);
            //get offender
            Member offender = u.getUser(guild);
            ProfileObject profile = guild.users.getUserByID(u.getUserID());
            //get amount over limit
            long rate = u.getSize() - guild.config.messageLimit;
            //format messageChannel mentions
            String formattedChannels = channels.stream().map(IMentionable::getAsMention).collect(Collectors.joining(", "));
            //debug log mute
            guild.sendDebugLog(offender.getUser(), admin, "RATE_LIMITING", "MUTE", rate + " over limit.");
            //logging
            if (guild.config.deleteLogging) {
                LoggingListener.sendLog("\\> **@" + offender.getUser().getAsTag() + "** was muted for breaking rate limit.", guild, true);
            }
            //send messages
            profile.addSailModNote(String.format(modNoteFormat, rate, channels.size() > 1 ? "s" : "", formattedChannels), u.getTimeStamp(), false);
            admin.sendMessage(String.format(adminFormat, offender.getAsMention(), rate, channels.size() > 1 ? "channels: " + formattedChannels : formattedChannels)).queue();
        }
    }


    private static void tenSecGuildTask(GuildObject task) {
        task.resetRateLimit();
        Globals.lastRateLimitReset = System.currentTimeMillis();
        if (task.getRateUsers().size() != 0) {
            logger.error("Failed to clear list, forcing it to clear.");
            task.forceClearRate();
        }
        //Mutes.
        ArrayList<MutedUserObject> mutedUsers = task.users.getMutedUsers();
        for (int i = 0; i < mutedUsers.size(); i++) {
            if (mutedUsers.get(i).getRemainderSecs() != -1) {
                mutedUsers.get(i).tickDown(10);
                if (mutedUsers.get(i).getRemainderSecs() == 0) {
                    task.users.unMuteUser(mutedUsers.get(i).getID(), task.longID);
                }
            }
        }
    }


    private static void doEventFiveMin(ZonedDateTime nowUTC) {
        try {
            Globals.client.awaitReady();
        } catch (InterruptedException | IllegalArgumentException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        ZonedDateTime nextTimeUTC;
        long initialDelay;
        if (nowUTC.getMinute() != 59) {
            nextTimeUTC = nowUTC.withSecond(0).withMinute(nowUTC.getMinute() + 1);
        } else {
            if (nowUTC.getHour() == 23 && nowUTC.getMinute() > 54) {
                nextTimeUTC = nowUTC.withDayOfYear(nowUTC.getDayOfYear() + 1).withMinute(0).withHour(0).withSecond(0);
            } else {
                nextTimeUTC = nowUTC.withSecond(0).withHour(nowUTC.getHour() + 1).withMinute(0);
            }
        }
        initialDelay = (nextTimeUTC.toEpochSecond() - nowUTC.toEpochSecond());
        if (initialDelay < 30) {
            initialDelay += 60;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
                    keepAliveFiveMin = System.currentTimeMillis();

                    checkKeepAlive();

                    logger.info("Reaction Counter: " + Globals.reactionCount);

                    reminderHandler(now);

                    botStatsHandler();

                    for (GuildObject g : Globals.getGuilds()) {
                        g.adminCCs.purgeKeys();
                    }

                    saveHandler();
                } catch (Exception e) {
                    Utility.sendStack(e);
                }
            }
        }, initialDelay * 1000, 5 * 60 * 1000);
    }

    private static void saveHandler() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Utility.sendStack(e);
            Thread.currentThread().interrupt();
        }

        randomPlayingStatus();
        Globals.saveFiles(false);
        if (Globals.showSaveWarning) {
            logger.info("Files Saved.");
        } else {
            logger.debug("Files Saved.");
        }
    }

    private static void botStatsHandler() {
        // grab current memory usage
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long usedMemory = totalMemory - freeMemory;
        // and make it look pretty
        StringBuilder memString = new StringBuilder();
        NumberFormat nf = NumberFormat.getInstance();
        memString.append("Memory Usage: ");
        memString.append(nf.format(totalMemory / 1024)).append("KB total\t");
        memString.append(nf.format(usedMemory / 1024)).append("KB used\t");
        memString.append(nf.format(freeMemory / 1024)).append("KB free");
        double avgCpu = 0;
        for (Double i : cpuUsage) {
            avgCpu += i;
        }
        avgCpu /= cpuUsage.size();

        if (avgCpu > 0.9) {
            logger.info("Total active threads: " + Thread.activeCount() + "\tCPU Usage: " + nf.format(avgCpu * 100) + "%");
            logger.info(memString.toString());
            logger.error("CPU USAGE LIMIT REACHED RESTARTING.");
            Utility.sendStack(new Exception("CPU overloaded - Dumping logs of last 25 actions"), 25);
            Client.getClientObject().creator.sendDm("\\> CPU USAGE LIMIT REACHED, AUTO RESTARTING...");
            Client.getClient().shutdown();
            System.exit(Constants.EXITCODE_RESTART);
        }

        //Sending isAlive Check.
        if (Globals.showSaveWarning) {
            logger.info("Total active threads: " + Thread.activeCount() + "\tCPU Usage: " + nf.format(avgCpu * 100) + "%");
            logger.info(memString.toString());
            logger.info("Backup in 5 seconds do not restart.");
        } else {
            logger.debug("Total active threads: " + Thread.activeCount() + "\tCPU Usage: " + nf.format(avgCpu * 100) + "%");
            logger.debug(memString.toString());
            logger.debug("Backup in 5 seconds do not restart.");
        }

    }

    private static void reminderHandler(ZonedDateTime now) {
        for (ReminderObject object : Globals.getGlobalData().getReminders()) {
            if (object.getExecuteTime() - now.toEpochSecond() < 350) {
                if (!object.isSent()) {
                    sendReminder(object);
                    object.setSent(true);
                } else {
                    if (object.getExecuteTime() - now.toEpochSecond() < 0) {
                        sendReminder(object);
                    }
                }
            }
        }
    }

    private static void randomPlayingStatus() {
        Random random = Globals.getGlobalRandom();
        String status = Globals.playing;
        if (Globals.doRandomGames && Globals.getRandomStatuses().size() != 0) {
            ArrayList<String> games = new ArrayList<>();
            for (RandomStatusObject r : Globals.getRandomStatuses()) {
                for (int i = 0; i < r.getWeight(); i++) {
                    games.add(r.getStatus());
                }
            }
            int nextRand = random.nextInt(games.size());
            status = games.get(nextRand);
        }
        RequestHandler.changePresence(status);
    }
}
