package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class AdminLogging implements GuildToggle {

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
    public void execute(GuildObject guild) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
