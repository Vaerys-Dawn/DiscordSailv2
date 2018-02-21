package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.enums.SAILType;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class JoinLeaveLogging extends GuildSetting{

    @Override
    public SAILType name() {
        return SAILType.JOIN_LEAVE_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.joinLeaveLogging = !config.joinLeaveLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.joinLeaveLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().joinLeaveLogging;
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of users joining and leaving. Requires the " + ChannelSetting.SERVER_LOG.toString() + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
