package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class UseTimeStamps extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.USE_TIME_STAMPS;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.useTimeStamps = !config.useTimeStamps;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.useTimeStamps;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().useTimeStamps;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Logging messages use fixed timestamp in place of time since.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Changes all logging messages to use timestamps based at UTC-00 instead of time since.";
    }

    @Override
    public void setup() {

    }
}
