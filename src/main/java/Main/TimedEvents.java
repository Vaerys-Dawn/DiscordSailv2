package Main;

import Objects.DailyMessageObject;
import Objects.ReminderObject;
import Objects.TimedObject;
import Objects.WaiterObject;
import POGOs.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.Image;

import java.io.File;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class TimedEvents {

    static ArrayList<TimedObject> TimerObjects = new ArrayList<>();
    static IDiscordClient client;

    final static Logger logger = LoggerFactory.getLogger(TimedEvents.class);

    public TimedEvents() {
        doRemovalOneSec();
        doRemovalThreeSec();
        doRemovalMin();
        doTaskFiveMin();
        dailyTasks();
    }

    public static void addGuildCoolDown(String guildID) {
        for (TimedObject t : TimerObjects) {
            if (t.getGuildID().equals(guildID)) {
                return;
            }
        }
        TimerObjects.add(new TimedObject(guildID));
        logger.debug("Timed Events initiated for guild with ID: " + guildID);
    }

    public static int getDoAdminMention(String guildID) {
        for (TimedObject g : TimerObjects) {
            if (g.getGuildID().equals(guildID)) {
                return g.getDoAdminMention();
            }
        }
        return -1;
    }

    public static void setDoAdminMention(String guildID, int i) {
        for (TimedObject g : TimerObjects) {
            if (g.getGuildID().equals(guildID)) {
                g.setDoAdminMention(i);
                return;
            }
        }
    }

    private static void dailyTasks() {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime midnightUTC = ZonedDateTime.now(ZoneOffset.UTC);
        midnightUTC = midnightUTC.withHour(0).withSecond(0).withMinute(0).withNano(0).plusDays(1);
        long initialDelay = midnightUTC.toEpochSecond() - nowUTC.toEpochSecond() + 4;
        logger.debug("Now UTC = " + Utility.formatTimeSeconds(nowUTC.toEpochSecond()));
        logger.debug("Midnight UTC = " + Utility.formatTimeSeconds(midnightUTC.toEpochSecond()));
        logger.debug("Delay = " + Utility.formatTimeSeconds(initialDelay));
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ZonedDateTime timeNow = ZonedDateTime.now(ZoneOffset.UTC);
                String dailyFileName = Globals.dailyAvatarName.replace("#day#",timeNow.getDayOfWeek().toString());
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

                //backups
                Utility.backupConfigFile(Constants.FILE_CONFIG);
                for (TimedObject g : TimerObjects) {
                    Utility.backupFile(g.getGuildID(), Constants.FILE_GUILD_CONFIG);
                    Utility.backupFile(g.getGuildID(), Constants.FILE_CUSTOM);
                    Utility.backupFile(g.getGuildID(), Constants.FILE_CHARACTERS);
                    Utility.backupFile(g.getGuildID(), Constants.FILE_SERVERS);
                    Utility.backupFile(g.getGuildID(), Constants.FILE_INFO);
                    Utility.backupFile(g.getGuildID(), Constants.FILE_COMPETITION);
                    GuildConfig guildConfig = Globals.getGuildContent(g.getGuildID()).getGuildConfig();

                    //daily messages
                    if (guildConfig.getChannelTypeID(Constants.CHANNEL_GENERAL) != null) {
                        if (guildConfig.doDailyMessage()) {
                            IChannel channel = Globals.getClient().getChannelByID(guildConfig.getChannelTypeID(Constants.CHANNEL_GENERAL));
                            for (DailyMessageObject d : Globals.dailyMessages) {
                                if (day.equals(d.getDayOfWeek())) {
                                    if (timeNow.getDayOfMonth() == 25 && timeNow.getMonth().equals(Month.DECEMBER)) {
                                        Utility.sendMessage("> ***MERRY CHRISTMAS***", channel);
                                    } else if (timeNow.getDayOfMonth() == 1 && timeNow.getMonth().equals(Month.JANUARY)) {
                                        Utility.sendMessage("> ***HAPPY NEW YEAR***", channel);
                                    } else {
                                        Utility.sendMessage(d.getContents(), channel);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, initialDelay * 1000, 24 * 60 * 60 * 1000);
    }

    public static void addWaiter(String userID, String guildID) {
        for (TimedObject g : TimerObjects) {
            if (g.getGuildID().equals(guildID)) {
                g.mannageWaiter(userID);
            }
        }
    }

    public static ArrayList<WaiterObject> getWaiterObjects(String guildID) {
        for (TimedObject g : TimerObjects) {
            if (g.getGuildID().equals(guildID)) {
                return g.getWaiterObjects();
            }
        }
        return null;
    }


    public static boolean addReminder(String guildID, String userID, String channelID, long timeMins, String message) {
        for (TimedObject g : TimerObjects) {
            if (g.getGuildID().equals(guildID)) {
                return g.addReminderObject(new ReminderObject(userID, channelID, message, timeMins));
            }
        }
        return true;
    }

    private static void doRemovalOneSec() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (TimedObject task : TimerObjects) {
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

    private static void doRemovalThreeSec() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (TimedObject task : TimerObjects) {
                    ArrayList<WaiterObject> waiterObjects = task.getWaiterObjects();
                    for (int i = 0; i < waiterObjects.size(); i++) {
                        if (waiterObjects.get(i).getRemainderSecs() > 0) {
                            waiterObjects.get(i).setRemainderSecs(waiterObjects.get(i).getRemainderSecs() - 1);
                            if (waiterObjects.get(i).getRemainderSecs() == 0) {
                                waiterObjects.remove(i);
                                logger.debug("User with ID: " + waiterObjects.get(i).getID() + " removed from waiting list.");
                            }
                        }
                    }
                    task.setWaiterObjects(waiterObjects);
                }
            }
        }, 3000, 3000);
    }

    private static void doRemovalMin() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (TimedObject task : TimerObjects) {
                    ArrayList<ReminderObject> reminderObjects = task.getReminderObjects();
                    for (int i = 0; i < reminderObjects.size(); i++) {
                        if (reminderObjects.get(i).getTimeMins() > 0) {
                            reminderObjects.get(i).setTimeMins(reminderObjects.get(i).getTimeMins() - 1);
                            if (reminderObjects.get(i).getTimeMins() == 0) {
                                ReminderObject reminder = reminderObjects.get(i);
                                IChannel channel = Globals.getClient().getChannelByID(reminder.getChannelID());
                                String reminderMessage;
                                reminderMessage = Globals.getClient().getUserByID(reminder.getUserID()).mention() + " " + reminder.getMessage();
                                reminderMessage = TagSystem.tagMentionRemover(reminderMessage);
                                Utility.sendMessage(reminderMessage, channel);
                                logger.debug("User with ID: " + reminderObjects.get(i).getUserID() + " Sending reminder");
                                reminderObjects.remove(i);
                            }
                        }
                    }
                    task.setReminderObjects(reminderObjects);
                }
            }
        }, 3000, 60000);
    }

    private static void doTaskFiveMin() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Globals.saveFiles();
            }
        }, 2* 60 * 1000, 5 * 60 * 1000);
    }
}
