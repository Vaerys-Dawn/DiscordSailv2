package com.github.vaerys.oldcode;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class RoleTypeObject {
    String RoleName;
    String RoleID;

    public RoleTypeObject(String roleName, String roleID) {
        RoleName = roleName;
        RoleID = roleID;
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
