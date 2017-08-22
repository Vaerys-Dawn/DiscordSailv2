package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.interfaces.GuildToggle;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class EditLogging implements GuildToggle {

    @Override
    public String name() {
        return "EditLogging";
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
    public void execute(GuildObject guild) {
        guild.removeToggle(new ExtendEditLog().name());
    }

    @Override
    public boolean isModule() {
        return false;
    }
}
