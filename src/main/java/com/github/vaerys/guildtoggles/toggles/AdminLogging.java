package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class AdminLogging extends GuildSetting {

    @Override
    public String name() {
        return "AdminLogging";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.adminLogging = !config.adminLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.adminLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().adminLogging;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of admin commands. Requires the " + Command.CHANNEL_ADMIN_LOG + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
