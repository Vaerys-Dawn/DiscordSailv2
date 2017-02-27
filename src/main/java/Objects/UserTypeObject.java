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
    String gender = "Unknown";
    String quote = "This person doesn't seem to have much to say for themselves.";

    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public String getQuote() {
        if (quote == null){
            quote = "This person doesn't seem to have much to say for themselves.";
        }
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getGender() {
        if (gender == null){
            gender = "Unknown";
        }
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRewardRoleID() {
        return rewardRoleID;
    }

    public void setRewardRoleID(String rewardRoleID) {
        this.rewardRoleID = rewardRoleID;
    }

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
