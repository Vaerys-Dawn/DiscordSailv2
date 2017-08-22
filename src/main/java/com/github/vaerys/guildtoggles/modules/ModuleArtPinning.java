package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.guildtoggles.toggles.AutoArtPinning;
import com.github.vaerys.interfaces.Command;
import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 13/05/2017.
 */
public class ModuleArtPinning implements GuildToggle{
    @Override
    public String name() {
        return "ArtPinning";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.artPinning = !config.artPinning;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.artPinning;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().artPinning;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeChannel(Command.CHANNEL_ART);
        guild.removeToggle(new AutoArtPinning().name());
    }

    @Override
    public boolean isModule() {
        return true;
    }
}
