package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class MuteRepeatOffender extends GuildSetting {

    @Override
    public String name() {
        return "MuteRepeatOffender";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.muteRepeatOffenders = !config.muteRepeatOffenders;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.muteRepeatOffenders;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().muteRepeatOffenders;
    }

    @Override
    public String desc(CommandObject command) {
        return "If the " + new MentionSpam().name() + " setting is enabled, after 3 times of triggering the feature it will automatically mute the user.";
    }

    @Override
    public void setup() {

    }
}
