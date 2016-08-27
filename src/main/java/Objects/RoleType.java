package Objects;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class RoleType {
    String RoleName;
    String RoleID;

    public RoleType(String roleName, String roleID) {
        RoleName = roleName;
        RoleID = roleID;
    }

    public String getRoleID() {
        return RoleID;
    }

    public void updateRoleName(String roleName) {
        RoleName = roleName;
    }
}
