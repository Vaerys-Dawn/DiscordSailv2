package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 21/02/2017.
 */
public class JoinLeaveLogging extends GuildSetting{

    @Override
    public String name() {
        return "JoinLeaveLogging";
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
        return "Enables the logging of users joining and leaving. Requires the " + Command.CHANNEL_SERVER_LOG + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
