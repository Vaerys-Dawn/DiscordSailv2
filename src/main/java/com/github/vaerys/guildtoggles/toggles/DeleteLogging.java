package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.enums.SAILType;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class DeleteLogging extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.DELETE_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.deleteLogging = !config.deleteLogging;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.deleteLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().deleteLogging;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of deleted messages. Requires the " + ChannelSetting.SERVER_LOG.toString() + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
