package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.admin.Mute;
import com.github.vaerys.main.Utility;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleModMuting extends GuildModule {

    @Override
    public String name() {
        return "ModMute";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleModMute = !config.moduleModMute;
    }

    @Override
    public boolean get(GuildConfig config) {
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
    public String stats(CommandObject object) {
        if (!Utility.testForPerms(object, Permissions.MANAGE_SERVER)) return null;
        IRole muteRole = object.guild.getRoleByID(object.guild.config.getMutedRoleID());
        if (muteRole != null) {
            return "**Mute Role:** " + muteRole.getName();
        }
        return null;
    }
}
