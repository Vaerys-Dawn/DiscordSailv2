package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.admin.DenyXpPrefix;
import com.github.vaerys.guildtoggles.toggles.ReactToLevelUp;
import com.github.vaerys.guildtoggles.toggles.SelfDestructLevelUps;
import com.github.vaerys.guildtoggles.toggles.XpDecay;
import com.github.vaerys.guildtoggles.toggles.XpGain;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 04/07/2017.
 */
public class ModulePixels implements GuildToggle {
    @Override
    public String name() {
        return "Pixels";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.modulePixels = !config.modulePixels;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.modulePixels;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().modulePixels;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeCommandsByType(Command.TYPE_PIXEL);
        guild.removeCommand(new DenyXpPrefix().names());
        guild.removeToggle(new XpDecay().name());
        guild.removeToggle(new XpGain().name());
        guild.removeToggle(new SelfDestructLevelUps().name());
        guild.removeToggle(new ReactToLevelUp().name());
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
