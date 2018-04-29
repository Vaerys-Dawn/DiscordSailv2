package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 12/03/2017.
 */
public class ExtendEditLog extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.EXTEND_EDIT_LOG;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.extendEditLog = !config.extendEditLog;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.extendEditLog;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().extendEditLog;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }

    @Override
    public String desc(CommandObject command) {
        return "Extends the logging output of the " + new EditLogging().name() + " setting to also include new message.";
    }

    @Override
    public void setup() {

    }
}
