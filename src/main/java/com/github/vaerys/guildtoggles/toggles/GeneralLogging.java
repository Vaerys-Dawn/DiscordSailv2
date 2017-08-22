package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class GeneralLogging implements GuildToggle {

    @Override
    public String name() {
        return "GeneralLogging";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.generalLogging = !config.generalLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.generalLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().generalLogging;
    }

    @Override
    public void execute(GuildObject guild) {

    }

    @Override
    public boolean isModule() {
        return false;
    }
}
