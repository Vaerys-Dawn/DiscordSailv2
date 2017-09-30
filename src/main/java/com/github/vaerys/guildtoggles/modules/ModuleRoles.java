package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.roleSelect.CosmeticRoles;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildModule;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;
import sx.blah.discord.handle.obj.IRole;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleRoles implements GuildModule {

    @Override
    public String name() {
        return "Roles";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleRoles = !config.moduleRoles;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.moduleRoles;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleRoles;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeCommandsByType(Command.TYPE_ROLE_SELECT);
    }

    @Override
    public String stats(CommandObject object) {
        if (object.guild.config.getCosmeticRoleIDs().size() == 0 && object.guild.config.getModifierRoleIDs().size() == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        long totalUsers = 0;
        builder.append("**[COSMETIC ROLES]**" + Command.spacer);
        for (long id : object.guild.config.getCosmeticRoleIDs()) {
            totalUsers += appendRole(builder, id, object);
        }
        builder.append("**\nTotal Users:** " + totalUsers);
        totalUsers = 0;
        builder.append("\n<split>**[MODIFIER ROLES]**" + Command.spacer);
        for (long id : object.guild.config.getModifierRoleIDs()) {
            totalUsers += appendRole(builder, id, object);
        }
        builder.append("**\nTotal Users:** " + totalUsers);
        return builder.toString();
    }

    private long appendRole(StringBuilder builder, long id, CommandObject object) {
        IRole role = object.guild.get().getRoleByID(id);
        if (role != null) {
            String roleName = role.getName();
            if (role.isEveryoneRole()) roleName = "everyone";
            builder.append("\n**" + roleName + "** - " + object.guild.get().getUsersByRole(role).size());
            return object.guild.get().getUsersByRole(role).size();
        }
        return 0;
    }
}