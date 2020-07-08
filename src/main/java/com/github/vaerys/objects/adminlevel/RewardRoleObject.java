package com.github.vaerys.objects.adminlevel;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.handlers.PixelHandler;
import com.github.vaerys.masterobjects.GuildObject;
import sx.blah.discord.handle.obj.Guild;
import sx.blah.discord.handle.obj.Role;

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

    public Role get(GuildObject guild) {
        return guild.getRoleById(roleID);
    }

    public Role getRole(CommandObject object) {
        return object.guild.getRoleById(roleID);
    }

    public Role getRole(GuildObject object) {
        return object.getRoleById(roleID);
    }

    public Role getRole(Guild object) {
        return object.getRoleById(roleID);
    }
}
