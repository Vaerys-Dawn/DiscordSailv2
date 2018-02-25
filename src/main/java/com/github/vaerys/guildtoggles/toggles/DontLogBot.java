package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 09/04/2017.
 */
public class DontLogBot extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.DONT_LOG_BOT;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.dontLogBot = !config.dontLogBot;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.dontLogBot;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().dontLogBot;
    }

    @Override
    public String desc(CommandObject command) {
        return "Disables the logging of bot actions for the logging system.";
    }

    @Override
    public void setup() {

    }
}
