package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.GetGuildInfo;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;
import sx.blah.discord.handle.obj.IRole;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleRoles extends GuildModule {

    @Override
    public String name() {
        return Command.TYPE_ROLE_SELECT;
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
    public String desc(CommandObject command) {
        return "This module allows users to select their own roles from a select list via commands.\n\n" +
                "**Stats:**\n" +
                "To see the stats of this module you will need to run the **" + new GetGuildInfo().getCommand(command) + "** command.";
    }

    @Override
    public void setup() {
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
        builder.append("**\nTotal:** " + totalUsers);
        totalUsers = 0;
        builder.append("\n<split>**[MODIFIER ROLES]**" + Command.spacer);
        for (long id : object.guild.config.getModifierRoleIDs()) {
            totalUsers += appendRole(builder, id, object);
        }
        builder.append("**\nTotal:** " + totalUsers);
        return builder.toString();
    }

    @Override
    public boolean statsOnInfo() {
        return false;
    }

    private long appendRole(StringBuilder builder, long id, CommandObject object) {
        IRole role = object.guild.getRoleByID(id);
        if (role != null) {
            String roleName = role.getName();
            if (role.isEveryoneRole()) roleName = "everyone";
            builder.append("\n**" + roleName + "** - " + object.guild.get().getUsersByRole(role).size());
            return object.guild.get().getUsersByRole(role).size();
        }
        return 0;
    }
}