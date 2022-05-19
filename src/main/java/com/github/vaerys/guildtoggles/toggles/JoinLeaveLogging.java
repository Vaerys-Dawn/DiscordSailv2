package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class JoinLeaveLogging extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.JOIN_LEAVE_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.joinLeaveLogging = !config.joinLeaveLogging;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.joinLeaveLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().joinLeaveLogging;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables logging of user joins/leaves.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of users joining and leaving. Requires the " + ChannelSetting.SERVER_LOG.toString() + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
