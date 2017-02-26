package Objects;

import Handlers.MessageHandler;
import Main.Globals;
import POGOs.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class UserTypeObject {
    String ID;
    float xp = 0;
    long level = 0;
    String rewardRoleID = "";

    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private float XP;

    public UserTypeObject(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void addXP(GuildConfig config) {
        xp += 1;
        logger.trace(Globals.getClient().getUserByID(ID)+ " - Xp gained");
    }

    public void addLevel(){
        level += 1;
    }

    public void setXp(float xp) {
        this.xp = xp;
        calculateLevel();
    }

    private void calculateLevel() {
        int xpForLvlOne = Globals.xpForLevelOne;
        int baseModifier = Globals.baseXPModifier;

    }

    public long getLevel() {
        return level;
    }

    public int getXP() {
        return Math.round(xp);
    }
}
