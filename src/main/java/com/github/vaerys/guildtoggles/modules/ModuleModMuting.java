package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.admin.PropMutePerms;
import com.github.vaerys.commands.admin.SetMutedRole;
import com.github.vaerys.commands.modtools.Mute;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.GuildHandler;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;
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
        return "This module enables the **" +
                Command.get(Mute.class).getCommand(command) +
                "** and **" + Command.get(PropMutePerms.class).getCommand(command) +
                "** commands";
    }

    @Override
    public void setup() {
        settings.add(SAILType.MUTE_REPEAT_OFFENDER);
        settings.add(SAILType.STOP_SPAM_WALLS);
        settings.add(SAILType.RATE_LIMITING);
        settings.add(SAILType.MENTION_SPAM);
        commands.add(Mute.class);
        commands.add(PropMutePerms.class);
        commands.add(SetMutedRole.class);
    }

    @Override
    public String stats(CommandObject object) {
        if (!GuildHandler.testForPerms(object, Permissions.MANAGE_SERVER)) return null;
        IRole muteRole = object.guild.getRoleByID(object.guild.config.getMutedRoleID());
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
