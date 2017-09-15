package com.github.vaerys.oldcode;

import java.util.ArrayList;

public class OldGuildConfig {
    ArrayList<RoleTypeObject> cosmeticRoles = new ArrayList<>();
    ArrayList<RoleTypeObject> modifierRoles = new ArrayList<>();
    ArrayList<RoleTypeObject> trustedRoles = new ArrayList<>();
    RoleTypeObject roleToMention = new RoleTypeObject("null", -1);
    RoleTypeObject mutedRole = new RoleTypeObject("null", -1);

    public ArrayList<RoleTypeObject> getCosmeticRoles() {
        return cosmeticRoles;
    }

    public ArrayList<RoleTypeObject> getModifierRoles() {
        return modifierRoles;
    }

    public ArrayList<RoleTypeObject> getTrustedRoles() {
        return trustedRoles;
    }

    public RoleTypeObject getRoleToMention() {
        return roleToMention;
    }

    public RoleTypeObject getMutedRole() {
        return mutedRole;
    }
}
