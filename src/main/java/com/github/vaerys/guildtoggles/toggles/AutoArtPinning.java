package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 13/05/2017.
 */
public class AutoArtPinning extends GuildSetting {
    @Override
    public String name() {
        return "AutoArtPinning";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.autoArtPinning = !config.autoArtPinning;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.autoArtPinning;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().autoArtPinning;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables automatic pinning of images within the " + Command.CHANNEL_ART + "Channel.";
    }

    @Override
    public void setup() {

    }
}
