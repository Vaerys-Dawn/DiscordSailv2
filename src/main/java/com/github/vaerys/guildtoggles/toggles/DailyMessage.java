package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.general.LastDailyMessage;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class DailyMessage extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.DAILY_MESSAGE;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.dailyMessage = !config.dailyMessage;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.dailyMessage;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().dailyMessage;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return desc(command);
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables Daily Messages.";
    }

    @Override
    public void setup() {
        commands.add(new LastDailyMessage());
    }

}
