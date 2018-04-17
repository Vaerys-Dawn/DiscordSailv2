package com.github.vaerys.objects;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.masterobjects.GuildObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

/**
 * Created by Vaerys on 11/05/2017.
 */
public class RewardRoleObject {
    long roleID;
    long level;

    public RewardRoleObject(long roleID, long level) {
        this.roleID = roleID;
        this.level = level;
    }

    public long getRoleID() {
        return roleID;
    }

    public void setRoleID(long roleID) {
        this.roleID = roleID;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public long getXp() {
        return PixelHandler.totalXPForLevel(level);
    }

    public IRole get(GuildObject guild) {
        return guild.getRoleByID(roleID);
    }

    public IRole getRole(CommandObject object) {
        return object.guild.getRoleByID(roleID);
    }

    public IRole getRole(GuildObject object) {
        return object.getRoleByID(roleID);
    }

    public IRole getRole(IGuild object) {
        return object.getRoleByID(roleID);
    }
}
