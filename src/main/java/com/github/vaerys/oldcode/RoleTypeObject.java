package com.github.vaerys.oldcode;

/**
 * Created by Vaerys on 27/08/2016.
 */

@Deprecated
public class RoleTypeObject {
    String RoleName;
    String RoleID;

    public RoleTypeObject(String roleName, long roleID) {
        RoleName = roleName;
        RoleID = Long.toUnsignedString(roleID);
    }

    public String getRoleID() {
        return RoleID;
    }

    public void updateRoleName(String roleName) {
        RoleName = roleName;
    }

    public String getRoleName() {
        return RoleName;
    }
}
