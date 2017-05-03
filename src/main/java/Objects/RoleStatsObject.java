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
        super(role.getName(), role.getStringID());
        this.colour = "#" + Integer.toHexString(role.getColor().getRGB()).substring(2).toUpperCase();
        this.totalUsers = totalUsers;
        isCosmetic = guildConfig.isRoleCosmetic(role.getStringID());
        isModifier = guildConfig.isRoleModifier(role.getStringID());
        isTrusted = guildConfig.isRoleTrusted(role.getStringID());
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
