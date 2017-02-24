package Objects;

import Main.Globals;
import POGOs.GuildConfig;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class UserTypeObject {
    String displayName;
    String ID;
    float xp = 0;
    long level = 0;

    public UserTypeObject(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void addXP(GuildConfig config) {
        xp += 1;
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
}
