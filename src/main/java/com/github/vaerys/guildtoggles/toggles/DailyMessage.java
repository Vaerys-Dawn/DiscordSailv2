package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.general.LastDailyMessage;
import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.interfaces.GuildSetting;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.pogos.GuildConfig;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class DailyMessage implements GuildSetting {

    @Override
    public String name() {
        return "DailyMessage";
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.dailyMessage = !config.dailyMessage;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.dailyMessage;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().dailyMessage;
    }

    @Override
    public void execute(GuildObject guild) {
        guild.removeCommand(new LastDailyMessage().names());
    }

}
