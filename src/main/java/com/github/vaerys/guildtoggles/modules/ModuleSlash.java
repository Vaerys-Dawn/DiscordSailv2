package com.github.vaerys.guildtoggles.modules;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.pogos.GuildConfig;
import com.github.vaerys.templates.GuildModule;
import com.github.vaerys.enums.SAILType;

/**
 * Created by Vaerys on 13/03/2017.
 */
public class ModuleSlash extends GuildModule {

    @Override
    public SAILType name() {
        return SAILType.SLASH;
    }

    @Override
    public boolean toggle(GuildConfig config) {
        return config.slashCommands = !config.slashCommands;
    }

    @Override
    public boolean get(GuildConfig config) {
        return config.slashCommands;
    }

    @Override
    public boolean getDefault() {
        return new GuildConfig().slashCommands;
    }

    @Override
    public String desc(CommandObject command) {
        return "This module enables Slash commands.";
    }

    @Override
    public void setup() {
    }

    @Override
    public String stats(CommandObject object) {
        return null;
    }
}
