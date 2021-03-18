package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.help.GetGuildInfo;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;
import net.dv8tion.jda.api.entities.Role;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleRoles extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.ROLE_SELECT;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleRoles = !config.moduleRoles;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.moduleRoles;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleRoles;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module allows users to select their own roles from a select list via commands.\n\n" +
                "**Stats:**\n" +
                "To see the stats of this module you will need to run the **" + new GetGuildInfo().getCommand(command) + "** command.";
    }

    @Override
    public void setup() {

    }

    @Override
    public String stats(CommandObject command) {
        if (command.guild.config.getCosmeticRoleIDs().size() == 0 && command.guild.config.getModifierRoleIDs().size() == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        long totalUsers = 0;
        builder.append("**[COSMETIC ROLES]**" + Command.SPACER);
        for (long id : command.guild.config.getCosmeticRoleIDs()) {
            totalUsers += appendRole(builder, id, command);
        }
        builder.append("\n**Total:** " + totalUsers);
        totalUsers = 0;
        builder.append("\n<split>**[MODIFIER ROLES]**" + Command.SPACER);
        for (long id : command.guild.config.getModifierRoleIDs()) {
            totalUsers += appendRole(builder, id, command);
        }
        builder.append("\n**Total:** " + totalUsers);
        return builder.toString();
    }

    @Override
    public boolean statsOnInfo() {
        return false;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Allows users to pick their own roles.";
    }

    private long appendRole(StringBuilder builder, long id, CommandObject object) {
        Role role = object.guild.getRoleById(id);
        if (role != null) {
            String roleName = role.getName();
            if (role.isPublicRole()) roleName = "everyone";
            builder.append("\n**" + roleName + "** - " + object.guild.get().getMembersWithRoles(role).size());
            return object.guild.get().getMembersWithRoles(role).size();
        }
        return 0;
    }
}
