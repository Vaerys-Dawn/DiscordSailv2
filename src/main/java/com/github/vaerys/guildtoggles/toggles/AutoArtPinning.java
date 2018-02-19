package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.templates.SAILType;

/**
 * Created by Vaerys on 13/05/2017.
 */
public class AutoArtPinning extends GuildSetting {
    @Override
    public SAILType name() {
        return SAILType.AUTO_ART_PINNING;
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
        return "Enables automatic pinning of images within the " + ChannelSetting.ART.toString() + "Channel.";
    }

    @Override
    public void setup() {

    }
}
