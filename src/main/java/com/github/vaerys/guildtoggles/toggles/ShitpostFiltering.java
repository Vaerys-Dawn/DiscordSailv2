package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.templates.SAILType;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class ShitpostFiltering extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.SHITPOST_FILTERING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.shitPostFiltering = !config.shitPostFiltering;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.shitPostFiltering;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().shitPostFiltering;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the locking the usage of Custom commands tagged with shitpost to " + ChannelSetting.SHITPOST.toString() + " channels.";
    }

    @Override
    public void setup() {

    }
}
