package Handlers;

import Interfaces.Command;
import Main.Constants;
import Main.Globals;
import Main.Utility;
import Objects.*;
import POGOs.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class EventHandler {

    final static Logger logger = LoggerFactory.getLogger(EventHandler.class);

    public EventHandler() {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        doEventSec();
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
                for (GuildContentObject g : Globals.getGuildContentObjects()) {
                    g.getSpokenUsers().clear();
                }
            }
        }, intialdelay, 60 * 1000);
    }

    //Reminder new setup.
    public static void sendReminder(ReminderObject object) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        long initialDelay = object.getExecuteTime() - now.toEpochSecond();
        System.out.println(initialDelay + "");
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

    public static int getDoAdminMention(String guildID) {
        for (GuildContentObject task : Globals.getGuildContentObjects()) {
            if (task.getGuildID().equals(guildID)) {
                return task.getDoAdminMention();
            }
        }
        return -1;
    }

    public static void setDoAdminMention(String guildID, int i) {
        for (GuildContentObject task : Globals.getGuildContentObjects()) {
            if (task.getGuildID().equals(guildID)) {
                task.setDoAdminMention(i);
                return;
            }
        }
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
                    e.printStackTrace();
                }
                //backups
                Utility.backupConfigFile(Constants.FILE_CONFIG, Constants.FILE_CONFIG_BACKUP);
                Utility.backupConfigFile(Constants.FILE_GLOBAL_DATA, Constants.FILE_GLOBAL_DATA_BACKUP);
                for (GuildContentObject task : Globals.getGuildContentObjects()) {
                    Utility.backupFile(task.getGuildID(), Constants.FILE_GUILD_CONFIG);
                    Utility.backupFile(task.getGuildID(), Constants.FILE_CUSTOM);
                    Utility.backupFile(task.getGuildID(), Constants.FILE_CHARACTERS);
                    Utility.backupFile(task.getGuildID(), Constants.FILE_SERVERS);
                    Utility.backupFile(task.getGuildID(), Constants.FILE_INFO);
                    Utility.backupFile(task.getGuildID(), Constants.FILE_COMPETITION);
                    Utility.backupFile(task.getGuildID(), Constants.FILE_GUILD_USERS);
                    GuildConfig guildConfig = Globals.getGuildContent(task.getGuildID()).getGuildConfig();

                    if (guildConfig.modulePixels && guildConfig.xpDecay) {
                        XpHandler.doDecay(task, nowUTC);
                    }

                    //daily messages
                    if (guildConfig.getChannelIDsByType(Command.CHANNEL_GENERAL) != null) {
                        if (guildConfig.dailyMessage) {
                            IChannel channel = Globals.getClient().getChannelByID(guildConfig.getChannelIDsByType(Command.CHANNEL_GENERAL).get(0));
                            for (DailyMessageObject d : Globals.dailyMessages) {
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
                                        ArrayList<DailyUserMessageObject> dailyMessages = Globals.getGlobalData().getDailyMessages(day);
                                        dailyMessages.add(new DailyUserMessageObject(d.getContents(), d.getDayOfWeek(), Globals.botName));
                                        Random random = new Random();
                                        Utility.sendMessage(dailyMessages.get(random.nextInt(dailyMessages.size())).getContents(), channel);
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

    private static void doEventSec() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (GuildContentObject task : Globals.getGuildContentObjects()) {
                    if (task.getDoAdminMention() > 0) {
                        task.setDoAdminMention(task.getDoAdminMention() - 1);
                        if (task.getDoAdminMention() == 0) {
                            logger.debug("Admin mention cool down has worn off for guild with ID" + task.getGuildID());
                        }
                    }
                }
            }
        }, 1000, 1000);
    }

    private void doEventTenSec() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (GuildContentObject task : Globals.getGuildContentObjects()) {
                    task.resetRateLimit();

                    //Mutes.
                    ArrayList<UserCountDown> mutedUsers = task.getGuildUsers().getMutedUsers();
                    for (int i = 0; i < mutedUsers.size(); i++) {
                        if (mutedUsers.get(i).getRemainderSecs() != -1) {
                            mutedUsers.get(i).tickDown(10);
                            if (mutedUsers.get(i).getRemainderSecs() == 0) {
                                task.getGuildUsers().unMuteUser(mutedUsers.get(i).getID(), task.getGuildID());
                            }
                        }
                    }
                }
            }
        }, 1000 * 10, 10 * 1000);
    }

    private static void doEventFiveMin(ZonedDateTime nowUTC) {
        while (!Globals.getClient().isReady()) ;
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
                        logger.info("Backup in 5 seconds do not restart.");
                    } else {
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
                    e.printStackTrace();
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
