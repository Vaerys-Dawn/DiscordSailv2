package com.github.vaerys.handlers;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Client;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.objects.DailyMessage;
import com.github.vaerys.objects.RandomStatusObject;
import com.github.vaerys.objects.ReminderObject;
import com.github.vaerys.objects.TimedEvent;
import com.github.vaerys.objects.UserCountDown;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.ChannelSetting;
import com.sun.management.OperatingSystemMXBean;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class TimerHandler {

    final static Logger logger = LoggerFactory.getLogger(TimerHandler.class);

    private static long keepAliveTenSec;
    private static long keepAliveMin;
    private static long keepAliveFiveMin;
    private static long keepAliveDaily;
    public static List<Double> cpuUsage = new LinkedList<>();

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

    private static void doEventMin() {
        int initialDelay = 4000;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                keepAliveMin = System.currentTimeMillis();
                checkKeepAlive();
                logger.trace("Reset speakers.");

                OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
                cpuUsage.add(operatingSystemMXBean.getProcessCpuLoad());

                if (cpuUsage.size() > 5) {
                    cpuUsage.remove(0);
                }

                for (GuildObject g : Globals.getGuilds()) {
                    g.getSpokenUsers().clear();
                }
                if (FileHandler.exists("screenlog.0") && FileHandler.exists("screenlog.closed")) {
                    File file = new File("screenlog.0");
                    if (file.length() > 1048576 * 10) {
                        file.delete();
                    }
                } else if (FileHandler.exists("screenlog.0")) {
                    File file = new File("screenlog.0");
                    if (file.length() > 1048576 * 10) {
                        IUser creator = Client.getClient().getUserByID(Globals.creatorID);
                        if (creator == null) return;
                        UserObject creatorUser = new UserObject(creator, null);
                        creatorUser.sendDm("> screenlog.0 got too big, potential log spam, archived log to prevent loss of usage.");
                        file.renameTo(new File("screenlog.closed"));
                    }
                }
            }
        }, initialDelay, 60 * 1000);
    }

    //Reminder new setup.
    public static void sendReminder(ReminderObject object) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        long initialDelay = object.getExecuteTime() - now.toEpochSecond();
//        System.out.println(initialDelay + "");
        if (initialDelay < 0) {
            initialDelay = 5;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                IMessage message = RequestHandler.sendMessage(object.getMessage(), Globals.getClient().getChannelByID(object.getChannelID())).get();
                if (message == null) {
                    logger.error("REMINDER FAILED FOR USER WITH ID \"" + object.getUserID() + "\" TO SEND. WILL ATTEMPT TO SEND AGAIN IN 5 MINS.");
                    object.setSent(false);
                } else {
                    Globals.getGlobalData().removeReminder(object);
                }
            }
        }, initialDelay * 1000);
    }

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
//        initialDelay = 60;
//        period = 60 * 1000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    keepAliveDaily = System.currentTimeMillis();
                    //init vars
                    ZonedDateTime timeNow = ZonedDateTime.now(ZoneOffset.UTC);
                    DayOfWeek day = timeNow.getDayOfWeek();

                    Random random = new Random();

//                day = DayOfWeek.values()[random.nextInt(DayOfWeek.values().length)];
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

                    logger.info("Running Daily tasks for " + day);

                    for (GuildObject task : Globals.getGuilds()) {
                        GuildConfig guildconfig = task.config;
                        //do decay
                        XpHandler.doDecay(task);

                        //reset offenders
                        task.resetOffenders();

                        //get general channel
                        IChannel generalChannel = task.getChannelByType(ChannelSetting.GENERAL);

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
                            RequestHandler.sendMessage(finalMessage.getContents(command), generalChannel);
                        }
                    }
                } catch (Exception e) {
                    Utility.sendStack(e);
                }
            }
        }, initialDelay * 1000, period);
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

    private static void doEventTenSec() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                keepAliveTenSec = System.currentTimeMillis();
                for (GuildObject task : Globals.getGuilds()) {
                    task.resetRateLimit();
                    if (task.getRateLimiting().size() != 0) {
                        logger.error("Failed to clear list, forcing it to clear.");
                        task.forceClearRate();
                    }
                    //Mutes.
                    ArrayList<UserCountDown> mutedUsers = task.users.getMutedUsers();
                    for (int i = 0; i < mutedUsers.size(); i++) {
                        if (mutedUsers.get(i).getRemainderSecs() != -1) {
                            mutedUsers.get(i).tickDown(10);
                            if (mutedUsers.get(i).getRemainderSecs() == 0) {
                                task.users.unMuteUser(mutedUsers.get(i).getID(), task.longID);
                            }
                        }
                    }
                }
            }
        }, 1000 * 10, 10 * 1000);
    }

    private static void doEventFiveMin(ZonedDateTime nowUTC) {
        try {
            Globals.client.getDispatcher().waitFor(ReadyEvent.class);
        } catch (InterruptedException e) {
            Utility.sendStack(e);
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
                    double avgCpu=0;
                    for (Double i : cpuUsage) {
                        avgCpu += i;
                    }
                    avgCpu /= cpuUsage.size();
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
                    Thread.sleep(5000);
                    Globals.getClient().checkLoggedIn("Check Online status");
                    randomPlayingStatus();
                    Globals.saveFiles(false);
                    if (Globals.showSaveWarning) {
                        logger.info("Files Saved.");
                    } else {
                        logger.debug("Files Saved.");
                    }
                } catch (DiscordException e) {
                    logger.error(e.getErrorMessage());
                    logger.info("Logging back in.");
                    try {
//                        Globals.client.logout();
                        Thread.sleep(4000);
                        Globals.getClient().login();
                        RequestHandler.changePresence("Recovered From Crash.");
                        return;
                    } catch (DiscordException e2) {
                        if (!e2.getMessage().contains("login")) {
                            Utility.sendStack(e2);
                        }
                    } catch (IllegalStateException ex) {
                        //ignore exception
                    } catch (InterruptedException e1) {
                        Utility.sendStack(e1);
                    }
                } catch (InterruptedException e) {
                    Utility.sendStack(e);
                } catch (Exception e) {
                    Utility.sendStack(e);
                }
            }
        }, initialDelay * 1000, 5 * 60 * 1000);
    }

    private static void randomPlayingStatus() {
        Random random = new Random();
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
        return;
    }
}
