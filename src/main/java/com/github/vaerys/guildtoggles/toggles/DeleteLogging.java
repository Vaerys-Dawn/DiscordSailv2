package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class DeleteLogging extends GuildSetting {

    @Override
    public String name() {
        return "DeleteLogging";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.deleteLogging = !config.deleteLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.deleteLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().deleteLogging;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of deleted messages. Requires the " + Command.CHANNEL_SERVER_LOG + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
