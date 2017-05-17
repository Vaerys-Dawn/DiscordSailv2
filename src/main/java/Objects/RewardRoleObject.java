package Objects;

/**
 * Created by Vaerys on 11/05/2017.
 */
public class RewardRoleObject {
    long RoleID;
    int level;

    public RewardRoleObject(long roleID, int level) {
        RoleID = roleID;
        this.level = level;
    }

    public long getRoleID() {
        return RoleID;
    }

    public void setRoleID(long roleID) {
        RoleID = roleID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
