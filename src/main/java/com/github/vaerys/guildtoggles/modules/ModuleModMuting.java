package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.admin.Mute;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 02/03/2017.
 */
public class ModuleModMuting implements GuildToggle {
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
    public void execute(GuildObject guild) {
        guild.removeCommand(new Mute().names());
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
