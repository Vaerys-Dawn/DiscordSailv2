package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class GeneralLogging extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.GENERAL_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.generalLogging = !config.generalLogging;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.generalLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().generalLogging;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of general commands. Requires the " + ChannelSetting.SERVER_LOG.toString() + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
