package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;

/**
 * Created by Vaerys on 20/02/2017.
 */
public class BanLogging extends GuildSetting {

    @Override
    public SAILType name() {
        return SAILType.BAN_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.banLogging = !config.banLogging;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.banLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().banLogging;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Enables ban logging.";
    }

    @Override
    public String desc(CommandObject command) {
        return "Enables the logging of bans. Requires the " + ChannelSetting.ADMIN_LOG.toString() + " Channel to be set up.";
    }

    @Override
    public void setup() {

    }
}
