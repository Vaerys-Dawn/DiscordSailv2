package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.enums.SAILType;

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
    public String desc(CommandObject command) {
        return "Changes all logging messages to use timestamps based at UTC-00 instead of time since.";
    }

    @Override
    public void setup() {

    }
}
