package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.commands.general.LastDailyMessage;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
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
        return "Enables special messages that are sent once a day to your server's " + ChannelSetting.GENERAL.toString() + " messageChannel.";
    }

    @Override
    public void setup() {
        commands.add(LastDailyMessage.class);
    }

}
