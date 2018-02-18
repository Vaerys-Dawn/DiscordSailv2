package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.channelsettings.settings.DontLog;
import com.github.vaerys.channelsettings.types.AdminLog;
import com.github.vaerys.channelsettings.types.ServerLog;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.guildtoggles.toggles.*;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;

public class ModuleLogging extends GuildModule {

    @Override
    public String name() {
        return Command.TYPE_LOGGING;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.moduleLogging = !config.moduleLogging;
    }

    @Override
    public boolean get(GuildConfig config) {
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
        settings.add(new AdminLogging());
        settings.add(new GeneralLogging());
        settings.add(new JoinLeaveLogging());
        settings.add(new ChannelLogging());
        settings.add(new DeleteLogging());
        settings.add(new EditLogging());
        settings.add(new DontLogBot());
        settings.add(new UserRoleLogging());
        settings.add(new UseTimeStamps());
        settings.add(new ExtendEditLog());
        channels.add(new AdminLog());
        channels.add(new ServerLog());
        channels.add(new DontLog());
    }

    @Override
    public String stats(CommandObject object) {
        return null;
    }
}