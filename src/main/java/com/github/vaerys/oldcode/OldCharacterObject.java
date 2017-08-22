package com.github.vaerys.oldcode;

import java.util.ArrayList;

public class OldCharacterObject {
    String name = "null"; //Character's Name
    ArrayList<RoleTypeObject> roles = new ArrayList<>();

    public ArrayList<RoleTypeObject> getRoles() {
        return roles;
    }

    public String getName() {
        return name;
    }
}
