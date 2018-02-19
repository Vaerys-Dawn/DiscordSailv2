package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.guildtoggles.toggles.AdminLogging;
import com.github.vaerys.guildtoggles.toggles.ChannelLogging;
import com.github.vaerys.guildtoggles.toggles.DeleteLogging;
import com.github.vaerys.guildtoggles.toggles.DontLogBot;
import com.github.vaerys.guildtoggles.toggles.EditLogging;
import com.github.vaerys.guildtoggles.toggles.ExtendEditLog;
import com.github.vaerys.guildtoggles.toggles.GeneralLogging;
import com.github.vaerys.guildtoggles.toggles.JoinLeaveLogging;
import com.github.vaerys.guildtoggles.toggles.UseTimeStamps;
import com.github.vaerys.guildtoggles.toggles.UserRoleLogging;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.ChannelSetting;
import com.github.vaerys.templates.Command;
import com.github.vaerys.templates.GuildModule;
import com.github.vaerys.templates.SAILType;

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
        channels.add(ChannelSetting.ADMIN_LOG);
        channels.add(ChannelSetting.SERVER_LOG);
        channels.add(ChannelSetting.DONT_LOG);
    }

    @Override
    public String stats(CommandObject object) {
        return null;
    }
}