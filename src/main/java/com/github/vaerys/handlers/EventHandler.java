package com.github.vaerys.handlers;

import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildFile;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.objects.*;
import com.github.vaerys.pogos.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;

import java.io.File;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class EventHandler {

    final static Logger logger = LoggerFactory.getLogger(EventHandler.class);

    public EventHandler() {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
//        doEventSec();
        doEventTenSec();
        doEventMin();
        doEventFiveMin(nowUTC);
        doEventDaily(nowUTC);
    }

    private void doEventMin() {
        int intialdelay = 4000;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.trace("Reset speakers.");
                for (GuildObject g : Globals.getGuilds()) {
                    g.getSpokenUsers().clear();
                }
            }
        }, intialdelay, 60 * 1000);
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
                Utility.sendMessage(object.getMessage(), Globals.getClient().getChannelByID(object.getChannelID()));
                Globals.getGlobalData().removeReminder(object.getUserID(), object.getMessage());
            }
        }, initialDelay * 1000);
    }

    private static void doEventDaily(ZonedDateTime nowUTC) {
        ZonedDateTime midnightUTC = ZonedDateTime.now(ZoneOffset.UTC);
        midnightUTC = midnightUTC.withHour(0).withSecond(0).withMinute(0).withNano(0).plusDays(1);
        long initialDelay = midnightUTC.toEpochSecond() - nowUTC.toEpochSecond() + 4;
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
                ZonedDateTime timeNow = ZonedDateTime.now(ZoneOffset.UTC);
                String dailyFileName = Globals.dailyAvatarName.replace("#day#", timeNow.getDayOfWeek().toString());
                DayOfWeek day = timeNow.getDayOfWeek();
                File avatarFile;
                Random random = new Random();

                logger.info("Running Daily tasks for " + day);

                //sets Avatar.
                if (Globals.doDailyAvatars) {
                    avatarFile = new File(Constants.DIRECTORY_GLOBAL_IMAGES + dailyFileName);
                } else {
                    avatarFile = new File(Constants.DIRECTORY_GLOBAL_IMAGES + Globals.defaultAvatarFile);
                }
                Image avatar = Image.forFile(avatarFile);
                Utility.updateAvatar(avatar);

                //wait for the avatar to update properly
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Utility.sendStack(e);
                }
                //backups
                Utility.backupConfigFile(Constants.FILE_CONFIG, Constants.FILE_CONFIG_BACKUP);
                Utility.backupConfigFile(Constants.FILE_GLOBAL_DATA, Constants.FILE_GLOBAL_DATA_BACKUP);
                Globals.getDailyMessages().backUp();
                for (GuildObject task : Globals.getGuilds()) {
                    for (GuildFile f : task.guildFiles) {
                        f.backUp();
                    }
                    GuildConfig guildconfig = task.config;

                    if (guildconfig.modulePixels && guildconfig.xpDecay) {
                        XpHandler.doDecay(task, nowUTC);
                    }

                    //daily messages
                    if (guildconfig.getChannelIDsByType(Command.CHANNEL_GENERAL) != null) {
                        if (guildconfig.dailyMessage) {
                            IChannel channel = Globals.getClient().getChannelByID(guildconfig.getChannelIDsByType(Command.CHANNEL_GENERAL).get(0));
                            for (DailyMessageObject d : Globals.configDailyMessages) {
                                if (day.equals(d.getDayOfWeek())) {
                                    if (timeNow.getDayOfMonth() == 25 && timeNow.getMonth().equals(Month.DECEMBER)) {
                                        Utility.sendMessage("> ***MERRY CHRISTMAS***", channel);
                                    } else if (timeNow.getDayOfMonth() == 1 && timeNow.getMonth().equals(Month.JANUARY)) {
                                        Utility.sendMessage("> ***HAPPY NEW YEAR***", channel);
                                    } else if (timeNow.getDayOfMonth() == 13 && timeNow.getMonth().equals(Month.JULY)) {
                                        int age = nowUTC.getYear() - 1996;
                                        String modifier = "th";
                                        if ((age + "").endsWith("1")) {
                                            modifier = "st";
                                        } else if ((age + "").endsWith("2")) {
                                            modifier = "nd";
                                        } else if ((age + "").endsWith("3")) {
                                            modifier = "rd";
                                        }
                                        Utility.sendMessage("> Happy " + age + modifier + " Birthday Mum.", channel);
                                    } else {
                                        ArrayList<DailyUserMessageObject> dailyMessages = Globals.getDailyMessages().getDailyMessages(day);
                                        dailyMessages.add(new DailyUserMessageObject(d.getContents(), d.getDayOfWeek(), task.client.longID, 10000));
                                        DailyUserMessageObject toSend = dailyMessages.get(random.nextInt(dailyMessages.size()));
                                        if (toSend.getUserID() == task.client.longID) {
                                            task.config.lastDailyMessageID = -1;
                                        } else {
                                            task.config.lastDailyMessageID = toSend.getUID();
                                        }
                                        String message = toSend.getContents(task);
                                        if (message.matches("^(> |\\*> |\\*\\*> |\\*\\*\\*> |_> |__> |`> |```> ).*$") || message.startsWith("> ")) {
                                            Utility.sendMessage(message, channel);
                                        } else {
                                            Utility.sendMessage("> " + message, channel);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, initialDelay * 1000, 24 * 60 * 60 * 1000);
    }

    public static boolean addReminder(String userID, String channelID, long timeSecs, String message) {
        for (ReminderObject object : Globals.getGlobalData().getReminders()) {
            if (object.getUserID().equals(userID)) {
                return false;
            }
        }
        ReminderObject object = new ReminderObject(userID, channelID, message, timeSecs);
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        if (object.getExecuteTime() - now.toEpochSecond() < 300) {
            sendReminder(object);
            object.setSent(true);
        }
        Globals.getGlobalData().addReminder(object);
        return true;
    }

    private void doEventTenSec() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (GuildObject task : Globals.getGuilds()) {
                    task.resetRateLimit();

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
            nextTimeUTC = nowUTC.withSecond(0).withHour(nowUTC.getHour() + 1).withMinute(0);
        }
        initialDelay = (nextTimeUTC.toEpochSecond() - nowUTC.toEpochSecond());
        if (initialDelay < 30) {
            initialDelay += 60;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
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

                //Sending isAlive Check.
                try {
                    if (Globals.showSaveWarning) {
                        logger.info("Total active threads: " + Thread.activeCount());
                        logger.info("Backup in 5 seconds do not restart.");
                    } else {
                        logger.debug("Total active threads: " + Thread.activeCount());
                        logger.debug("Backup in 5 seconds do not restart.");
                    }
                    Thread.sleep(5000);
                    Globals.getClient().checkLoggedIn("IsAlive");
                } catch (DiscordException e) {
                    logger.error(e.getErrorMessage());
                    logger.info("Logging back in.");
                    try {
                        Globals.getClient().login();
                        Globals.getClient().changePlayingText("Recovered From Crash.");
                        Thread.sleep(30000);
                        Globals.getClient().changePlayingText(Globals.playing);
                        return;
                    } catch (IllegalStateException ex) {
                        //ignore exception
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    Utility.sendStack(e);
                }
                randomPlayingStatus();
                Globals.saveFiles();
                if (Globals.showSaveWarning) {
                    logger.info("Files Saved.");
                } else {
                    logger.debug("Files Saved.");
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
        Globals.client.changePlayingText(status);
        return;
    }
}
