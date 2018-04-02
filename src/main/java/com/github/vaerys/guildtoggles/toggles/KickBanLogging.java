package com.github.vaerys.guildtoggles.toggles;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildSetting;
import com.github.vaerys.templates.GuildToggle;

public class KickBanLogging extends GuildSetting {
    @Override
    public SAILType name() {
        return SAILType.KICK_BAN_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.kickBanLogging = !config.kickBanLogging;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.kickBanLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().kickBanLogging;
    }

    @Override
    public String desc(CommandObject command) {
        return "Toggles whether or not kicks and bans are logged by SAIL.\nKick logging requires that " + new JoinLeaveLogging().name() + " is also enabled.";
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Kick and Ban logging.";
    }

    @Override
    public void setup() {

    }
}
