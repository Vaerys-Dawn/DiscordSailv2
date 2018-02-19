package com.github.vaerys.objects;


import sx.blah.discord.handle.obj.Permissions;

public class SubCommandObject {

    private final String name[];
    private final String usage;
    private final String description;
    private final String type;
    private final Permissions[] permissions;

    public SubCommandObject(String[] name, String usage, String description, String type, Permissions... permissions) {
        this.name = name;
        this.usage = usage;
        this.description = description;
        this.type = type;
        this.permissions = permissions;
    }

    public String[] getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public Permissions[] getPermissions() {
        return permissions;
    }
}
