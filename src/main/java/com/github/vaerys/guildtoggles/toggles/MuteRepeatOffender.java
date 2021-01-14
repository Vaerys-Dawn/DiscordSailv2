package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class MuteRepeatOffender extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.MUTE_REPEAT_OFFENDER;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.muteRepeatOffenders = !config.muteRepeatOffenders;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.muteRepeatOffenders;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().muteRepeatOffenders;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables muting of users when " + new MentionSpam().name() + " is also enabled";
    }

    @Override
    public String desc(CommandObject command) {
        return "If the " + new MentionSpam().name() + " setting is enabled, after 3 times of triggering the feature it will automatically mute the globalUser.";
    }

    @Override
    public void setup() {

    }
}
