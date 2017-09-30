package com.github.vaerys.objects;

import com.github.vaerys.oldcode.RoleTypeObject;
import com.github.vaerys.pogos.GuildConfig;
import sx.blah.discord.handle.obj.IRole;

/**
 * Created by Vaerys on 28/11/2016.
 */
public class RoleStatsObject extends RoleTypeObject {

    String colour;
    int totalUsers;
    boolean isCosmetic;
    boolean isModifier;
    boolean isTrusted;

    public RoleStatsObject(IRole role, GuildConfig guildConfig, int totalUsers) {
        super(role.getName(), role.getLongID());
        this.colour = "#" + Integer.toHexString(role.getColor().getRGB()).toUpperCase();
        this.totalUsers = totalUsers;
        isCosmetic = guildConfig.isRoleCosmetic(role.getLongID());
        isModifier = guildConfig.isRoleModifier(role.getLongID());
        isTrusted = guildConfig.isRoleTrusted(role.getLongID());
    }


    public String getColour() {
        return colour;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public boolean isCosmetic() {
        return isCosmetic;
    }

    public boolean isModifier() {
        return isModifier;
    }

    public boolean isTrusted() {
        return isTrusted;
    }
}
