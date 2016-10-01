package Main;

import Objects.GuildCoolDownObject;
import Objects.WaiterObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.IDiscordClient;


import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import Handlers.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.RateLimitException;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class TimedEvents {

    static ArrayList<GuildCoolDownObject> coolDownObjects = new ArrayList<>();
    static IDiscordClient client;

    final static Logger logger = LoggerFactory.getLogger(TimedEvents.class);

    public TimedEvents() {
//        final Image avatar = Image.forFile(new File("Storage/Images/avatar_for_day_7"));
//        try {
//            client.changeAvatar(avatar);
//            logger.info("avatar \"updated\".");
//        } catch (DiscordException e) {
//            e.printStackTrace();
//        } catch (RateLimitException e) {
//            e.printStackTrace();
//        }
        doCoolDownRemoval();
        waiterRemover();
        dailyTasks();
    }

    public static void addGuildCoolDown(String guildID) {
        coolDownObjects.add(new GuildCoolDownObject(guildID));
        logger.debug("Timed Events initiated for guild with ID: " + guildID);
    }

    public static int getServerCoolDown(String guildID) {
        for (GuildCoolDownObject g : coolDownObjects) {
            if (g.getGuildID().equals(guildID)) {
                return g.getServerCoolDown();
            }
        }
        return -1;
    }

    public static int getDoAdminMention(String guildID) {
        for (GuildCoolDownObject g : coolDownObjects) {
            if (g.getGuildID().equals(guildID)) {
                return g.getServerCoolDown();
            }
        }
        return -1;
    }

    public static void setDoAdminMention(String guildID, int i) {
        for (GuildCoolDownObject g : coolDownObjects) {
            if (g.getGuildID().equals(guildID)) {
                g.setDoAdminMention(i);
                return;
            }
        }
    }

    private static void dailyTasks() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId utc = ZoneId.of("UTC");
        ZonedDateTime zonedNow = ZonedDateTime.of(localDateTime, utc);
        ZonedDateTime nextZero;
        nextZero = zonedNow.withHour(0).withMinute(0).withSecond(0);
        if (zonedNow.compareTo(nextZero) > 0) {
            nextZero = nextZero.plusDays(1);
        }
        Duration duration = Duration.between(zonedNow, nextZero);
        logger.debug(duration.getSeconds() + " Seconds Till Daily task is run.");
        long initialDelay = duration.getSeconds() * 1000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logger.info("Running Daily Tasks");
                Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_WEEK);
                final Image avatar = Image.forFile(new File(Constants.DIRECTORY_GLOBAL_IMAGES + "avatarForDay_" + day + ".png"));
                Utility.updateAvatar(avatar);
                for (GuildCoolDownObject g : coolDownObjects) {
                    Utility.backupFile(g.getGuildID(), Constants.FILE_GUILD_CONFIG);
                    Utility.backupFile(g.getGuildID(), Constants.FILE_CUSTOM);
                    Utility.backupFile(g.getGuildID(), Constants.FILE_CHARACTERS);
                    Utility.backupFile(g.getGuildID(), Constants.FILE_SERVERS);
                    Utility.backupFile(g.getGuildID(), Constants.FILE_INFO);
                }
            }
        }, initialDelay, 24 * 60 * 60 * 1000);
    }

    public void addWaiter(String userID, String guildID) {
        for (GuildCoolDownObject g : coolDownObjects) {
            g.AddWaiterObject(new WaiterObject(userID));
        }
    }

    public ArrayList<WaiterObject> getWaiterObjects(String guildID) {
        for (GuildCoolDownObject g : coolDownObjects) {
            if (g.getGuildID().equals(guildID)) {
                return g.getWaiterObjects();
            }
        }
        return null;
    }

    private static void doCoolDownRemoval() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (GuildCoolDownObject g : coolDownObjects) {
                    if (g.getDoAdminMention() > 0) {
                        g.setDoAdminMention(g.getDoAdminMention() - 1);
                        if (g.getDoAdminMention() == 0) {
                            logger.debug("Admin mention cool down has worn off for guild with ID" + g.getGuildID());
                        }
                    }
                    if (g.getServerCoolDown() > 0) {
                        g.setServerCoolDown(g.getServerCoolDown() - 1);
                        if (g.getServerCoolDown() == 0) {
                            logger.debug("Server cool down has worn off for guild with ID" + g.getGuildID());
                        }
                    }
                }
            }
        }, 1000, 1000);
    }

    private static void waiterRemover() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (GuildCoolDownObject g : coolDownObjects) {
                    ArrayList<WaiterObject> waiterObjects = g.getWaiterObjects();
                    for (int i = 0; i < waiterObjects.size(); i++) {
                        if (waiterObjects.get(i).removerCountdown > 0) {
                            waiterObjects.get(i).removerCountdown--;
                            if (waiterObjects.get(i).removerCountdown == 0) {
                                waiterObjects.remove(i);
                                logger.debug("User with ID: " + waiterObjects.get(i).getID() + " removed from waiting list.");
                            }
                        }
                    }
                    g.setWaiterObjects(waiterObjects);
                }
            }
        }, 3000, 3000);
    }

}
