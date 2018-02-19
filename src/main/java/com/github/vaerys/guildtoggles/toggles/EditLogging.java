package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.templates.SAILType;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class EditLogging extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.EDIT_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.editLogging = !config.editLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.editLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().editLogging;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of edited messages. Requires the " + ChannelSetting.SERVER_LOG.toString() + " Channel to be set up.";
    }

    @Override
    public void setup() {
        settings.add(new ExtendEditLog());
    }
}
