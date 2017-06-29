package Objects;

import Enums.UserSetting;
import Handlers.MessageHandler;
import Main.Globals;
import POGOs.GuildConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class UserTypeObject {
    String ID;
    long xp = 0;
    long rewardID = -1;
    String gender = "Unknown";
    String quote = "This person doesn't seem to have much to say for themselves.";
    ArrayList<UserSetting> settings = new ArrayList<>();
    ArrayList<UserLinkObject> links = new ArrayList<>();
    public long lastTalked = -1;


    public UserTypeObject() {
        if (links == null) links = new ArrayList<>();
        if (settings == null) settings = new ArrayList<>();
    }

    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public String getQuote() {
        if (quote == null) {
            quote = "This person doesn't seem to have much to say for themselves.";
        }
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getGender() {
        if (gender == null) {
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
        xp += 20;
        logger.trace(Globals.getClient().getUserByID(ID) + " - Xp gained");
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public long getXP() {
        return xp;
    }

    public ArrayList<UserSetting> getSettings() {
        return settings;
    }

    public ArrayList<UserLinkObject> getLinks() {
        return links;
    }

    public void setLinks() {
        if (links == null) {
            links = new ArrayList<>();
        }
    }

    public long getLastTalked() {
        return lastTalked;
    }
}
