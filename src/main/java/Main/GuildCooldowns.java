package Main;

import Objects.WaiterObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Vaerys on 14/08/2016.
 */
public class GuildCooldowns {

    public static int doAdminMention = 0;
    public static int serverCoolDown = 0;
    String guildID = "";
    ArrayList<WaiterObject> waiterObjects = new ArrayList<>();

    final static Logger logger = LoggerFactory.getLogger(GuildCooldowns.class);

    public GuildCooldowns(String guildID) {
        this.guildID = guildID;
        doCoolDownRemoval();
        waiterRemover();
    }

    public void addWaiter(String userID) {
        waiterObjects.add(new WaiterObject(userID));
    }

    public ArrayList<WaiterObject> getWaiterObjects() {
        return waiterObjects;
    }

    private void doCoolDownRemoval() {
        logger.debug("Cool downs initiated for guild with ID: " + guildID);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (doAdminMention > 0) {
                    doAdminMention--;
                    if (doAdminMention == 0) {
                        logger.debug("Admin mention cool down has worn off.");
                    }
                }
                if (serverCoolDown > 0) {
                    serverCoolDown--;
                    if (serverCoolDown == 0) {
                        logger.debug("Server cool down has worn off.");
                    }
                }

            }
        }, 1000, 1000);
    }

    private void waiterRemover() {
        logger.debug("Waiter Removal initiated for guild with ID: " + guildID);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0;i < waiterObjects.size();i++){
                    if (waiterObjects.get(i).removerCountdown > 0){
                        waiterObjects.get(i).removerCountdown--;
                        if (waiterObjects.get(i).removerCountdown == 0){
                            waiterObjects.remove(i);
                            logger.debug("User with ID: " + waiterObjects.get(i).getID() + " removed from waiting list.");
                        }
                    }
                }
            }
        },3000,3000);
    }
}
