package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class DeleteLogging implements GuildToggle {

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
    public void execute(GuildObject guild) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
