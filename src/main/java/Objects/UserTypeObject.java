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
    long xp = 0;
    long rewardID = -1;
//    String rewardRoleID = "";
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

    public long getRewardID() {
        return rewardID;
    }

    public void setRewardID(long rewardID) {
        this.rewardID = rewardID;
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

    public void setXp(long xp) {
        this.xp = xp;
    }

    public int getXP() {
        return Math.round(xp);
    }
}
