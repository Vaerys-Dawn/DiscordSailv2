package Objects;

import POGOs.GuildConfig;
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
        super(role.getName(), role.getID());
        this.colour = "#" + Integer.toHexString(role.getColor().getRGB()).substring(2).toUpperCase();
        this.totalUsers = totalUsers;
        isCosmetic = guildConfig.isRoleCosmetic(role.getID());
        isModifier = guildConfig.isRoleModifier(role.getID());
        isTrusted = guildConfig.isRoleTrusted(role.getID());
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
