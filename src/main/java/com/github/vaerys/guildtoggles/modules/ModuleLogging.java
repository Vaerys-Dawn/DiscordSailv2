package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;

public class ModuleLogging extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleLogging = !config.moduleLogging;
    }

    @Override
    public boolean enabled(GuildConfig config) {
        return config.moduleLogging;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().moduleLogging;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module allows for logging of various actions that are performed on the server.";
    }

    @Override
    public void setup() {
        settings.add(SAILType.ADMIN_LOGGING);
        settings.add(SAILType.GENERAL_LOGGING);
        settings.add(SAILType.JOIN_LEAVE_LOGGING);
        settings.add(SAILType.CHANNEL_LOGGING);
        settings.add(SAILType.DELETE_LOGGING);
        settings.add(SAILType.EDIT_LOGGING);
        settings.add(SAILType.DONT_LOG_BOT);
        settings.add(SAILType.USER_ROLE_LOGGING);
        settings.add(SAILType.USE_TIME_STAMPS);
        settings.add(SAILType.EXTEND_EDIT_LOG);
        settings.add(SAILType.CHECK_NEW_USERS);
        settings.add(SAILType.BAN_LOGGING);
        settings.add(SAILType.KICK_LOGGING);
        channels.add(ChannelSetting.ADMIN_LOG);
        channels.add(ChannelSetting.SERVER_LOG);
        channels.add(ChannelSetting.DONT_LOG);
    }

    @Override
    public String stats(CommandObject object) {
        return null;
    }

    @Override
    public String shortDesc(CommandObject command) {
        return "Handles various additional logging features.";
    }
}
