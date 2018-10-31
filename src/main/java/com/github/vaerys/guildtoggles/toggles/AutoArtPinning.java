package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

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
    public boolean enabled(GuildConfig config) {
        return config.autoArtPinning;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().autoArtPinning;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables automatic art pinning.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables automatic pinning of images within the " + ChannelSetting.ART.toString() + "Channel.";
    }

    @Override
    public void setup() {

    }
}
