package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.Mute;
import com.github.vaerys.main.Utility;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;
import com.github.vaerys.enums.SAILType;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleModMuting extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.MOD_MUTE;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleModMute = !config.moduleModMute;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.moduleModMute;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleModMute;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module enables the **" + new Mute().getCommand(command) + "** command";
    }

    @Override
    public void setup() {
        commands.add(new Mute());
    }

    @Override
    public String stats(CommandObject command) {
        if (!Utility.testForPerms(command, Permissions.MANAGE_SERVER)) return null;
        IRole muteRole = command.guild.getRoleByID(command.guild.config.getMutedRoleID());
        if (muteRole != null) {
            return "**Mute Role:** " + muteRole.getName();
        }
        return null;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }
}
